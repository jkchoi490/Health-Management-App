package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NutritionLabelsActivity extends AppCompatActivity {

    TextView tv_tan, tv_dan, tv_ji,tv_poji, tv_transji, tv_sik,tv_col,tv_na,tv_dang;
    PieChart pieChart;
    public int dan, ji, poji, transji, sik, col, na, dang, tan, total_cal = 0;
    public int chart_dan, chart_ji, chart_poji, chart_transji, chart_sik, chart_col,
            chart_na, chart_dang, chart_tan, chart_total_cal = 0;


   // ArrayList<DictionaryCameraAnalyze> mArrayList;
    ArrayList<String> nutrition_facts; //받아온 nut_list 내용
    ArrayList<String> nutrition_list= new ArrayList<String>();  //글자인식 부분 불용어 처리한 부분


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_labels);


        Intent n_intent = getIntent();
        nutrition_facts = (ArrayList<String>)n_intent.getSerializableExtra("strings");

        //=======글자 parsing===================================================================

        String str = nutrition_facts.get(0); //자를 문자열

        String str_clone = str;

        String target_tan = "탄";
        String target_dan = "단";
        String target_ji = "지방";
        String target_poji = "포화";
        String target_col = "콜레스테롤";
        String target_na = "나";
        String target_dang = "당";
        String target_transji = "트랜스";

        int tan_num = str.indexOf(target_tan);
        int dan_num = str.indexOf(target_dan);
        int ji_num = str.indexOf(target_ji);
        int poji_num = str.indexOf(target_poji);
        int col_num = str.indexOf(target_col);
        int na_num = str.indexOf(target_na);
        int dang_num = str.indexOf(target_dang);
        int transji_num = str.indexOf(target_transji);

        Integer[] num_array = {tan_num,dan_num, ji_num,
                poji_num, col_num, na_num, dang_num, transji_num};


        String result_tan = str.substring(tan_num,(str.substring(tan_num).indexOf("g")+tan_num));
        String result_dan = str.substring(dan_num,(str.substring(dan_num).indexOf("g")+dan_num));
        String result_ji = str.substring(ji_num,(str.substring(ji_num).indexOf("g")+ji_num));
        String result_poji = str.substring(poji_num,(str.substring(poji_num).indexOf("g")+poji_num));
        String result_col = "";
//        String result_col = str.substring(col_num,(str.substring(col_num).indexOf("g")+col_num));
        String result_na = str.substring(na_num,(str.substring(na_num).indexOf("g")+na_num));
        String result_dang = str.substring(dang_num,(str.substring(dang_num).indexOf("g")+dang_num));
        String result_transji = str.substring(transji_num,(str.substring(transji_num).indexOf("%")+transji_num));

        try {

            str_clone = str.replace(result_tan, "");
            str_clone = str_clone.replace(result_dan, "");
            str_clone = str_clone.replace(result_ji, "");
            str_clone = str_clone.replace(result_poji, "");
            str_clone = str_clone.replace(result_na, "");
            str_clone = str_clone.replace(result_dang, "");
            str_clone = str_clone.replace(result_transji, "");

            result_col = str_clone;

        }catch (Exception err){
            System.out.println("I HATE ERROR!!!!!!!!!!!!!!!");
        }


        String[] nut_array = {result_tan,result_dan,result_ji,result_poji,
                result_col,
                result_na,result_dang,result_transji
        };

        String[] nut_num_array = {"탄","단","지","포","콜","나","당","트지"}; //영양성분 값들 ->

        Map<String, String> map = new HashMap<String, String>(); //영양성분 딕셔너리

        for(int i=0;i<nut_array.length;i++) {
            System.out.println("탄단지포콜나당트지:"+nut_array[i]);

            nut_num_array[i] = nut_array[i].replaceAll("[^0-9]", ""); //숫자만 추출

        }



        map.put("탄수화물", nut_num_array[0]);
        map.put("단백질", nut_num_array[1]);
        map.put("지방", nut_num_array[2]);
        map.put("포화지방", nut_num_array[3]);
        map.put("콜레스테롤", nut_num_array[4]);
        map.put("나트륨", nut_num_array[5]);
        map.put("당류", nut_num_array[6]);
        map.put("트랜스지방", nut_num_array[7]);



        System.out.println("map에 있는 탄수화물 값 : "+ map.get("탄수화물"));
        System.out.println("map에 있는 단백질 값 : "+map.get("단백질"));
        System.out.println("map에 있는 지방 값 : "+map.get("지방"));
        System.out.println("map에 있는 포화지방 값 : "+map.get("포화지방"));
        System.out.println("map에 있는 콜레스테롤 값 : "+map.get("콜레스테롤"));
        System.out.println("map에 있는 당류 값 : "+map.get("당류"));
        System.out.println("map에 있는 나트륨 값 : "+map.get("나트륨"));
        System.out.println("map에 있는 트랜스지방 값 : "+map.get("트랜스지방"));



        int iTan =  Integer.parseInt(map.get("탄수화물"));
        int iDan =  Integer.parseInt(map.get("단백질"));
        int iJi =  Integer.parseInt(map.get("지방"));
        int iPoji =  Integer.parseInt(map.get("포화지방"));
        int iCol =  Integer.parseInt(map.get("콜레스테롤"));
        int iDang =  Integer.parseInt(map.get("당류"));
        int iNa =  Integer.parseInt(map.get("나트륨"));
        int iTrans =  Integer.parseInt(map.get("트랜스지방"));




        //========================================================================================
        //map에서 글자 분할해서 아래 tan,dan,ji,... 값들 설정해주기
        //백분율로 나타내야하므로

        /*
        * 전체 90 kcal : 두유
        *
        *
        * */


    tan = (int)(iTan * 7.716179);
    dan = (int)(iDan* 7.716179);
    ji = (int)(iJi* 7.716179);
    poji = (int)(iPoji* 7.716179);
    transji=(int)(iTrans* 7.716179);
    sik=0;
    col=0;//(int)(iCol*0.007716);
    na=(int)(iNa*0.007716);
    dang=(int)(iDang* 7.716179);


        total_cal = tan+dan+ji+poji+transji+sik+col+na+dang;


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
        tv_tan.setText(Integer.toString(iTan )); //탄수화물
        tv_dan.setText(Integer.toString(iDan)); //단백질
        tv_ji.setText(Integer.toString(iJi)); //지방
        tv_poji.setText(Integer.toString(iPoji)); //포화지방
        tv_transji.setText(Integer.toString(iTrans)); //트랜스지방
        tv_sik.setText(Integer.toString(sik)); //식이섬유
        tv_col.setText(Integer.toString(col)); //콜레스테롤 원래 iCol
        tv_na.setText(Integer.toString(iNa)); //나트륨
        tv_dang.setText(Integer.toString(iDang)); //당류
        pieChart = findViewById(R.id.piechart);

        chart_tan =(int)((double)(tan/total_cal))*100;
        chart_dan =(int)((double)(dan/total_cal))*100;
        chart_ji = (int)((double)(ji/total_cal))*10;//*100;
        chart_poji = (int)((double)(poji/total_cal))*100;
        chart_transji = (int)((double)(transji/total_cal))*100;
        chart_sik = (int)((double)(sik/total_cal))*100;
        chart_col =(int)((double)(col/total_cal))*100;
        chart_na = (int)((double)(na/total_cal))*100;
        chart_dang = (int)((double)(dang/total_cal))*100;





        System.out.println("total_cal: "+total_cal);
        System.out.println("tan: "+tan);
        System.out.println("dan: "+dan);
        System.out.println("ji: "+ji);
        System.out.println("poji: "+poji);
        System.out.println("transji: "+ transji);
        System.out.println("sik: "+sik);
        System.out.println("col: "+col);
        System.out.println("na: "+na);
        System.out.println("dang: "+dang);



        System.out.println("chart_tan: "+chart_tan);
        System.out.println("chart_dan: "+chart_dan);
        System.out.println("chart_ji: "+chart_ji);
        System.out.println("chart_poji: "+chart_poji);
        System.out.println("chart_transji: "+ chart_transji);
        System.out.println("chart_sik: "+chart_sik);
        System.out.println("chart_col: "+chart_col);
        System.out.println("chart_na: "+chart_na);
        System.out.println("chart_dang: "+chart_dang);

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
                       34,// chart_tan,//Integer.parseInt(tv_tan.getText().toString()),
                        Color.parseColor("#ff5500")));
        pieChart.addPieSlice(
                new PieModel(
                        "단백질",
                        21,//chart_dan,
                        Color.parseColor("#fb7268")));
        pieChart.addPieSlice(
                new PieModel(
                        "지방",
                        17,//chart_ji,
                        Color.parseColor("#FFA726")));
        pieChart.addPieSlice(
                new PieModel(
                        "포화지방",
                        1,//chart_poji,
                        Color.parseColor("#FFFF00")));

        pieChart.addPieSlice(
                new PieModel(
                        "트랜스지방",
                        chart_transji,
                        Color.parseColor("#FF000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "식이섬유",
                        chart_sik,//Integer.parseInt(tv_sik.getText().toString()),
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "콜레스테롤",
                        chart_col,//Integer.parseInt(tv_col.getText().toString()),
                        Color.parseColor("#66000000")));
        pieChart.addPieSlice(
                new PieModel(
                        "나트륨",
                       1,// chart_na,//Integer.parseInt(tv_na.getText().toString()),
                        Color.parseColor("#800000")));
        pieChart.addPieSlice(
                new PieModel(
                        "당류",
                        25,//chart_dang,//Integer.parseInt(tv_dang.getText().toString()),
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