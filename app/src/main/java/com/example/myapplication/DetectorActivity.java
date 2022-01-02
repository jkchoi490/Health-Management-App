package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.myapplication.customview.OverlayView;
import com.example.myapplication.customview.OverlayView.DrawCallback;
import com.example.myapplication.env.BorderedText;
import com.example.myapplication.env.ImageUtils;
import com.example.myapplication.env.Logger;
import com.example.myapplication.tflite.Detector;
import com.example.myapplication.tflite.TFLiteObjectDetectionAPIModel;
import com.example.myapplication.tracking.MultiBoxTracker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 384; //모델 사이즈

    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "model.tflite";
    private static final String TF_OD_API_LABELS_FILE = "labelmap.txt";
    private static final DetectorMode MODE = DetectorMode.TF_OD_API;
    // Minimum detection confidence to track a detection.
    private static final float MINIMUM_CONFIDENCE_TF_OD_API = 0.5f;
    private static final boolean MAINTAIN_ASPECT = false;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);


    private static final boolean SAVE_PREVIEW_BITMAP = false;
    private static final float TEXT_SIZE_DIP = 10;
    OverlayView trackingOverlay;
    private Integer sensorOrientation;

    private Detector detector;

    private long lastProcessingTimeMs;
    private Bitmap rgbFrameBitmap = null;
    private Bitmap croppedBitmap = null;
    private Bitmap cropCopyBitmap = null;

    private boolean computingDetection = false;

    private long timestamp = 0;

    private Matrix frameToCropTransform;
    private Matrix cropToFrameTransform;

    private MultiBoxTracker tracker;

    private BorderedText borderedText;

    Bitmap pass_image = null; //잘라서 액티비티에 넘길 이미지
    private FloatingActionButton fabAdd; // 카메라 촬영 버튼
    private boolean addPending = false;
    public Detector.Recognition crop_result_list;

    private Bitmap portraitBmp = null;
    private Bitmap fBmp = null;

    public int cropW = 0;
    public int cropH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddClick();
            }
        });

    }


    private void onAddClick() {
        addPending = true;
        click_and_passCrop(crop_result_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPreviewSizeChosen(final Size size, final int rotation) {
        final float textSizePx =
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
        borderedText = new BorderedText(textSizePx);
        borderedText.setTypeface(Typeface.MONOSPACE);

        tracker = new MultiBoxTracker(this);

        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector =
                    TFLiteObjectDetectionAPIModel.create(
                            this,
                            TF_OD_API_MODEL_FILE,
                            TF_OD_API_LABELS_FILE, //파일에 NutritionFactsLabel을 넣어준다
                            TF_OD_API_INPUT_SIZE,
                            TF_OD_API_IS_QUANTIZED);
            cropSize = TF_OD_API_INPUT_SIZE;
        } catch (final IOException e) {
            e.printStackTrace();
            LOGGER.e(e, "Exception initializing Detector!");
            Toast toast =
                    Toast.makeText(
                            getApplicationContext(), "Detector could not be initialized", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        previewWidth = size.getWidth();
        previewHeight = size.getHeight();

        sensorOrientation = rotation - getScreenOrientation();
        LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

        LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
        rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888);

        int targetW, targetH; //자르려는 가로,세로 = 모델적용한 부분 가로 세로로 지정
        if (sensorOrientation == 90 || sensorOrientation == 270) {
            targetH = previewWidth;  //자르려는 가로,세로 = 모델적용한 부분 가로 세로로 지정
            targetW = previewHeight;
        } else {
            targetW = previewWidth;
            targetH = previewHeight;
        }

        cropW = (int) (targetW / 2.0); // 자를 사진 크기 (가로)
        cropH = (int) (targetH / 2.0); // 자를 사진 크기 (세로)

        //   croppedBitmap = Bitmap.createBitmap(cropW, cropH, Config.ARGB_8888); //사진 자르기

        portraitBmp = Bitmap.createBitmap(targetW, targetH, Config.ARGB_8888); //모델 적용한 원본 자른부분

        fBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Config.ARGB_8888);

        frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        previewWidth, previewHeight,
                        // cropW, cropH,
                        cropSize, cropSize,
                        sensorOrientation, MAINTAIN_ASPECT);

        cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        trackingOverlay = (OverlayView) findViewById(R.id.tracking_overlay);
        trackingOverlay.addCallback(
                new DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        tracker.draw(canvas);
                        if (isDebug()) {
                            tracker.drawDebug(canvas);
                        }
                    }
                });

        tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
    }

    @Override
    protected void processImage() {
        ++timestamp;
        final long currTimestamp = timestamp;
        trackingOverlay.postInvalidate();

        // No mutex needed as this method is not reentrant.
        if (computingDetection) {
            readyForNextImage();
            return;
        }
        computingDetection = true;
        LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");


        rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

        readyForNextImage();

        final Canvas canvas = new Canvas(croppedBitmap);
        canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
        // For examining the actual TF input.
        if (SAVE_PREVIEW_BITMAP) {
            ImageUtils.saveBitmap(croppedBitmap);
        }

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);
                        final Canvas canvas = new Canvas(cropCopyBitmap); //canvas 생성
                        final Paint paint = new Paint(); //그리기 위한 paint 생성
                        paint.setColor(Color.RED); //페인트 컬러, 스타일 등등 설정
                        paint.setStyle(Style.STROKE);
                        paint.setStrokeWidth(2.0f);

                        float minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                        switch (MODE) {
                            case TF_OD_API:
                                minimumConfidence = MINIMUM_CONFIDENCE_TF_OD_API;
                                break;
                        }

                        final List<Detector.Recognition> mappedRecognitions =
                                new ArrayList<Detector.Recognition>(); //Detector 객체담을 리스트 생성

                        for (final Detector.Recognition result : results) {
                            final RectF location = result.getLocation(); //사각형 생성(객체탐지 위한)
                            if (location != null && result.getConfidence() >= minimumConfidence) { //사각형이 있고 정확도가 최소를 넘으면
                                canvas.drawRect(location, paint); //사각형 그림

                                cropToFrameTransform.mapRect(location);
                                result.setLocation(location);
                                mappedRecognitions.add(result);


                                float location_top = location.top;
                                float location_left = location.left;
                                float location_right = location.right;
                                float location_bottom = location.bottom;
                                float location_width = location.width();
                                float location_height = location.height();

                                System.out.println("location 값 : " + location);

                                System.out.println("width값 : " + location_width);
                                System.out.println("height값 : " + location_height);

                                //System.out.println(result);
                                //mappedRecognitions : [[0] NutritionFactsLabel (70.3%) RectF(373.3333, 46.25, 536.6666, 425.0)]
                                try {


                                    float confidence = result.getConfidence();


                                    crop_result_list = new Detector.Recognition(
                                            "0", "NutritionFactsLabel", confidence, location);
                                    Bitmap crop_image = cropBitmap_testing(cropCopyBitmap);
                                    crop_result_list.setCrop(crop_image);


                                } catch (Exception err) {
                                    System.out.println("Fail image crop");
                                }

                            }
                        }

                        tracker.trackResults(mappedRecognitions, currTimestamp);
                        trackingOverlay.postInvalidate();

                        computingDetection = false;

                        runOnUiThread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        showFrameInfo(previewWidth + "x" + previewHeight);
                                        showCropInfo(cropCopyBitmap.getWidth() + "x" + cropCopyBitmap.getHeight());
                                        showInference(lastProcessingTimeMs + "ms");
                                    }
                                });
                    }
                });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.tfe_od_camera_connection_fragment_tracking;
    }

    @Override
    protected Size getDesiredPreviewFrameSize() {
        return DESIRED_PREVIEW_SIZE;
    }

    // Which detection model to use: by default uses Tensorflow Object Detection API frozen
    // checkpoints.
    private enum DetectorMode {
        TF_OD_API;
    }

    @Override
    protected void setUseNNAPI(final boolean isChecked) {
        runInBackground(
                () -> {
                    try {
                        detector.setUseNNAPI(isChecked);
                    } catch (UnsupportedOperationException e) {
                        LOGGER.e(e, "Failed to set \"Use NNAPI\".");
                        runOnUiThread(
                                () -> {
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                });
    }

    @Override
    protected void setNumThreads(final int numThreads) {
        runInBackground(() -> detector.setNumThreads(numThreads));
    }


    public void click_and_passCrop(Detector.Recognition rec) {
        pass_image = rec.getCrop(); //넘겨줄 이미지

        Intent intent = new Intent(DetectorActivity.this, CropActivity.class);
        //image 넘겨주기
        intent.putExtra("pass_image", pass_image);
        startActivity(intent);

    }

    public Bitmap cropBitmap_testing(Bitmap original) {
        //original.getWidth() : 384
        //original.getHeight() : 384
        System.out.println("previewWidth : " + previewWidth);
        System.out.println("previewHeight : " + previewHeight);

        System.out.println("original.getWidth() : " + original.getWidth());
        System.out.println("original.getHeight() : " + original.getHeight());

        Bitmap result = Bitmap.createBitmap(original
                , original.getWidth() / 3
                , original.getHeight() / 3
                , original.getWidth() / 2
                , original.getHeight() / 2);

        if (result != original) {
            original.recycle();
        }
        return result;
    } //Crop Method

//---------------------------------------------------------------------------------------------
    public Bitmap getCroppedBitmap(Bitmap bitmap, RectF loc) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        // final RectF rectf = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(loc.left, loc.top, loc.right, loc.bottom, paint);
        //   bitmap.getWidth() / 2,
        // bitmap.getHeight() / 2,
        //bitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Bitmap cropImage(Bitmap bitmap, RectF reference) { //길이 따져서해보기
        int heightOriginal = previewHeight; //카메라 전체 화면 크기
        int widthOriginal = previewWidth;

        int heightFrame = (int) reference.height(); //사각형 높이
        int widthFrame = (int) reference.width(); //사각형 가로
        int leftFrame = (int) reference.left; //
        int topFrame = (int) reference.top;

        int heightReal = bitmap.getHeight();
        int widthReal = bitmap.getWidth();

        int widthFinal = (widthFrame * widthReal) / widthOriginal;
        int heightFinal = (heightFrame * heightReal) / heightOriginal;
        int leftFinal = (leftFrame * widthReal) / widthOriginal;
        int topFinal = (topFrame * heightReal) / heightOriginal;

        Bitmap bitmapfinal = Bitmap.createBitmap(bitmap,
                leftFinal, topFinal, widthFinal, heightFinal);
        System.out.println(" leftFinal : " + leftFinal);
        System.out.println(" topFinal : " + topFinal);
        System.out.println(" widthFinal : " + widthFinal);
        System.out.println(" heightFinal : " + heightFinal);
        System.out.println("previewWidth : " + previewWidth);
        System.out.println("previewHeight : " + previewHeight);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapfinal.compress(Bitmap.CompressFormat.JPEG, 100, stream);//100 is the best quality possibe
        // return stream.toByteArray();
        return bitmapfinal;
    }

    public Bitmap cropImageRect(Bitmap original, RectF rectangle) {
        float needWidth = 384; // 384
        float needHeight = 384; // 384

        int height_rect = (int) rectangle.height(); //사각형 높이
        int width_rect = (int) rectangle.width(); //사각형 가로

        int left_rect = (int) rectangle.left; //188
        int top_rect = (int) rectangle.top;
        int right_rect = (int) rectangle.right;
        int bottom_rect = (int) rectangle.bottom;


        float biyul_left = rectangle.left / needWidth;
        float biyul_right = rectangle.right / needWidth;
        float biyul_top = rectangle.top / needHeight;
        float biyul_height = rectangle.bottom / needHeight;

        System.out.println("biyul_right : " + biyul_right); //
        System.out.println("biyul_height : " + biyul_height); //
        System.out.println("biyul_left : " + biyul_left); //
        System.out.println("biyul_top : " + biyul_top); //


        int leftFinal = (int) (needWidth * biyul_left);
        int topFinal = (int) (needHeight * biyul_top);
        int widthFinal = width_rect;
        int heightFinal = height_rect;

        System.out.println("******rectangle : " + rectangle);
        System.out.println(" leftFinal : " + leftFinal); //
        System.out.println(" topFinal : " + topFinal); //
        System.out.println(" widthFinal : " + widthFinal); //140
        System.out.println(" heightFinal : " + heightFinal); //308

        Bitmap thisiscropimage = Bitmap.createBitmap(
                original,
                leftFinal,
                topFinal,
                widthFinal,
                heightFinal
        );

        return thisiscropimage;
    }

    private byte[] cropImage_(Bitmap bitmap, View frame, RectF reference) {
        float heightOriginal = frame.getHeight();
        float widthOriginal = frame.getWidth();

        float heightFrame = reference.height(); //getHeight();
        float widthFrame = reference.height(); //getWidth();
        float leftFrame = reference.left;//getLeft();
        float topFrame = reference.top;//getTop();
        float heightReal = bitmap.getHeight();
        float widthReal = bitmap.getWidth();

        float widthFinal = widthFrame * widthReal / widthOriginal;
        float heightFinal = heightFrame * heightReal / heightOriginal;
        float leftFinal = leftFrame * widthReal / widthOriginal;
        float topFinal = topFrame * heightReal / heightOriginal;

        Bitmap bitmapFinal = Bitmap.createBitmap(
                bitmap,
                (int) leftFinal,
                (int) topFinal,
                (int) widthFinal,
                (int) heightFinal);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFinal.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                stream
        ); //100 is the best quality possibe
        return stream.toByteArray();
    }

    public Bitmap byteArrayToBitmap(byte[] $byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length);
        return bitmap;
    }


}