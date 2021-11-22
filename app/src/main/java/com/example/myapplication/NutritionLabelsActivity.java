package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NutritionLabelsActivity extends AppCompatActivity {

    TextView tv_tan, tv_dan, tv_ji,tv_poji, tv_transji, tv_sik,tv_col,tv_na,tv_dang;
    PieChart pieChart;
    public int dan, ji, poji, transji, sik, col, na, dang, tan = 0;

    ArrayList<String> nutrition_facts; //받아온 nut_list 내용
    ArrayList<String> nutrition_list= new ArrayList<String>();  //글자인식 부분 불용어 처리한 부분
    ArrayList<DictionaryCameraAnalyze> mArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_labels);

        //fab 버튼(변경될 수 있음)으로 글자인식한 부분 받아오기
        Intent n_intent = getIntent();
        nutrition_facts = (ArrayList<String>)n_intent.getSerializableExtra("strings");
        System.out.println("받아온 nut_list 내용(nutrition_facts):"+nutrition_facts);

        System.out.println("nutrition_facts[0]:"+ nutrition_facts.get(0));
        //----------------영양성분표 받아온 내용---nutrition_facts 리스트의
        // 내용을 nut_list에 넣어야함*************************************-----------------------




        ArrayList<String> nut_list = new ArrayList<String>(); //nutrition_facts 리스트 내용 넣기
        List<String> nut_name_list = new ArrayList<>();
        List<Integer> nut_size_list = new ArrayList<>();



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

         for (String mapkey : map.keySet()){
              System.out.println("key:"+mapkey+",value:"+map.get(mapkey));
          }

        // System.out.println("탄수화물 값 "+map.get("탄수화물 "));
        //  System.out.println("단백질 값(value)"+map.get("단백질 ")); //이거 보여주면 되것다!

        Integer d_tan = map.get("탄수화물 ");
        Integer d_dan = map.get("단백질 ");
        Integer d_ji = map.get("지방 ");
        Integer d_poji = map.get("포화지방 ");
       // Integer d_sik = map.get("식이섬유 ");
        Integer d_na = map.get("나트륨 ");
        Integer d_col = map.get("콜레스테롤 ");

        List<DataEntry> data = new ArrayList<>(); //탄,단,지,포지,식이,나트륨,콜레 성분

        data.add(new ValueDataEntry("탄수화물", d_tan));
        data.add(new ValueDataEntry("단백질", d_dan));
        data.add(new ValueDataEntry("지방", d_ji));
        data.add(new ValueDataEntry("포화지방", d_poji));
       // data.add(new ValueDataEntry("식이섬유", d_sik));
        data.add(new ValueDataEntry("나트륨", d_na));
        data.add(new ValueDataEntry("콜레스테롤", d_col));

        //탄수화물 //영양성분표를 분석한 결과를 nut_list에서 받아온다!
        DictionaryCameraAnalyze data_tan = new DictionaryCameraAnalyze("1","탄수화물",d_tan.toString());
       // mArrayList.add(data_tan); //error!!!!!!!!!!!!!!!!!!!!!!
        //단백질
        DictionaryCameraAnalyze data_dan = new DictionaryCameraAnalyze("2","단백질",d_dan.toString());
       // mArrayList.add(data_dan);
        //지방
        DictionaryCameraAnalyze data_ji = new DictionaryCameraAnalyze("3","지방",d_ji.toString());
       // mArrayList.add(data_ji);
        //포화지방
        DictionaryCameraAnalyze data_poji = new DictionaryCameraAnalyze("4","포화지방",d_poji.toString());
       // mArrayList.add(data_poji);
        // 식이섬유
        //DictionaryCameraAnalyze data_sik = new DictionaryCameraAnalyze("5","식이섬유",d_sik.toString());
       // mArrayList.add(data_sik);
        // 나트륨
        DictionaryCameraAnalyze data_na = new DictionaryCameraAnalyze("5","나트륨",d_na.toString());
       // mArrayList.add(data_na);
        // 콜레스테롤
        DictionaryCameraAnalyze data_col = new DictionaryCameraAnalyze("6","콜레스테롤",d_col.toString());
        //mArrayList.add(data_col);

//-----------------------------------------------

        //nutrition_facts에서 글자 분할해서 아래 tan,dan,ji,... 값들 설정해주기
        tan = 50;
        dan = 20;
        ji = 10;
        poji = 10;
        transji=10;
        sik=10;
        col=10;
        na=10;
        dang=10;


        tv_tan = findViewById(R.id.textView_tan);
        tv_dan= findViewById(R.id.textView_dan);
        tv_ji= findViewById(R.id.textView_ji);
        tv_poji= findViewById(R.id.textView_poji);
        tv_transji= findViewById(R.id.textView_transji);
        tv_sik= findViewById(R.id.textView_sik);
        tv_na= findViewById(R.id.textView_na);
        tv_col = findViewById(R.id.textView_col);
        tv_dang= findViewById(R.id.textView_dang);

        // Set the percentage of language used
        tv_tan.setText(Integer.toString(tan)); //탄수화물
        tv_dan.setText(Integer.toString(dan)); //단백질
        tv_ji.setText(Integer.toString(ji)); //지방
        tv_poji.setText(Integer.toString(poji)); //포화지방
        tv_transji.setText(Integer.toString(transji)); //트랜스지방
        tv_sik.setText(Integer.toString(sik)); //식이섬유
        tv_col.setText(Integer.toString(col)); //콜레스테롤
        tv_na.setText(Integer.toString(na)); //나트륨
        tv_dang.setText(Integer.toString(dang)); //당류
        pieChart = findViewById(R.id.piechart);
        setData();

        //버튼 클릭시 액티비티 이동 button_blue

        Button button_camera = (Button)findViewById(R.id.button_blue);
        button_camera.setOnClickListener(v -> {
            Intent intent_next = new Intent(NutritionLabelsActivity.this, MyNutritionsActivity.class);
            intent_next.putExtra("map",(Serializable) map); //map을 넘김
            startActivity(intent_next);
        });


    }

    private void setData()
    {
        // Set the data and color to the pie chart
        pieChart.addPieSlice(
                new PieModel("탄수화물",
                        Integer.parseInt(tv_tan.getText().toString()),
                        //tan,
                        Color.parseColor("#FF0000")));
        pieChart.addPieSlice(
                new PieModel(
                        "단백질",
                        Integer.parseInt(tv_dan.getText().toString()),
                        //dan,
                        Color.parseColor("#fb7268")));
        pieChart.addPieSlice(
                new PieModel(
                        "지방",
                        Integer.parseInt(tv_ji.getText().toString()),
                       // ji,
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "포화지방",
                        Integer.parseInt(tv_poji.getText().toString()),
                      //  poji,
                        Color.parseColor("#FFFF00")));

        pieChart.addPieSlice(
                new PieModel(
                        "트랜스지방",
                        Integer.parseInt(tv_transji.getText().toString()),
                        //transji,
                        Color.parseColor("#FF000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "식이섬유",
                        Integer.parseInt(tv_sik.getText().toString()),
                        //sik,
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "콜레스테롤",
                        Integer.parseInt(tv_col.getText().toString()),
                        // col,
                        Color.parseColor("#66000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "나트륨",
                        Integer.parseInt(tv_na.getText().toString()),
                        //na,
                        Color.parseColor("#800000")));
        pieChart.addPieSlice(
                new PieModel(
                        "당류",
                        Integer.parseInt(tv_dang.getText().toString()),
                       // dang,
                        Color.parseColor("#FFBB86FC")));
        pieChart.startAnimation();
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