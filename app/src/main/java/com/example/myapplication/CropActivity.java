package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;

public class CropActivity extends AppCompatActivity {

    //Detector Activity에서 가져온 이미지
    private Bitmap pass_image = null;

    Bitmap crop_final = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);


        Intent image_pass = getIntent();
        pass_image = image_pass. getParcelableExtra("pass_image");
        ImageView passImage = findViewById(R.id.quick_start_cropped_image);
        passImage.setImageBitmap(pass_image);


        startCropActivity();



    }

    private void startCropActivity() {
        // start picker to get image for cropping and then use the image in cropping activity
        Uri pass_image_uri =  getImageUri(this,pass_image);
        CropImage.activity(pass_image_uri)
                .start(this);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }
    //=====================================================================

//=====================================================================




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            //크롭 성공시
            if (resultCode == RESULT_OK) {
                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());

                ImageView result_crop = findViewById(R.id.quick_start_cropped_image);
                BitmapDrawable drawable = (BitmapDrawable) result_crop.getDrawable();
                Bitmap crop_bitmap = drawable.getBitmap();

                Intent intent_crop = new Intent(CropActivity.this, CameraActivity2.class);
                intent_crop.putExtra("pass_crop", crop_bitmap);
                startActivity(intent_crop);


                //실패시
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                System.out.println("이미지 크롭 실패");
            }
        }
    }





}