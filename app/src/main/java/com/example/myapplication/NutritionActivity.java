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
//import com.anychart.sample.R;



public class NutritionActivity extends AppCompatActivity {
    final static String[] COLORS = new String[] {"#ae017e", "#dd3497", "#f768a1", "#6baed6", "#4292c6", "#2171b5", "#084594"}; // 차트 색상

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
        setContentView(R.layout.activity_nutrition);

        SQLiteDatabase db = check_or_copy_db(); // SQLITE DB 저장소 옮김

        // 탄수화물, 단백질, 지방, 포화지방, 식이섬유소, 콜레스테롤, 나트륨 (7 things)
        // 하루 총 필요열량 계산

        int calc = 69 * 30;
        double tan = calc * 0.5; // 탄수화물 에너지량
        double dan = calc * 0.15; // 단백질 에너지량
        double ji = calc * 0.25; // 지방 에너지량
        double po_ji = calc * 0.07; // 포화지방 에너지량
        int sik = 20; // 식이섬유소 에너지량
        int cole = 200; // 콜레 에너지량
        int na = 4000; // 나트륨 에너지량

        // 유저 하루 영양성분 계산
        double u_tan = 0;
        double u_dan = 0;
        double u_ji = 0;
        double u_po_ji = 0;
//        double u_sik = 0;
        double u_cole = 0;
        double u_na = 0;

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


                    u_tan += datas[1] * size * 4; // 탄수화물은 1g 당 4kcal이므로  u_tan += dates[1]*size*4;

                    u_dan += datas[2] * size * 4; //단백질은 1g 당 4kcal

                    u_ji += datas[3] * size * 9;//지방은 1g 당 9kcal

                    u_po_ji += datas[7] * size * 9;
//                    u_sik += ;
                    u_cole += datas[6] * size;
                    u_na += datas[5] * size;
                }
            } catch (Exception err){
                System.out.println(err);
            }
        }

        System.out.println("금일 영양성분 " + u_tan + " " + u_dan + " " + u_ji + " " + u_po_ji + " " + u_cole + " " +  u_na);
        System.out.println("필요 영양성분 대비 백분율" + u_tan/tan*100 + " " + u_dan/dan*100 + " " + u_ji/ji*100 + " " + u_po_ji/po_ji*100 + " " + u_cole/cole*100 + " " +  u_na/na*100);

        String chart_datas[] = new String[]{ Long.toString(Math.round(u_tan/tan*100)), Long.toString(Math.round(u_dan/dan*100)),
                Long.toString(Math.round(u_ji/ji*100)), Long.toString(Math.round(u_po_ji/po_ji*100)),
                Long.toString(Math.round(u_cole/cole*100)), Long.toString(Math.round(u_na/na*100)), "100"}; // 백분율 값을 차트에 넣기 위함

        //프로그레스바============================================================================
        //탄수화물

        int p_tan = 0;
        int p_dan = 0;
        int p_ji = 0;
        int p_poji = 0;
        int p_col = 0;
        int p_na = 0;

        ProgressBar progress_tan = (ProgressBar) findViewById(R.id.progress_tan);
        ProgressBar progress_dan = (ProgressBar) findViewById(R.id.progress_dan);
        ProgressBar progress_ji = (ProgressBar) findViewById(R.id.progress_ji);
        ProgressBar progress_poji = (ProgressBar) findViewById(R.id.progress_poji);
        ProgressBar progress_col= (ProgressBar) findViewById(R.id.progress_col);
        ProgressBar progress_na = (ProgressBar) findViewById(R.id.progress_na);

        TextView edit_tan=(TextView)findViewById(R.id.edit_tan);
        TextView edit_dan=(TextView)findViewById(R.id.edit_dan);
        TextView edit_ji=(TextView)findViewById(R.id.edit_ji);
        TextView edit_poji=(TextView)findViewById(R.id.edit_poji);
        TextView edit_col=(TextView)findViewById(R.id.edit_col);
        TextView edit_na=(TextView)findViewById(R.id.edit_na);


        Integer p_chart_datas[] = new Integer[]{
                Integer.parseInt(chart_datas[0]),Integer.parseInt(chart_datas[1]),
                Integer.parseInt(chart_datas[2]),Integer.parseInt(chart_datas[3]),
                Integer.parseInt(chart_datas[4]),Integer.parseInt(chart_datas[5])
        };


        try {
            p_tan = p_chart_datas[0];
            p_dan = p_chart_datas[1];
            p_ji = p_chart_datas[2];
            p_poji = p_chart_datas[3];
            p_col = p_chart_datas[4];
            p_na = p_chart_datas[5];

        }catch (Exception err){
            System.out.println(err);
        }

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

        //======================================================================================

        String[] things = new String[] { "탄수화물", "단백질", "지방", "포화지방", "콜레스테롤", "나트륨"};

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

        for (int i=0; i<things.length; i++){
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
                    .offsetY(130 - 20*i + "%")
                    .offsetX(0d);

            Bar bar0 = circularGauge.bar(i);
            bar0.dataIndex(i);
            bar0.radius(130 - 20*i);
            bar0.width(17d);
            bar0.fill(new SolidFill(COLORS[i], 1d));
            bar0.stroke(null);
            bar0.zIndex(5d);

            Bar bar100 = circularGauge.bar(100 + i);
            bar100.dataIndex(6);
            bar100.radius(130d - 20*i);
            bar100.width(17d);
            bar100.fill(new SolidFill("#F5F4F4", 1d));
            bar100.stroke("1 #e5e4e4");
            bar100.zIndex(4d);
        }

        circularGauge.margin(50d, 50d, 50d, 50d);
        circularGauge.title()
                .text("내 영양성분 섭취 현황")
                .useHtml(true);
        circularGauge.title().enabled(true);
        circularGauge.title().hAlign(HAlign.CENTER);
        circularGauge.title()
                .padding(0d, 0d, 0d, 0d)
                .margin(0d, 0d, 0d, 0d);

        anyChartView.setChart(circularGauge);

    }





}