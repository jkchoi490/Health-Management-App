package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class CalculateCaloriesActivity extends AppCompatActivity {

    /*
    public SQLiteDatabase check_or_copy_db() {
        String fileName = "userDB.db";
        File file = getDatabasePath(fileName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }

            try {
                InputStream inputStream = getAssets().open(fileName);
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024 * 8];
                int numOfBytesToRead;

                while ((numOfBytesToRead = inputStream.read(buffer)) > 0)
                    outputStream.write(buffer, 0, numOfBytesToRead);

                inputStream.close();
                outputStream.close();
            } catch (Exception err) {
                System.out.println(err);
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(file, null);
    }
     */


    public double StandardWeight = 0; //표준체중
    public double DailyCalories = 0; //하루평균 총 필요열량
    public double ExerciseValue = 0; //운동량 지수
    public double GenderValue = 0; //성별 지수

    public double tansu = 0;         // (50-60% 이내)
    public double danbaek = 0;      //(15-20% 이내)
    public double jibang = 0;       //(25% 이내)
    public double transjibang = 0;  //최소화
    public double pohwajibang = 0;  //(7%미만, 최소화)
    public double sickisumyoo =25;    //1일 20g-25g (1g 당 2kcal) => 40 ~ 50kcal
    public double cholesterol = 200;  //0 최소화
    public double Nat = 4000;              // 1일 4000mg 이하
    public double dang = 0;         //0최소화

    private DBHandlerNutrition dbHandlerNutrition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_calories);



        DBHandler dbHelper = new DBHandler(this.getApplicationContext());
        ArrayList<CourseModal> courseModalArrayList = dbHelper.readCourses();

        dbHandlerNutrition = new DBHandlerNutrition(CalculateCaloriesActivity.this);

        for (CourseModal row : courseModalArrayList) { // 데이터 읽음
            int id = row.getId();
            String user_name = row.getname(); //사람이름
            String gender = row.getgender(); //성별
            String s_height = row.getheight();
            String exercise = row.getexercise(); // 운동량

            System.out.println("이름 : " + user_name);
            System.out.println("성별 : " + gender);
            System.out.println("키 : " + s_height);
            System.out.println("운동량 : " + exercise);
            //계산된 칼로리 화면에 표시


            //사람이름 화면에 표시 user_name
            TextView tv_user_name = findViewById(R.id.user_name);
            tv_user_name.setText(user_name);

            try {

                int height = Integer.parseInt(s_height);

                if (gender.equals("남")) {
                    GenderValue = 22;

                } else if (gender.equals("여")) {

                    GenderValue = 21;

                } else {
                    System.out.println("gender content error");
                }


                StandardWeight = Math.pow(((double)height / 100), 2)* GenderValue; //계산시 한쪽을 double이나 float으로 변경해줘야함

                System.out.println("height:"+height);
                System.out.println("표준체중 : " + StandardWeight);

                if (exercise.equals("적음")) {
                    ExerciseValue = 27;

                } else if (exercise.equals("보통")) {
                    ExerciseValue = 32;
                } else if (exercise.equals("많음")) {
                    ExerciseValue = 37;

                } else {
                    System.out.println("exercise value error");
                }

                System.out.println(" ExerciseValue : "+ ExerciseValue);
                DailyCalories = StandardWeight * ExerciseValue;
                System.out.println("하루평균 총 필요 열량 : " + DailyCalories);

                TextView calculate_calories = findViewById(R.id.my_cal_calories);
                calculate_calories.setText(String.valueOf((int)DailyCalories));

                TextView calculate_calories2 = findViewById(R.id.my_cal_calories2);
                calculate_calories2.setText(String.valueOf((int)DailyCalories));

                try {
                    tansu = DailyCalories * 0.5;
                    danbaek = DailyCalories * 0.2;
                    jibang = DailyCalories * 0.25;
                    pohwajibang = DailyCalories * 0.05;
                    System.out.println("탄수화물 : " + tansu);
                    System.out.println("단백질 : " + danbaek);
                    System.out.println("지방 : " + jibang);
                    System.out.println("포화지방 : " + pohwajibang);
                    System.out.println("트랜스지방 : " + transjibang);
                    System.out.println("식이섬유 : " + sickisumyoo);
                    System.out.println("콜 : " + cholesterol);
                    System.out.println("나트륨 : " + Nat);
                    System.out.println("당류 : " + dang);


                    TextView calculate_tan = findViewById(R.id.my_cal_tan);
                    calculate_tan.setText(String.valueOf((int)tansu));

                    TextView calculate_dan = findViewById(R.id.my_cal_dan);
                    calculate_dan.setText(String.valueOf((int)danbaek));

                    TextView calculate_ji = findViewById(R.id.my_cal_ji);
                    calculate_ji.setText(String.valueOf((int)jibang));

                    TextView calculate_poji= findViewById(R.id.my_cal_poji);
                    calculate_poji.setText(String.valueOf((int)pohwajibang));

                    TextView calculate_transji = findViewById(R.id.my_cal_transji);
                    calculate_transji.setText(String.valueOf((int)transjibang));

                    TextView calculate_sik = findViewById(R.id.my_cal_sik);
                    calculate_sik.setText(String.valueOf((int)sickisumyoo));

                    TextView calculate_col = findViewById(R.id.my_cal_col);
                    calculate_col.setText(String.valueOf((int)cholesterol));

                    TextView calculate_na = findViewById(R.id.my_cal_na);
                    calculate_na.setText(String.valueOf((int)Nat));


                    //DB에 저장
                    String cal_db = String.valueOf((int)DailyCalories);
                    String tan_db = String.valueOf((int)tansu);
                    String dan_db = String.valueOf((int)danbaek);
                    String ji_db = String.valueOf((int)jibang);
                    String poji_db = String.valueOf((int)pohwajibang);
                    String sik_db = String.valueOf((int)sickisumyoo);
                    String col_db = String.valueOf((int)cholesterol);
                    String na_db = String.valueOf((int)Nat);


                    dbHandlerNutrition.addNewCourseN(
                            user_name,
                            gender,
                            exercise,
                            s_height,
                            cal_db,
                            tan_db,
                            dan_db,
                            ji_db,
                            poji_db,
                            sik_db,
                            col_db,
                            na_db);

                } catch (Exception err) {
                    System.out.println("으아아아ㅏ아에러그만");
                }



            } catch (Exception err) {
                System.out.println(err);
            }
        }


        //Button
        Button button_ok= findViewById(R.id.button_ok);
        button_ok.setOnClickListener(v -> {

            Intent ok_intent= new Intent(CalculateCaloriesActivity.this,tandanjipoActivity.class);
            startActivity(ok_intent);
        });



    }
}