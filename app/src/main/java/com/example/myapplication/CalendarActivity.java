package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

//edmodo Cropper Test
public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Intent intent = new Intent("com.android.camera.action.CROP");

        //CropImage.activity(imageUri).start(this);
    }


    /**
     * Crop a image taking a recerence a view parent like a frame, and a view child like final
     * reference
     *
     * @param bitmap image to crop
     * @param frame where the image is set it
     * @param reference frame to take reference for crop the image
     * @return image already cropped
     */
    public static byte[] cropImage(Bitmap bitmap, View frame, View reference){
        int heightOriginal = frame.getHeight();
        int widthOriginal = frame.getWidth();

        int heightFrame = reference.getHeight();
        int widthFrame = reference.getWidth();
        int leftFrame = reference.getLeft();
        int topFrame = reference.getTop();

        int heightReal = bitmap.getHeight();
        int widthReal = bitmap.getWidth();

        int widthFinal = (widthFrame * widthReal)/widthOriginal;
        int heightFinal = (heightFrame * heightReal)/heightOriginal;
        int leftFinal = (leftFrame * widthReal)/widthOriginal;
        int topFinal = (topFrame * heightReal)/heightOriginal;

        Bitmap bitmapfinal = Bitmap.createBitmap(bitmap,
                leftFinal, topFinal, widthFinal, heightFinal);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapfinal.compress(Bitmap.CompressFormat.JPEG, 100, stream);//100 is the best quality possibe
        return stream.toByteArray();
    }
}