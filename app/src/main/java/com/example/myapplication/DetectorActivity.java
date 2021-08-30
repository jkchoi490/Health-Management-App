package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
    private static final Logger LOGGER = new Logger();

    // Configuration values for the prepackaged SSD model.
    private static final int TF_OD_API_INPUT_SIZE = 384; //모델 사이즈에 맞게 변경해주기
    //  private static final int TF_OD_API_INPUT_SIZE = 300;

    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    //private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_MODEL_FILE = "model.tflite"; //모델이름 변경
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

    //^_^V ******----------------------여기부터---------------------------------------------------
    Bitmap pass_image = null; //잘라서 액티비티에 넘길 이미지
    private FloatingActionButton fabAdd; // 카메라 촬영 버튼
    private boolean addPending = false;
    public Detector.Recognition crop_result_list;

    private Bitmap portraitBmp = null;
    private Bitmap faceBmp = null;

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
        showAddFaceDialog(crop_result_list);
        //Toast.makeText(this, "click", Toast.LENGTH_LONG ).show();


    }

    //^_^V ******----------------여기까지 손댐---------------------------------------------------------

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
        croppedBitmap = Bitmap.createBitmap(cropSize, cropSize, Config.ARGB_8888); //<--원래 이거였음
//////------------------------------
        int targetW, targetH; //자르려는 가로,세로 = 모델적용한 부분 가로 세로로 지정
        if (sensorOrientation == 90 || sensorOrientation == 270) {
            targetH = previewWidth;  //자르려는 가로,세로 = 모델적용한 부분 가로 세로로 지정
            targetW = previewHeight;
        }
        else {
            targetW = previewWidth;
            targetH = previewHeight;
        }
        int cropW = (int) (targetW / 2.0); // 자를 사진 크기 (가로)
        int cropH = (int) (targetH / 2.0); // 자를 사진 크기 (세로)

        //   croppedBitmap = Bitmap.createBitmap(cropW, cropH, Config.ARGB_8888); //사진 자르기

        portraitBmp = Bitmap.createBitmap(targetW, targetH, Config.ARGB_8888); //모델 적용한 원본 자른부분

        faceBmp = Bitmap.createBitmap(TF_OD_API_INPUT_SIZE, TF_OD_API_INPUT_SIZE, Config.ARGB_8888);

//////-----------------------------------------
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
//---위까지-동일------
        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        LOGGER.i("Running detection on image " + currTimestamp);
                        final long startTime = SystemClock.uptimeMillis();
                        final List<Detector.Recognition> results = detector.recognizeImage(croppedBitmap);
                        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

                        cropCopyBitmap = Bitmap.createBitmap(croppedBitmap); //croppedBitmap 복사
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
                                float location_width = location.width();
                                float location_height = location.height();

                                System.out.println("location 값 : "+location);
                                System.out.println("top값 : "+location_top);
                                System.out.println("left값 : "+location_left);
                                System.out.println("width값 : "+location_width);
                                System.out.println("height값 : "+location_height);

                                //System.out.println("result안에 뭐가있나좀보자 염병 : "+result);
                                //mappedRecognitions : [[0] NutritionFactsLabel (70.3%) RectF(373.3333, 46.25, 536.6666, 425.0)]
                                try {
                                    /*
                                    String str_rec = mappedRecognitions.get(0).toString();
                                    String substr = str_rec.substring(str_rec.indexOf("RectF("));
                                    String[] array_substr = substr.split(",");

                                    String one_s = array_substr[0].substring(array_substr[0].lastIndexOf("(")+1);
                                    String two_s = array_substr[1].replaceAll(" ", "");
                                    String three_s = array_substr[2].replaceAll(" ", "");
                                    String four_s = array_substr[3].substring(0, array_substr[3].lastIndexOf(")")).replaceAll(" ", "");


                                    Double one = Double.parseDouble(one_s);
                                    Double two = Double.parseDouble(two_s);
                                    Double three = Double.parseDouble(three_s);
                                    Double four = Double.parseDouble(four_s);*/

                                    float confidence = result.getConfidence();


                                    crop_result_list = new Detector.Recognition( //여기서 getCrop()하면 됨
                                            "0", "NutritionFactsLabel",confidence,location);

                                    // Bitmap crop_image = keep_sibal(cropCopyBitmap,mappedRecognitions);
                                    // Bitmap crop_image = crop_testing(location);
                                    //Bitmap crop_image = cropNutritionFactsLabel(cropCopyBitmap,location); //이미지 잘라내기!
                                    Bitmap crop_image = cropBitmap_testing(cropCopyBitmap);//여거
                                    // Bitmap crop_image = help_crop(cropCopyBitmap, location);
                                    crop_result_list.setCrop(crop_image);

                                    /*
                                    Paint paint_for_crop = new Paint();
                                    paint_for_crop.setFilterBitmap(true);
                                    int targetWidth = previewWidth;
                                    int targetHeight = previewHeight;

                                    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);

                                    Matrix matrix = new Matrix();
                                    matrix.postScale(1f, 1f);
                                    Bitmap resizedBitmap = Bitmap.createBitmap(
                                            targetBitmap,
                                            one,
                                            two,
                                            three,
                                            four);

                                    Bitmap bd = resizedBitmap;
                                    pass_image = bd;

                                    */
                                }catch (Exception err){
                                    System.out.println("이미지 자르기 실패ㅠㅠ");
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

    //=------------------------------------------------------
    public Bitmap cropNutritionFactsLabel(Bitmap source, RectF cropRectF){

        Bitmap resultBitmap = Bitmap.createBitmap(portraitBmp, //추출해낸 부분 넣기
                (int) cropRectF.left,
                (int) cropRectF.top,
                (int) cropRectF.width(),
                (int) cropRectF.height());



        Canvas cavas = new Canvas(resultBitmap);
        // draw background 배경에 그리기 -> 카메라 프레임에 그리기로 변경
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);
        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);

        cavas.drawBitmap(source, matrix, paint);

        return resultBitmap;


    }

    // Face Processing
    private Matrix createTransform(
            final int srcWidth,
            final int srcHeight,
            final int dstWidth,
            final int dstHeight,
            final int applyRotation) {

        Matrix matrix = new Matrix();
        if (applyRotation != 0) {
            //  if (applyRotation % 90 != 0) {
            //    LOGGER.w("Rotation of %d % 90 != 0", applyRotation);
            //  }
            // Translate so center of image is at origin.
            matrix.postTranslate(-srcWidth / 2.0f, -srcHeight / 2.0f);

            // Rotate around origin.
            matrix.postRotate(applyRotation);
        }

        if (applyRotation != 0) {

            //  Translate back from origin centered reference to destination frame.
            matrix.postTranslate(dstWidth / 2.0f, dstHeight / 2.0f);
        }

        return matrix;

    }

    private void showAddFaceDialog(Detector.Recognition rec) { //자른부분 dilog로 저장하기--------

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
        ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image); //자른 영역------------------------
        TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
        EditText etName = dialogLayout.findViewById(R.id.dlg_input); //자른부분 이미지 이름 적기------------

        tvTitle.setText("영양성분표 추출");
        ivFace.setImageBitmap(rec.getCrop()); //자른부분 보여주기-- 이부분 넘기기***************--------------
        etName.setHint("이름을 입력하세요");

        pass_image = rec.getCrop(); //넘겨줄 이미지

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dlg, int i) {

                String name = etName.getText().toString(); //작성한 식품명
                if (name.isEmpty()) {
                    return;
                }
                detector.register(name, rec);
                //knownFaces.put(name, rec);
                dlg.dismiss();

                //내가짠코드*************************************
                Intent intent = new Intent(DetectorActivity.this, PassDataActivity.class);
                //name 넘겨주고, image 넘겨주기
                intent.putExtra("pass_image",pass_image);
                startActivity(intent);
                //*********************************************
            }
        });
        builder.setView(dialogLayout);
        builder.show();

    }

    public Bitmap crop_testing(RectF cropRectF){
        Bitmap resultBitmap = null;


        int sourceW = rgbFrameBitmap.getWidth(); //처음이미지
        int sourceH = rgbFrameBitmap.getHeight();
        int targetW = portraitBmp.getWidth();  //추출해내려는 부분 가로
        int targetH = portraitBmp.getHeight(); //추출해내려는 부분 세로
        Matrix transform = createTransform(
                sourceW,
                sourceH,
                targetW,
                targetH,
                sensorOrientation);
        final Canvas cv = new Canvas(portraitBmp);
        //세로 모드에서 원본 이미지를 그립니다. (draws the original image in portrait mode.)
        cv.drawBitmap(rgbFrameBitmap, transform, null);

        final Canvas cvFace = new Canvas(faceBmp);
        cropToFrameTransform.mapRect(cropRectF);
        // 원래 좌표를 세로 좌표에 매핑 (maps original coordinates to portrait coordinates)
        RectF faceBB = new RectF(cropRectF);
        transform.mapRect(faceBB);

        // 세로를 원점으로 변환하고 입력 추론 크기에 맞게 크기를 조정합니다.(translates portrait to origin and scales to fit input inference size)
        //cv.drawRect(faceBB, paint);
        float sx = ((float) TF_OD_API_INPUT_SIZE) / faceBB.width();
        float sy = ((float) TF_OD_API_INPUT_SIZE) / faceBB.height();
        Matrix matrix = new Matrix();
        matrix.postTranslate(-faceBB.left, -faceBB.top);
        matrix.postScale(sx, sy);

        cvFace.drawBitmap(portraitBmp, matrix, null); //추출해낸 부분 그리기

        resultBitmap = Bitmap.createBitmap(portraitBmp, //추출해낸 부분 넣기
                (int) faceBB.left,
                (int) faceBB.top,
                (int) faceBB.width(),
                (int) faceBB.height());


        return resultBitmap;
    }

    public Bitmap cropBitmap_testing(Bitmap original) {

        Bitmap result = Bitmap.createBitmap(original
                , original.getWidth() / 3//X 시작위치 (원본의 4/1지점)
                , original.getHeight() / 3 //Y 시작위치 (원본의 4/1지점)
                , original.getWidth() / 2 // 넓이 (원본의 절반 크기)
                , original.getHeight() / 2); // 높이 (원본의 절반 크기)
        if (result != original) {
            original.recycle();
        }
        return result;
    } //찐


    public Bitmap keep_sibal(Bitmap original, List<Detector.Recognition> mappedRecognitions){

        String str_rec = mappedRecognitions.get(0).toString();
        String substr = str_rec.substring(str_rec.indexOf("RectF("));
        String[] array_substr = substr.split(",");

        String one_s = array_substr[0].substring(array_substr[0].lastIndexOf("(")+1);
        String two_s = array_substr[1].replaceAll(" ", "");
        String three_s = array_substr[2].replaceAll(" ", "");
        String four_s = array_substr[3].substring(0, array_substr[3].lastIndexOf(")")).replaceAll(" ", "");


        Double b_left = new Double(Double.parseDouble(one_s));
        Double b_top = new Double(Double.parseDouble(two_s));
        Double b_width = new Double(Double.parseDouble(three_s));
        Double b_height = new Double(Double.parseDouble(four_s));


        int boxing_left = b_left.intValue();
        int boxing_top = b_top.intValue();
        int boxing_width = b_width.intValue();
        int boxing_height = b_height.intValue();

        int box_left = boxing_left;
        int box_top = boxing_top;
        int box_width = boxing_width;
        int box_height = boxing_height;

        Bitmap result = Bitmap.createBitmap(original
                , box_left*1//X 시작위치 (원본의 4/1지점)
                , box_top*1 //Y 시작위치 (원본의 4/1지점)
                , box_width * 1 // 넓이 (원본의 절반 크기)
                , box_height*1); // 높이 (원본의 절반 크기)
        if (result != original) {
            original.recycle();
        }
        return result;
    }


    public Bitmap help_crop(Bitmap original, RectF rect){

        //calculate aspect ratio
        float koefX = (float) previewWidth/ (float) original.getWidth();
        float koefY = (float) previewHeight/ (float) original.getHeight();

        //get viewfinder border size and position on the screen
        int x1 = (int) rect.left;
        int y1 = (int) rect.top;

        int x2 = (int) rect.width();
        int y2 = (int) rect.height();

        //calculate position and size for cropping
        int cropStartX = Math.round(x1 * koefX);
        int cropStartY = Math.round(y1 * koefY);

        int cropWidthX = Math.round(x2 * koefX);
        int cropHeightY = Math.round(y2 * koefY);


        Bitmap result = Bitmap.createBitmap(original
                , x1*1//X 시작위치
                , y1*1 //Y 시작위치
                , x2*2 // 넓이
                , y2*2); // 높이
        if (result != original) {
            original.recycle();
        }
        return result;
    }


}
