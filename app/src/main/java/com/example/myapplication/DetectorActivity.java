package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
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
   // private static final Size DESIRED_PREVIEW_SIZE = new Size(1280, 720);
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

    public int cropW = 0;
    public int cropH = 0;

    public Bitmap onebone = null; //크롭을 위한 원본이미지

    //google cloud vision api===================
    public int MAX_DIMENSION = 1200;
    public static ArrayList<String> nut_list = new ArrayList<String>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLOUD_VISION_API_KEY ="AIzaSyCvzYj8F337WKnyVREMx3aXGo7YYEdwhdQ";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";

    public boolean api_call = false;
    //==========================================


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

        cropW = (int) (targetW / 2.0); // 자를 사진 크기 (가로)
        cropH = (int) (targetH / 2.0); // 자를 사진 크기 (세로)

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
                                float location_right = location.right;
                                float location_bottom = location.bottom;
                                float location_width = location.width();
                                float location_height = location.height();

                                System.out.println("location 값 : "+location);

                                System.out.println("width값 : "+location_width);
                                System.out.println("height값 : "+location_height);

                                //System.out.println(result);
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
                                    Double four = Double.parseDouble(four_s);
                                    */

                                    float confidence = result.getConfidence();


                                    crop_result_list = new Detector.Recognition( //여기서 getCrop()하면 됨
                                            "0", "NutritionFactsLabel",confidence,location);


                                    //Bitmap crop_image =
                                      //      byteArrayToBitmap(cropImage_(cropCopyBitmap,cropCopyBitmap, location));

                                    //Bitmap crop_image = getCroppedBitmap_circle(cropCopyBitmap);
                                    /*
                                    카메라 프레임 내에서 글자인식 진행
                                    Bitmap crop_rect = Bitmap.createBitmap(cropCopyBitmap,
                                            (int)location_left*2,
                                            (int)location_top*2,
                                            (int)location_width*1,
                                            (int)location_height*1);


                                    if(api_call == false) {
                                        cloudText_recognize(crop_rect);
                                        //cloudText_recognize(cropCopyBitmap);
                                    }

                                    if (nut_list.size() != 0){
                                        Intent n_intent = new Intent(DetectorActivity.this, NutritionLabelsActivity.class);
                                        // text 분석한 내용 넘겨주기
                                        n_intent.putExtra("strings", nut_list);
                                        startActivity(n_intent);
                                    }
                                   else{
                                       break;
                                    }

                                     */

                                  //  Bitmap crop_image = getCroppedBitmap_circle(cropCopyBitmap);
                                   Bitmap crop_image = cropBitmap_testing(cropCopyBitmap);//이게찐임
                                  //  Bitmap crop_image =  getCroppedBitmap(cropCopyBitmap,location);
                                 //   Bitmap crop_image =  getCroppedBitmap(cropCopyBitmap);
                                    //Bitmap crop_image = cropImage(cropCopyBitmap,location); //2찐
                                    //Bitmap crop_image = cropImage2(cropCopyBitmap,location);
                                    //Bitmap crop_image = cropImageRect(cropCopyBitmap,location);

                                    crop_result_list.setCrop(crop_image);


                                }catch (Exception err){
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
                Intent intent = new Intent(DetectorActivity.this, CropActivity.class);
                //name 넘겨주고, image 넘겨주기
                intent.putExtra("pass_image",pass_image);
                startActivity(intent);
                //*********************************************
            }
        });
        builder.setView(dialogLayout);
        builder.show();

    }



    public Bitmap cropBitmap_testing(Bitmap original) {


        //original.getWidth() : 384
        //original.getHeight() : 384
        System.out.println("previewWidth : "+previewWidth);
        System.out.println("previewHeight : "+previewHeight);

        System.out.println("original.getWidth() : "+original.getWidth());
        System.out.println("original.getHeight() : "+original.getHeight());

        Bitmap result = Bitmap.createBitmap(original
                , original.getWidth()/4// 128 X 시작위치 (원본의 4/1지점)
                , original.getHeight()/4 //128  Y 시작위치 (원본의 4/1지점)
                , original.getWidth()/2 //192 넓이 (원본의 절반 크기) 86original.getWidth()/2
                , original.getHeight()/2); //192 높이 (원본의 절반 크기)125original.getHeight()/2

        if (result != original) {
            original.recycle();
        }
        return result;
    } //찐

    public Bitmap getCroppedBitmap(Bitmap bitmap,RectF loc) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
       // final RectF rectf = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final Rect rect = new Rect(0,0,bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawRect(loc.left,loc.top,loc.right,loc.bottom,paint);
             //   bitmap.getWidth() / 2,
               // bitmap.getHeight() / 2,
                //bitmap.getWidth() / 2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.drawBitmap(bitmap,rect,rect,paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    public Bitmap cropImage(Bitmap bitmap, RectF reference){ //길이 따져서해보기
        int heightOriginal = previewHeight; //카메라 전체 화면 크기
        int widthOriginal = previewWidth;

        int heightFrame = (int) reference.height(); //사각형 높이
        int widthFrame = (int) reference.width(); //사각형 가로
        int leftFrame = (int) reference.left; //
        int topFrame = (int) reference.top;

        int heightReal = bitmap.getHeight();
        int widthReal = bitmap.getWidth();

        int widthFinal = (widthFrame * widthReal)/widthOriginal;
        int heightFinal = (heightFrame * heightReal)/heightOriginal;
        int leftFinal = (leftFrame * widthReal)/widthOriginal;
        int topFinal = (topFrame * heightReal)/heightOriginal;

        Bitmap bitmapfinal = Bitmap.createBitmap(bitmap,
                leftFinal, topFinal, widthFinal, heightFinal);
        System.out.println(" leftFinal : "+leftFinal);
        System.out.println(" topFinal : "+topFinal);
        System.out.println(" widthFinal : "+widthFinal);
        System.out.println(" heightFinal : "+heightFinal);
        System.out.println("previewWidth : "+previewWidth);
        System.out.println("previewHeight : "+previewHeight);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapfinal.compress(Bitmap.CompressFormat.JPEG, 100, stream);//100 is the best quality possibe
       // return stream.toByteArray();
        return bitmapfinal;
    }
    public Bitmap cropImageRect(Bitmap original, RectF rectangle){
        float needWidth = 384; // 384
        float needHeight = 384; // 384

        int height_rect = (int) rectangle.height(); //사각형 높이
        int width_rect = (int) rectangle.width(); //사각형 가로

        int left_rect = (int) rectangle.left; //188
        int top_rect = (int) rectangle.top;
        int right_rect = (int) rectangle.right;
        int bottom_rect = (int) rectangle.bottom;


        float biyul_left = rectangle.left/needWidth;
        float biyul_right = rectangle.right/needWidth;
        float biyul_top = rectangle.top/needHeight;
        float biyul_height = rectangle.bottom/needHeight;

        System.out.println("biyul_right : "+biyul_right); //
        System.out.println("biyul_height : "+biyul_height); //
        System.out.println("biyul_left : "+biyul_left); //
        System.out.println("biyul_top : "+biyul_top); //


        int leftFinal = (int)(needWidth*biyul_left);
        int topFinal = (int)(needHeight*biyul_top);
        int widthFinal = width_rect;
        int heightFinal = height_rect;

        System.out.println("******rectangle : "+rectangle);
        System.out.println(" leftFinal : "+leftFinal); //
        System.out.println(" topFinal : "+topFinal); //
        System.out.println(" widthFinal : "+widthFinal); //140
        System.out.println(" heightFinal : "+heightFinal); //308

        Bitmap thisiscropimage = Bitmap.createBitmap(
                original,
                leftFinal,
                topFinal,
                widthFinal,
                heightFinal
        );

        return thisiscropimage;
    }

   // private byte[] cropImage_(Bitmap bitmap, View frame, View reference) {
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

    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
    }


 //-------구글 클라우드 비전 -------------------------------------------------------
    public void cloudText_recognize(Bitmap inputBitmap){

        Bitmap bitmap =
                scaleBitmapDown(
                        inputBitmap,
                        MAX_DIMENSION);
        callCloudVision(bitmap);
        api_call = true;

    }


    private Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {

        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    private void callCloudVision(final Bitmap bitmap) {
        // Do the real work in an async task, because we need to use the network anyway
        try {
            AsyncTask<Object, Void, String> labelDetectionTask = new DetectorActivity.LableDetectionTask(this, prepareAnnotationRequest(bitmap));
            labelDetectionTask.execute();
            api_call = true;
        } catch (IOException e) {
            Log.d(TAG, "failed to make API request because of other IOException " +
                    e.getMessage());
        }
    }

    private Vision.Images.Annotate prepareAnnotationRequest(Bitmap bitmap) throws IOException {
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer =
                new VisionRequestInitializer(CLOUD_VISION_API_KEY) {
                    /**
                     * We override this so we can inject important identifying fields into the HTTP
                     * headers. This enables use of a restricted cloud platform API key.
                     */
                    @Override
                    protected void initializeVisionRequest(VisionRequest<?> visionRequest)
                            throws IOException {
                        super.initializeVisionRequest(visionRequest);

                        String packageName = getPackageName();
                        visionRequest.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                        String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                        visionRequest.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);
                    }
                };

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                new BatchAnnotateImagesRequest();
        batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
            AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();

            // Add the image
            Image base64EncodedImage = new Image();
            // Convert the bitmap to a JPEG
            // Just in case it's a format that Android understands but Cloud Vision
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64 encode the JPEG
            base64EncodedImage.encodeContent(imageBytes);
            annotateImageRequest.setImage(base64EncodedImage);

            // add the features we want
            annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                Feature textDetection = new Feature();
                textDetection.setType("DOCUMENT_TEXT_DETECTION");
                // textDetection.setType("TEXT_DETECTION");
                textDetection.setMaxResults(10);
                add(textDetection);
            }});

            // Add the list of one thing to the request
            add(annotateImageRequest);
        }});

        Vision.Images.Annotate annotateRequest =
                vision.images().annotate(batchAnnotateImagesRequest);
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotateRequest.setDisableGZipContent(true);
        Log.d(TAG, "created Cloud Vision request object, sending request");

        return annotateRequest;
    }

    private static class LableDetectionTask extends AsyncTask<Object, Void, String> {
        private final WeakReference<DetectorActivity> mActivityWeakReference;
        private Vision.Images.Annotate mRequest;

        LableDetectionTask(DetectorActivity activity, Vision.Images.Annotate annotate) {
            mActivityWeakReference = new WeakReference<>(activity);
            mRequest = annotate;
        }

        @Override
        protected String doInBackground(Object... params) {
            try {
                Log.d(TAG, "created Cloud Vision request object, sending request");
                BatchAnnotateImagesResponse response = mRequest.execute();
                return convertResponseToString(response);

            } catch (GoogleJsonResponseException e) {
                Log.d(TAG, "failed to make API request because " + e.getContent());
            } catch (IOException e) {
                Log.d(TAG, "failed to make API request because of other IOException " +
                        e.getMessage());
            }
            return "Cloud Vision API request failed. Check logs for details.";
        }

        protected void onPostExecute(String result) {
            DetectorActivity activity = mActivityWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
               // TextView imageDetail = activity.findViewById(R.id.image_details);
               // imageDetail.setText(result);
            }
        }
    }

    private static String convertResponseToString(BatchAnnotateImagesResponse response) {

        //StringBuilder message = new StringBuilder("I found these things:\n\n");
        String message = "I found these things:\n\n";
        List<EntityAnnotation> labels = response.getResponses().get(0).getTextAnnotations(); //text로 받아와줌
        if (labels != null) {
            //for (EntityAnnotation label : labels) {
            //      message.append(String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription()));
            //    message.append("\n");
            message  = labels.get(0).getDescription();
            System.out.println(message);
            //리스트 만들어서 글자 추출한 것
            nut_list.add(message);




        } else {
            message="nothing";
        }

        // return message.toString();
        System.out.println(nut_list);
        return message;
    }

}
