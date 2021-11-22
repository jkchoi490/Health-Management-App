package com.example.myapplication;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.SingleValueDataSet;
import com.anychart.charts.CircularGauge;
import com.anychart.core.axes.Circular;
import com.anychart.core.gauge.pointers.Bar;
import com.anychart.enums.Anchor;
import com.anychart.graphics.vector.Fill;
import com.anychart.graphics.vector.SolidFill;
import com.anychart.graphics.vector.text.HAlign;
import com.anychart.graphics.vector.text.VAlign;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class NutritionTestActivity extends AppCompatActivity {

    final static String[] COLORS = new String[]{"" +
            "#ae017e",
            "#dd3497",
            "#f768a1",
            "#6baed6",
            "#4292c6",
            "#2171b5",
            "#084594"}; // 차트 색상

    private DBHandlerNutrition dbHandlerNutrition;

    public SQLiteDatabase check_or_copy_db() {
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
            } catch (Exception err) {
                System.out.println(err);
            }
        }
        return SQLiteDatabase.openOrCreateDatabase(file, null);
    }


    public int cal_value = 0;
    public int tan_value = 0;
    public int dan_value = 0;
    public int ji_value = 0;
    public int poji_value = 0;
    public int sik_value = 0;
    public int col_value = 0;
    public int na_value = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_test);

        SQLiteDatabase db = check_or_copy_db(); // SQLITE DB 저장소 옮김

        DBHandlerNutrition dbHelperN = new DBHandlerNutrition(this.getApplicationContext());
        ArrayList<CourseModalN> courseModalArrayListN = dbHelperN.readCoursesN();

        dbHandlerNutrition = new DBHandlerNutrition(NutritionTestActivity.this);

        for (CourseModalN row : courseModalArrayListN) { // 데이터 읽음

            String tan_in_db = row.getTan(); // 탄수화물
            String dan_in_db = row.getDan(); // 단백질
            String ji_in_db = row.getJi(); // 지방
            String poji_in_db = row.getPoji();//포지
            String sik_in_db = row.getSik(); // 식
            String col_in_db = row.getCol();//콜
            String na_in_db = row.getNa(); // 나트륨
            String cal_in_db = row.getCal(); //칼로리


            try {


                //영양성분별 수치 입력 -> 운동할거 아니거나 음식 먹는거 아니면 변동 x
                TextView t_calories = findViewById(R.id.edit_cal);
                t_calories.setText(cal_in_db);

                TextView t_tan = findViewById(R.id.edit_tan);
                t_tan.setText(tan_in_db);

                TextView t_dan = findViewById(R.id.edit_dan);
                t_dan.setText(dan_in_db);

                TextView t_ji = findViewById(R.id.edit_ji);
                t_ji.setText(ji_in_db);

                TextView t_poji = findViewById(R.id.edit_poji);
                t_poji.setText(poji_in_db);

                TextView t_sik = findViewById(R.id.edit_sik);
                t_sik.setText(sik_in_db);

                TextView t_col = findViewById(R.id.edit_col);
                t_col.setText(col_in_db);

                TextView t_na = findViewById(R.id.edit_na);
                t_na.setText(na_in_db);

                //영양성분별 최대섭취량
                cal_value = Integer.valueOf(cal_in_db);
                tan_value = Integer.valueOf(tan_in_db);
                dan_value = Integer.valueOf(dan_in_db);
                ji_value = Integer.valueOf(ji_in_db);
                poji_value = Integer.valueOf(poji_in_db);
                sik_value = Integer.valueOf(sik_in_db);
                col_value = Integer.valueOf(col_in_db);
                na_value = Integer.valueOf(na_in_db);
            } catch (Exception err) {
                System.out.println("에러입니다");
            }
        }
            
                //유저 하루 영양성분 계산 -> 차트 업데이트
                double u_cal = 0;
                double u_tan = 0;
                double u_dan = 0;
                double u_ji = 0;
                double u_po_ji = 0;
                double u_sik = 0;
                double u_cole = 0;
                double u_na = 0;
                double u_transji = 0;

                DBHandler2 dbHelper = new DBHandler2(this.getApplicationContext());
                ArrayList<CourseModal2> courseModalArrayList = dbHelper.readCourses();

                for (CourseModal2 row_s : courseModalArrayList) { // 데이터 읽음
                    String menu = row_s.getdiet(); // 식단메뉴
                    String type = row_s.getmenu(); // 아/점/저
                    int size = Integer.parseInt(row_s.getsize()); // 인분

                    try {
                        String query = "SELECT * FROM FoodDB WHERE DESC_KOR = " + '\'' + menu + '\''; // 식단메뉴에 관한 데이터 조회 위함
                        Cursor c = db.rawQuery(query, null);
                        if (c != null && c.moveToFirst()) {
                    /*
                        0 : 열량 1 : 탄수화물 2 : 단백질 3 : 지방 4 : 당류 5 : 나트륨 6 : 콜레스테롤 7 : 포화지방산 8 : 트랜스지방 [Array 인덱스 이용해서 데이터 참조!]
                     */

                            // 3 : 총내용량g  4: 칼로리[0] 5:탄[1] 6:단[2] 7:지[3] 8:당[4] 9:나[5] 10:콜[6] 11:포지[7] 12:트랜스지방[8]

                            float datas[] = new float[]{c.getFloat(4), c.getFloat(5), c.getFloat(6), c.getFloat(7), c.getFloat(8)
                                    , c.getFloat(9), c.getFloat(10), c.getFloat(11), c.getFloat(12)};

                            u_cal += datas[0]*size; // 4: 칼로리[0]

                            u_tan += datas[1] * size * 7.716179; //g을 칼로리로 나타내야함

                            u_dan += datas[2] * size * 7.716179;

                            u_ji += datas[3] * size * 7.716179;

                            u_po_ji += datas[7] * size * 7.716179;
//                    u_sik += ;
                            u_cole += datas[6] * size;
                            u_na += datas[5] * size;
                            u_transji += datas[8] *size * 7.716179;
                        }
                    } catch (Exception err) {
                        System.out.println(err);
                    }
                }

                System.out.println("금일 영양성분: 칼 :" + u_cal +"탄 : "+u_tan+ " 단 : " + u_dan + "지 :" + u_ji + " 포지 :  " + u_po_ji + " 콜 : " + u_cole + "나 :  " + u_na);
                System.out.println("필요 영양성분 대비 백분율" + (u_tan / tan_value) * 100 + " " + (u_dan / dan_value) * 100 + " " + (u_ji / ji_value) * 100 + " " + (u_po_ji / poji_value) * 100 + " " + (u_cole / col_value) * 100 + " " + (u_na / na_value) * 100);

                String chart_datas[] = new String[]{Long.toString(Math.round((u_tan / tan_value) * 100)), Long.toString(Math.round((u_dan / dan_value) * 100)),
                        Long.toString(Math.round((u_ji/ji_value) * 100)), Long.toString(Math.round((u_po_ji / poji_value) * 100)),
                        Long.toString(Math.round((u_cole / col_value) * 100)), Long.toString(Math.round((u_na / na_value) * 100)), "100"}; // 백분율 값을 차트에 넣기 위함


//프로그레스바============================================================================


                int p_cal = 0;
                int p_tan = 0;
                int p_dan = 0;
                int p_ji = 0;
                int p_poji = 0;
                int p_col = 0;
                int p_na = 0;
                int p_sik = 0;

                ProgressBar progress_cal = (ProgressBar) findViewById(R.id.progress_cal);
                ProgressBar progress_tan = (ProgressBar) findViewById(R.id.progress_tan);
                ProgressBar progress_dan = (ProgressBar) findViewById(R.id.progress_dan);
                ProgressBar progress_ji = (ProgressBar) findViewById(R.id.progress_ji);
                ProgressBar progress_poji = (ProgressBar) findViewById(R.id.progress_poji);
                ProgressBar progress_col = (ProgressBar) findViewById(R.id.progress_col);
                ProgressBar progress_na = (ProgressBar) findViewById(R.id.progress_na);
                ProgressBar progress_sik = (ProgressBar) findViewById(R.id.progress_sik);

                TextView ate_cal = (TextView) findViewById(R.id.ate_cal);
                TextView ate_tan = (TextView) findViewById(R.id.ate_tan);
                TextView ate_dan = (TextView) findViewById(R.id.ate_dan);
                TextView ate_ji = (TextView) findViewById(R.id.ate_ji);
                TextView ate_poji = (TextView) findViewById(R.id.ate_poji);
                TextView ate_col = (TextView) findViewById(R.id.ate_col);
                TextView ate_na = (TextView) findViewById(R.id.ate_na);
                TextView ate_sik = (TextView) findViewById(R.id.ate_sik);

                Integer p_chart_datas[] = new Integer[]{
                        Integer.parseInt(Long.toString(Math.round((u_cal / cal_value) * 100))), //칼로리
                        Integer.parseInt(chart_datas[0]), //탄
                        Integer.parseInt(chart_datas[1]), //단
                        Integer.parseInt(chart_datas[2]), //지
                        Integer.parseInt(chart_datas[3]), //포지
                        Integer.parseInt(chart_datas[4]), //콜
                        Integer.parseInt(chart_datas[5]), //나트륨
                       // (int)u_cole, //콜레스테롤 --------->Error datas로 바꾸면 될듯????;;
                       // (int)u_po_ji,//Integer.parseInt(chart_datas[7]), //포화지방
                        //(int)u_transji//Integer.parseInt(chart_datas[8]) //트랜스지방
                };


                try {
                    p_cal = p_chart_datas[0];
                    p_tan = p_chart_datas[1];
                    p_dan = p_chart_datas[2];
                    p_ji = p_chart_datas[3];
                    p_poji = p_chart_datas[4];
                    p_col = p_chart_datas[5];
                   p_na = p_chart_datas[6];
                    // p_col = p_chart_datas[6];
                    //p_poji = p_chart_datas[7];

                } catch (Exception err) {
                    System.out.println(err);
                }

                //탄수화물
                progress_tan.setProgress(p_tan);
                ate_tan.setText(String.valueOf((int)u_tan));

                //단백질
                progress_dan.setProgress(p_dan);
                ate_dan.setText(String.valueOf((int)u_dan));

                //지방
                progress_ji.setProgress(p_ji);
                ate_ji.setText(String.valueOf((int)u_ji));

                //포화지방
                progress_poji.setProgress(p_poji);
                ate_poji.setText(String.valueOf((int)u_po_ji));

                //콜레스테롤
                progress_col.setProgress(p_col);
                ate_col.setText(String.valueOf((int)u_cole));

                //나트륨
                progress_na.setProgress(p_na);
                ate_na.setText(String.valueOf((int)u_na));

                //칼로리
                progress_cal.setProgress(p_cal);
                ate_cal.setText(String.valueOf((int)u_cal));
                //======================================================================================

                String[] things = new String[]{"탄수화물", "단백질", "지방", "포화지방", "콜레스테롤", "나트륨"};

                AnyChartView anyChartView = findViewById(R.id.any_chart_view_circle);
                anyChartView.setProgressBar(findViewById(R.id.progress_bar));

                CircularGauge circularGauge = AnyChart.circular();
                circularGauge.data(new SingleValueDataSet(chart_datas));
                circularGauge.fill("#fff")
                        .stroke(null)
                        .padding(0d, 0d, 0d, 0d)
                        .margin(100d, 100d, 100d, 100d);
                circularGauge.startAngle(0d);
                circularGauge.sweepAngle(270d);

                Circular xAxis = circularGauge.axis(0)
                        .radius(100d)
                        .width(1d)
                        .fill((Fill) null);
                xAxis.scale()
                        .minimum(0d)
                        .maximum(100d);
                xAxis.ticks("{ interval: 1 }")
                        .minorTicks("{ interval: 1 }");
                xAxis.labels().enabled(false);
                xAxis.ticks().enabled(false);
                xAxis.minorTicks().enabled(false);

                for (int i = 0; i < things.length; i++) {
                    String label = things[i]; // 키

                    // Draw Label
                    circularGauge.label(i)
                            .text(label)
                            .useHtml(true)
                            .hAlign(HAlign.CENTER)
                            .vAlign(VAlign.MIDDLE);
                    circularGauge.label(i)
                            .anchor(Anchor.RIGHT_CENTER)
                            .padding(0d, 10d, 0d, 0d)
                            .height(17d / 2d + "%")
                            .offsetY(130 - 20 * i + "%")
                            .offsetX(0d);

                    Bar bar0 = circularGauge.bar(i);
                    bar0.dataIndex(i);
                    bar0.radius(130 - 20 * i);
                    bar0.width(17d);
                    bar0.fill(new SolidFill(COLORS[i], 1d));
                    bar0.stroke(null);
                    bar0.zIndex(5d);

                    Bar bar100 = circularGauge.bar(100 + i);
                    bar100.dataIndex(6);
                    bar100.radius(130d - 20 * i);
                    bar100.width(17d);
                    bar100.fill(new SolidFill("#F5F4F4", 1d));
                    bar100.stroke("1 #e5e4e4");
                    bar100.zIndex(4d);
                }

                circularGauge.margin(50d, 50d, 50d, 50d);
                circularGauge.title()
                        .text("")
                        .useHtml(true);
                circularGauge.title().enabled(true);
                circularGauge.title().hAlign(HAlign.CENTER);
                circularGauge.title()
                        .padding(0d, 0d, 0d, 0d)
                        .margin(0d, 0d, 0d, 0d);

                anyChartView.setChart(circularGauge);

        }
    }





