package com.example.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CameraAnalyzeActivity extends AppCompatActivity {

    final static String[] COLORS = new String[] {"#ae017e", "#dd3497", "#f768a1", "#6baed6", "#4292c6", "#2171b5", "#084594"}; // 차트 색상


    //----리싸이클러 뷰 리스트---------------------------------------
    private ArrayList<DictionaryCameraAnalyze> mArrayList;
    private CustomAdapter mAdapter;
    private int count = -1;
    //------------------------------------------------------------------

    //--------my chart--------------------------------------
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
        setContentView(R.layout.activity_camera_analyze);

        AnyChartView anyChartView = findViewById(R.id.food_pie_chart); //해당 음식 차트
        //AnyChartView anyChartView_mychart = findViewById(R.id.my_chart); //내 영양성분 섭취상태 차트

        Pie pie = AnyChart.pie(); //해당 음식차트

        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(CameraAnalyzeActivity.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        ArrayList<String> nut_list = new ArrayList<String>();
        List<String> nut_name_list = new ArrayList<>();
        List<Integer> nut_size_list = new ArrayList<>();

        nut_list.add("나트륨 460 mg");
        nut_list.add("탄수화물 49000mg"); //49g
        nut_list.add(" 당류 8000mg"); //8g
        nut_list.add("지방 24000 mg"); //24g
        nut_list.add("트랜스지방 0g"); //0g
        nut_list.add("포화지방 12000mg"); // 12g
        nut_list.add("콜레스테롤 0mg");
        nut_list.add("단백질 5000mg"); //5g
        nut_list.add("식이섬유 0mg");



        for (int i = 0; i < nut_list.size(); i++) {
            nut_name_list.add(getStringPart(nut_list.get(i)));
            nut_size_list.add(Integer.parseInt(getIntegerPart(nut_list.get(i))));
        }

      //  System.out.println(nut_name_list);
      //  System.out.println(nut_size_list);

        Map<String, Integer> map = new LinkedHashMap<>(); //영양성분이랑 탄=몇g, 단=몇g 딕셔너리 형태로 제작
        for (int i=0; i<nut_name_list.size(); i++) {
            map.put(nut_name_list.get(i), nut_size_list.get(i));
        }
       // System.out.println(map);

      //  for (String mapkey : map.keySet()){
      //      System.out.println("key:"+mapkey+",value:"+map.get(mapkey));
      //  }

      // System.out.println("탄수화물 값 "+map.get("탄수화물 "));
      //  System.out.println("단백질 값(value)"+map.get("단백질 ")); //이거 보여주면 되것다!

        Integer d_tan = map.get("탄수화물 ");
        Integer d_dan = map.get("단백질 ");
        Integer d_ji = map.get("지방 ");
        Integer d_poji = map.get("포화지방 ");
        Integer d_sik = map.get("식이섬유 ");
        Integer d_na = map.get("나트륨 ");
        Integer d_col = map.get("콜레스테롤 ");

        List<DataEntry> data = new ArrayList<>(); //탄,단,지,포지,식이,나트륨,콜레 성분

        data.add(new ValueDataEntry("탄수화물", d_tan));
        data.add(new ValueDataEntry("단백질", d_dan));
        data.add(new ValueDataEntry("지방", d_ji));
        data.add(new ValueDataEntry("포화지방", d_poji));
        data.add(new ValueDataEntry("식이섬유", d_sik));
        data.add(new ValueDataEntry("나트륨", d_na));
        data.add(new ValueDataEntry("콜레스테롤", d_col));



        pie.data(data);

        pie.title("해당식품의 영양성분 분석결과");

        pie.labels().position("outside");

        pie.legend().title().enabled(true);
        pie.legend().title()
               .text("클릭 시 자세한 내용 확인가능")
               .padding(0d, 0d, 5d, 0d);

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

        /*
        //나의 영양섭취 변화 원 차트 ---------------------------------------------
        String[] things = new String[] { "탄수화물", "단백질", "지방", "포화지방", "콜레스테롤", "나트륨"};

        SQLiteDatabase db_ = check_or_copy_db(); // SQLITE DB 저장소 옮김

        //  탄수화물, 단백질, 지방, 포화지방, 식이섬유소, 콜레스테롤, 나트륨 (7 things)
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
                Cursor c = db_.rawQuery(query, null);
                if (c != null && c.moveToFirst()) {
                    /*
                        0 : 열량 1 : 탄수화물 2 : 단백질 3 : 지방 4 : 당류 5 : 나트륨 6 : 콜레스테롤 7 : 포화지방산 8 : 트랜스지방 [Array 인덱스 이용해서 데이터 참조!]
                     */
        /*
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

        anyChartView_mychart.setChart(circularGauge);


        */

        //리싸이클러 뷰 코딩-------------------------------------------------------
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();

        mAdapter = new CustomAdapter(mArrayList);
        mAdapter.setTextSizes(1); //리싸이클러 뷰 글씨 크기
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);




        //탄수화물 //영양성분표를 분석한 결과를 nut_list에서 받아온다!
        DictionaryCameraAnalyze data_tan = new DictionaryCameraAnalyze("1","탄수화물",d_tan.toString());
        mArrayList.add(data_tan);
        //단백질
        DictionaryCameraAnalyze data_dan = new DictionaryCameraAnalyze("2","단백질",d_dan.toString());
        mArrayList.add(data_dan);
        //지방
        DictionaryCameraAnalyze data_ji = new DictionaryCameraAnalyze("3","지방",d_ji.toString());
        mArrayList.add(data_ji);
        //포화지방
        DictionaryCameraAnalyze data_poji = new DictionaryCameraAnalyze("4","포화지방",d_poji.toString());
        mArrayList.add(data_poji);
        // 식이섬유
        DictionaryCameraAnalyze data_sik = new DictionaryCameraAnalyze("5","식이섬유",d_sik.toString());
        mArrayList.add(data_sik);
        // 나트륨
        DictionaryCameraAnalyze data_na = new DictionaryCameraAnalyze("6","나트륨",d_na.toString());
        mArrayList.add(data_na);
        // 콜레스테롤
        DictionaryCameraAnalyze data_col = new DictionaryCameraAnalyze("7","콜레스테롤",d_col.toString());
        mArrayList.add(data_col);


        mAdapter.notifyDataSetChanged();

        //권장섭취량 추천 버튼 누르면 영양성분 섭취현황에 따라 DialogNotOverActivity or DialogOverActivity로 이동
        Button button_chucheon = (Button)findViewById(R.id.button_eating_chucheon);
        button_chucheon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CameraAnalyzeActivity.this, DialogOverActivity.class);
                intent.putExtra("map",(Serializable) map);
                startActivity(intent);

            }
        });

       // Dialog dialog01;
       // dialog01 = new Dialog(CameraAnalyzeActivity.this);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
    }

    private static boolean testInteger(char str) {
        try {
            Integer.parseInt(str+"");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static String getStringPart(String str) {
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            if (!testInteger(str.charAt(i))) {
                s += str.charAt(i);
            } else {
                break;
            }
        }
        return s;
    }

    private static String getIntegerPart(String str) {
        String s = "";
        for (int i = 0; i < str.length(); i++) {
            if (testInteger(str.charAt(i))) {
                s += str.charAt(i);
            }
        }
        return s;
    }
}