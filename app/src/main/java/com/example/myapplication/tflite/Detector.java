package com.example.myapplication.tflite;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.List;

/** Generic interface for interacting with different recognition engines. */
public interface Detector {
    //List<Recognition> recognizeImage(Bitmap bitmap);
    List<Recognition> recognizeImage(Bitmap bitmap);

    void enableStatLogging(final boolean debug);
    void register(String name, Recognition recognition);
    String getStatString();

    void close();

    void setNumThreads(int numThreads);

    void setUseNNAPI(boolean isChecked);

    Object process(Bitmap image);

    // RectF getBoundingBox();

    /** An immutable result returned by a Detector describing what was recognized. */
    public class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /** Display name for the recognition. */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        /** Optional location within the source image for the location of the recognized object. */
        private RectF location;
        private Bitmap crop;
        private Object extra;
        private Integer color;
        private Float distance;





        public Recognition(
                final String id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
            this.crop = null;
            this.extra = null;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        public void setLocation(RectF location) {
            this.location = location;
        }

        public Float getDistance() {
            return distance;
        }


        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }

        public void setCrop(Bitmap crop) {
            this.crop = crop;
        }

        public Bitmap getCrop() {
            return this.crop;
        }
        public void setExtra(Object extra) {
            this.extra = extra;
        }
        public Object getExtra() {
            return this.extra;
        }

        private HashMap<String, Recognition> registered = new HashMap<>();
        public void register(String name, Detector.Recognition rec) {
            registered.put(name, rec);
        }

        public void setColor(Integer color) {
            this.color = color;
        }
        public Integer getColor() {
            return this.color;
        }


    }
}