package com.example.myapplication.customview;

import com.example.myapplication.tflite.Detector.Recognition;

import java.util.List;

public interface ResultsView {
    public void setResults(final List<Recognition> results);
}
