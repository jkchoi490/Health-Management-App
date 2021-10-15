
package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MyNutritionsActivity extends AppCompatActivity {


    public SQLiteDatabase check_or_copy_db(){
        String fileName = "FOOD.db";
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
            }
            catch (Exception err){
                System.out.println(err);
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(file, null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_nutritions);

        //인식한 영양성분표 성분들
        Intent intent = getIntent();
        Map<String, Integer> map = (Map<String, Integer>)intent.getSerializableExtra("map");

        System.out.println("인텐트값 왔는지 확인(nut_list -> nutrition_facts-> map"+map); //map값이 NULL나옴**에러
      //  for (String mapkey : map.keySet()){ //error--------------------------------------
      //      System.out.println("key:"+mapkey+",value:"+map.get(mapkey));
       // }


        SQLiteDatabase db = check_or_copy_db(); // SQLITE DB 저장소 옮김

        // 칼로리 탄수화물, 단백질, 지방, 포화지방, 식이섬유소, 콜레스테롤, 나트륨  하루 총 필요열량 계산------------------
        double calc = 69 * 30;
        double tan = calc * 0.5; // 탄수화물 에너지량
        double dan = calc * 0.15; // 단백질 에너지량
        double ji = calc * 0.25; // 지방 에너지량
        double po_ji = calc * 0.07; // 포화지방 에너지량
       // int cole = 0; // 콜레스테롤 최대 섭취량 ( 0g에 가까울수록 좋음)
        int na = 4000; // 나트륨 최대 섭취량(1일 4000g이하로 섭취)
        //-------------------------------------------------------------------------------------------------

        // 유저 하루 영양성분 계산
        double u_cal = 0; //칼로리
        double u_tan = 0; //탄
        double u_dan = 0; //단
        double u_ji = 0; //지
        double u_po_ji = 0; //포지
        double u_cole = 0; //콜
        double u_na = 0; //나
        double u_dang =0; //당 적게 먹을수록 이득
        double u_transji=0; //트랜스지 적게 먹을수록 이득

        DBHandler2 dbHelper = new DBHandler2(this.getApplicationContext());
        ArrayList<CourseModal2> courseModalArrayList = dbHelper.readCourses();

        for ( CourseModal2 row : courseModalArrayList){ // 데이터 읽음
            String menu = row.getdiet(); // 식단메뉴
            String type = row.getmenu(); // 아/점/저
            int size = Integer.parseInt(row.getsize()); // 인분

            try {
                String query = "SELECT * FROM FoodDB WHERE DESC_KOR = " + '\'' + menu + '\''; // 식단메뉴에 관한 데이터 조회 위함
                Cursor c = db.rawQuery(query, null);
                if (c != null && c.moveToFirst()) {
                    /*
                        0 : 열량 1 : 탄수화물 2 : 단백질 3 : 지방 4 : 당류 5 : 나트륨 6 : 콜레스테롤 7 : 포화지방산 8 : 트랜스지방 [Array 인덱스 이용해서 데이터 참조!]
                     */
                    float datas[] = new float[] {c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8)
                            ,c.getFloat(9), c.getFloat(10), c.getFloat(11), c.getFloat(12)};

                    u_cal += datas[0]; //칼로리 값 가져오기 **추가된 부분
                    u_tan += datas[1] * size * 4; // 탄수화물은 1g 당 4kcal이므로  u_tan += dates[1]*size*4;

                    u_dan += datas[2] * size * 4; //단백질은 1g 당 4kcal

                    u_ji += datas[3] * size * 9;//지방은 1g 당 9kcal

                    u_po_ji += datas[7] * size * 9;

                    u_cole += datas[6] * size;
                    u_na += datas[5] * size;

                    u_dang += datas[4]*size;
                    u_transji += datas[8]*size;
                }
            } catch (Exception err){
                System.out.println(err);
            }
        }

        System.out.println("금일 영양성분 " + u_tan + " " + u_dan + " " + u_ji + " " + u_po_ji + " " + u_cole + " " +  u_na);
        System.out.println("필요 영양성분 대비 백분율" + u_tan/tan*100 + " " + u_dan/dan*100 + " " + u_ji/ji*100 + " " + u_po_ji/po_ji*100 + " " + u_cole/1*100 + " " +  u_na/na*100);

        String chart_datas[] = new String[]{
                Long.toString(Math.round(u_cal/calc*100)), //칼로리0
                Long.toString(Math.round(u_tan/tan*100)),  //탄1
                Long.toString(Math.round(u_dan/dan*100)), //단2
                Long.toString(Math.round(u_ji/ji*100)),  //지3
                Long.toString(Math.round(u_po_ji/po_ji*100)), //포지4
                Long.toString(Math.round(u_cole/100)), //콜5
                Long.toString(Math.round(u_na/na*100)), //나6
                Long.toString(Math.round(u_dang/1*100)), //당7
                Long.toString(Math.round(u_transji/1*100)), //트랜스지8
                "100"}; // 백분율 값을 차트에 넣기 위함

        //=======================영양성분 10가지 프로그레스바===========================================
        int p_cal = 0; //칼로리
        int p_tan = 0; // 탄
        int p_dan = 0; // 단
        int p_ji = 0; // 지
        int p_poji = 0; // 포화지방
        int p_transji = 0; //트랜스지방
        int p_col = 0; // 콜레스테롤
        int p_na = 0; //나트륨
        int p_dang = 0; // 당류

        //프로그레스 바 현재섭취한 만큼 표시
        ProgressBar progress_cal = findViewById(R.id.progress_my_cal); //칼로리 프로그레스바1
        ProgressBar progress_tan = findViewById(R.id.progress_my_tan); //탄2
        ProgressBar progress_dan = findViewById(R.id.progress_my_dan);//단3
        ProgressBar progress_ji = findViewById(R.id.progress_my_ji); //지4
        ProgressBar progress_poji = findViewById(R.id.progress_my_poji); //포화지5
        ProgressBar progress_transji = findViewById(R.id.progress_my_transji); //트랜스지방6
        ProgressBar progress_col= findViewById(R.id.progress_my_col); //콜레스테롤8
        ProgressBar progress_na = findViewById(R.id.progress_my_na); //나트륨9
        ProgressBar progress_dang = findViewById(R.id.progress_my_dang); //당류10

        //하루 섭취가능한 총량을 표시 (최대량)
        TextView edit_cal = findViewById(R.id.textView_my_cal);  //칼로리1
        TextView edit_tan=findViewById(R.id.textView_my_tan);  //탄수화물2
        TextView edit_dan=findViewById(R.id.textView_my_dan); //단백질3
        TextView edit_ji=findViewById(R.id.textView_my_ji);   //지방4
        TextView edit_poji=findViewById(R.id.textView_my_poji);  //포화지방5
        TextView edit_transji = findViewById(R.id.textView_my_transji);//트랜스지6
        TextView edit_col=findViewById(R.id.textView_my_col);  //콜레스테롤8
        TextView edit_na=findViewById(R.id.textView_my_na);   //나트륨9
        TextView edit_dang=findViewById(R.id.textView_my_dang);   //당류10


        //하루 섭취 가능한

        Integer p_chart_datas[] = new Integer[]{
                Integer.parseInt(chart_datas[0]), //칼로리
                Integer.parseInt(chart_datas[1]), //탄
                Integer.parseInt(chart_datas[2]), //단
                Integer.parseInt(chart_datas[3]), //지
                Integer.parseInt(chart_datas[4]), //포지
                Integer.parseInt(chart_datas[5]), //콜
                Integer.parseInt(chart_datas[6]), //나
                Integer.parseInt(chart_datas[7]), //당
                Integer.parseInt(chart_datas[8]) //트랜스지
        };


        try {
            p_cal = p_chart_datas[0];
            p_tan = p_chart_datas[1];
            p_dan = p_chart_datas[2];
            p_ji = p_chart_datas[3];
            p_poji = p_chart_datas[4];
            p_col = p_chart_datas[5];
            p_na = p_chart_datas[6];
            p_dang = p_chart_datas[7];
            p_transji = p_chart_datas[8];


        }catch (Exception err){
            System.out.println(err);
        }

        //칼로리
        progress_cal.setProgress(p_cal);
        edit_cal.setText(String.valueOf(p_cal));

        //탄수
        progress_tan.setProgress(p_tan);
        edit_tan.setText(String.valueOf(p_tan));


        //단백질
        progress_dan.setProgress(p_dan);
        edit_dan.setText(String.valueOf(p_dan));

        //지방
        progress_ji.setProgress(p_ji);
        edit_ji.setText(String.valueOf(p_ji));

        //포화지방
        progress_poji.setProgress(p_poji);
        edit_poji.setText(String.valueOf(p_poji));

        //콜레스테롤
        progress_col.setProgress(p_col);
        edit_col.setText(String.valueOf(p_col));

        //나트륨
        progress_na.setProgress(p_na);
        edit_na.setText(String.valueOf(p_na));

        //당류
        progress_dang.setProgress(p_dang);
        edit_dang.setText(String.valueOf(p_dang));

        //트랜스지방
        progress_transji.setProgress(p_transji);
        edit_transji.setText(String.valueOf(p_transji));

        Button button_food = findViewById(R.id.button_blue);
        List value_count = new ArrayList<>();

        try {
            boolean change = false;
            int p_value = 0;


                for (int i = 0; i < p_chart_datas.length; i++) {
                    p_value = p_chart_datas[i];
                    if (p_value > 100) {
                        value_count.add(p_value);
                    }
                }

            System.out.println("value_count 원소개수===*******=="+value_count.size());

            if (value_count.size()>0) {
                change = true;
            }

            if(change == true){
                button_food.setOnClickListener(v -> {
                    Intent intent_over = new Intent(MyNutritionsActivity.this, DialogOverActivity.class);
                    intent_over.putExtra("map",(Serializable) map);
                    startActivity(intent_over);
                });

            }
            else {
                button_food.setOnClickListener(v -> {
                    Intent intent_not_over = new Intent(MyNutritionsActivity.this, DialogNotOverActivity.class);
                    intent_not_over.putExtra("map",(Serializable) map); //권장섭취량 추천해줘야하므로 map 값 넘겨줌
                    startActivity(intent_not_over);
                });

            }

        }catch (Exception err_aboutButtons){
            System.out.println("Activity Change Error..!");
        }

    }


}



