package com.example.myapplication.tflite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Trace;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;
import org.tensorflow.lite.task.vision.detector.ObjectDetector.ObjectDetectorOptions;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TFLiteObjectDetectionAPIModel implements Detector {
    //private static final String TAG = "TFLiteObjectDetectionAPIModelWithTaskApi";

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

    private static final Object TF_OD_API_INPUT_SIZE = 384;
    private Matrix cropToFrameTransform;
    public RectF boundingBox;

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
        Trace.beginSection("recognizeImage");
        List<Detection> results = objectDetector.detect(TensorImage.fromBitmap(bitmap));

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
            Trace.endSection();
        }
        return recognitions;
    }

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
