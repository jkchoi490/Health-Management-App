package com.example.myapplication.tflite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Trace;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions;

/**
 * Wrapper for frozen detection models trained using the Tensorflow Object Detection API: -
 * https://github.com/tensorflow/models/tree/master/research/object_detection where you can find the
 * training code.
 *
 * <p>To use pretrained models in the API or convert to TF Lite models, please see docs for details:
 * -
 * https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf1_detection_zoo.md
 * -
 * https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf2_detection_zoo.md
 * -
 * https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/running_on_mobile_tensorflowlite.md#running-our-model-on-android
 *
 * <p>For more information about Metadata and associated fields (eg: `labels.txt`), see <a
 * href="https://www.tensorflow.org/lite/convert/metadata#read_the_metadata_from_models">Read the
 * metadata from models</a>
 */

public class TFLiteObjectDetectionAPIModel implements Detector {
    private static final String TAG = "TFLiteObjectDetectionAPIModelWithTaskApi";

    /** Only return this many results. */
    private static final int NUM_DETECTIONS = 10;

    private final MappedByteBuffer modelBuffer;
    public Interpreter tfLite;
    public int inputSize;
    // public AbstractSequentialList<E> labels;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    public ObjectDetector objectDetector;

    /** Builder of the options used to config the ObjectDetector. */
    private final ObjectDetectorOptions.Builder optionsBuilder;

    //추가됨
    private static final Object TF_OD_API_INPUT_SIZE = 384; //이거 추가됨
    private Matrix cropToFrameTransform;
    public RectF boundingBox;
    ///--------------------------------------------------------




    /**
     * Initializes a native TensorFlow session for classifying images.
     *
     * <p>{@code labelFilename}, {@code inputSize}, and {@code isQuantized}, are NOT required, but to
     * keep consistency with the implementation using the TFLite Interpreter Java API. See <a
     * href="https://github.com/tensorflow/examples/blob/master/lite/examples/object_detection/android/lib_interpreter/src/main/java/org/tensorflow/lite/examples/detection/tflite/TFLiteObjectDetectionAPIModel.java">lib_interpreter</a>.
     *
     * @param modelFilename The model file path relative to the assets folder
     * @param labelFilename The label file path relative to the assets folder
     * @param inputSize The size of image input
     * @param isQuantized Boolean representing model is quantized or not
     */

    private HashMap<String, Recognition> registered = new HashMap<>();
    public void register(String name, Recognition rec) {
        registered.put(name, rec);
    }



    public static Detector create(
            final Context context,
            final String modelFilename,
            final String labelFilename,
            final int inputSize,
            final boolean isQuantized)
            throws IOException {
        return new TFLiteObjectDetectionAPIModel(context, modelFilename);
    }

    private TFLiteObjectDetectionAPIModel(Context context, String modelFilename) throws IOException {
        modelBuffer = FileUtil.loadMappedFile(context, modelFilename);
        optionsBuilder = ObjectDetectorOptions.builder().setMaxResults(NUM_DETECTIONS);
        objectDetector = ObjectDetector.createFromBufferAndOptions(modelBuffer, optionsBuilder.build());
    }








    @Override
    public List<Recognition> recognizeImage(final Bitmap bitmap) {
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");
        List<Detection> results = objectDetector.detect(TensorImage.fromBitmap(bitmap));

        // Converts a list of {@link Detection} objects into a list of {@link Recognition} objects
        // to match the interface of other inference method, such as using the <a
        // href="https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection/android/lib_interpreter">TFLite
        // Java API.</a>.

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int cnt = 0;
        for (Detection detection : results) {
            recognitions.add(
                    new Recognition(
                            "" + cnt++,
                            detection.getCategories().get(0).getLabel(),
                            detection.getCategories().get(0).getScore(),
                            detection.getBoundingBox()
                    ));

            //~~~~~~~~~~~~계속작성
 /*
          final List<Detector.Recognition> mappedRecognitions =
                  new LinkedList<Detector.Recognition>(); //여기 리스트에 넣을거임 크롭한거

          boundingBox = new RectF(detection.getBoundingBox()); //이미지 크롭위해 작성한 코드
*/




            Trace.endSection();

        }



        //여기부터 작성시작 ----------"recognizeImage"----------------------------------
    /*
      System.out.println("★리코그니션 0번째 내용★"+recognitions.get(0));
    //recognition 0번째-> [0] NutritionFactsLabel (62.9%) RectF(-13.0, 206.0, 294.0, 316.0)
    //RectF부터 끝까지 슬라이싱해서 좌표찾기

    Recognition rec_rec = recognitions.get(0);
    String str_rec = rec_rec.toString();

    String substr = str_rec.substring(str_rec.indexOf("RectF("));//substr 출력 시: RectF(-13.0, 216.0, 276.0, 322.0)
    String[] array_substr = substr.split(",");

    String one_s = array_substr[0].substring(array_substr[0].lastIndexOf("(")+1);
    String two_s = array_substr[1].replaceAll(" ", "");
    String three_s = array_substr[2].replaceAll(" ", "");
    String four_s = array_substr[3].substring(0, array_substr[3].lastIndexOf(")")).replaceAll(" ", "");


    Double one = Double.parseDouble(one_s);
    Double two = Double.parseDouble(two_s);
    Double three = Double.parseDouble(three_s);
    Double four = Double.parseDouble(four_s);


    System.out.println("********************one 값*****:"+one);
    System.out.println("********************two 값*****:"+two);
    System.out.println("********************three 값*****:"+three);
    System.out.println("********************four 값*****:"+four);
*/
        return recognitions;
    }

    //  public RectF getBoundingBox(RectF box){
    //     box = boundingBox;
    //    return box;
    // }


    @Override
    public void enableStatLogging(final boolean logStats) {}

    @Override
    public String getStatString() {
        return "";
    }

    @Override
    public void close() {
        if (objectDetector != null) {
            objectDetector.close();
        }
    }

    @Override
    public void setNumThreads(int numThreads) {
        if (objectDetector != null) {
            optionsBuilder.setNumThreads(numThreads);
            recreateDetector();
        }
    }

    @Override
    public void setUseNNAPI(boolean isChecked) {
        throw new UnsupportedOperationException(
                "Manipulating the hardware accelerators is not allowed in the Task"
                        + " library currently. Only CPU is allowed.");
    }

    @Override
    public Object process(Bitmap image) {
        return null;
    }


    private void recreateDetector() {
        objectDetector.close();
        objectDetector = ObjectDetector.createFromBufferAndOptions(modelBuffer, optionsBuilder.build());
    }


}
