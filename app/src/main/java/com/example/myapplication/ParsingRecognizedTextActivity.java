package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParsingRecognizedTextActivity extends AppCompatActivity {

    ArrayList<String> nutrition_facts; //받아온 nut_list 내용
    ArrayList<String> nutrition_list= new ArrayList<String>();  //글자인식 부분 불용어 처리한 부분

    String tan = "";
    String tan_value =""; //탄수화물

    String dan = "";
    String dan_value = ""; //단백질

    String ji = "";
    String ji_value = ""; //지방

    String poji = "";
    String poji_value = ""; //포화지방

    String col = "";
    String col_value = ""; //콜레스테롤

    String na = "";
    String na_value = ""; //나트륨

    String dang = "";
    String dang_value = ""; //당류

    String transji = "";
    String transji_value = ""; //트랜스지방

    String sik = "";
    String sik_value = ""; //식이섬유




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing_recognized_text);

        Intent n_intent = getIntent();
        nutrition_facts = (ArrayList<String>)n_intent.getSerializableExtra("strings");

        TextView ptext = findViewById(R.id.pass_text);
        ptext.setText(nutrition_facts.get(0));



        /*

         System.out.println("받아온 nut_list 내용(nutrition_facts):"+nutrition_facts);
        System.out.println("nutrition_facts[0]:"+ nutrition_facts.get(0));
        *           nutrition_facts[0]:영양정보
                    종 내용량 190m
                    90KC
                    나트륨 90 mg 5% |탄수화물 3g 2%
                    당류 6g 6% |지방 4.1g 8%
                    트랜스지방 0g 0%| 포화지방 0.5g 3
                    골레스테롤 0 mg 0% 단백질 5g 9%
        *
        * */



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
        String result_transji = str.substring(transji_num,(str.substring(transji_num).indexOf("g")+transji_num));

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


       // String[] array = str.split(" ");

        /*
        for(int i=0;i<array.length;i++) {
            System.out.println(array[i]);
           // String intStr = str.replaceAll("[^0-9]", "");
        }





        for(int i=0;i<array.length;i++) {



                if(array[i].contains("단백질")){
                   dan = array[i];
                   dan_value = array[i+1];
                }
                else{
                    if(array[i].contains("단") || array[i].contains("백") ||array[i].contains("질")  ){
                        dan = array[i];
                        dan_value = array[i+1];
                    }
                    else{
                        if(array[i].contains("탄수화물")){
                            tan = array[i];
                            tan_value = array[i+1];
                        }
                        else{
                            if(array[i].contains("탄") || array[i].contains("수") ||array[i].contains("화") ||array[i].contains("물") ){
                                tan = array[i];
                                tan_value = array[i+1];
                            }

                            else{
                                if(array[i].contains("지방") == true && array[i].contains("트랜스") == true){
                                    transji = array[i];
                                    transji_value = array[i+1];
                                }

                                else{

                                    if(array[i].contains("지방") == true && array[i].contains("트랜스") == false && array[i].contains("포화") == false){
                                        ji = array[i];
                                        ji_value = array[i+1];
                                    }
                                    else{
                                        if(array[i].contains("지방") == true && array[i].contains("트랜스") == false && array[i].contains("포화") == true || array[i].contains("포") == true){
                                            poji = array[i];
                                            poji_value = array[i+1];
                                        }

                                        else {
                                            if(array[i].contains("나트륨") == true || array[i].contains("나") == true || array[i].contains("나트") == true){
                                                na = array[i];
                                                na_value = array[i+1];
                                            }
                                            else{

                                                if(array[i].contains("콜레스테롤") == true || array[i].contains("콜") == true || array[i].contains("콜레스") == true){
                                                    col = array[i];
                                                    col_value = array[i+1];
                                                }

                                                else{
                                                    if(array[i].contains("당류") == true || array[i].contains("당") == true ){
                                                        dang = array[i];
                                                        dang_value = array[i+1];
                                                    }


                                                }

                                            }





                                        }



                                    }





                                }



                            }




                        }
                    }
                }

        } //for문 끝

        */




        /*

        System.out.println("tan :"+tan+"\n"+"tan_value :"+tan_value);
        System.out.println("dan :"+dan+"\n"+"dan_value :"+dan_value);
        System.out.println("ji :"+ji+"\n"+"ji_value :"+ji_value);
        System.out.println("poji :"+poji+"\n"+"poji_value :"+poji_value);
        System.out.println("col :"+col+"\n"+"col_value :"+col_value);
        System.out.println("na :"+na+"\n"+"na_value :"+na_value);
        System.out.println("dang :"+dang+"\n"+"dang_value :"+dang_value);
        System.out.println("transji :"+transji+"\n"+"transji_value :"+transji_value);
        */
        //System.out.println("sik :"+tan+"\n"+"tan_value :"+tan_value);

        //TextView result_text = findViewById(R.id.processing_text);

        List<String> nut_name_list = new ArrayList<>();
        List<Integer> nut_size_list = new ArrayList<>();

/*
        try{


                for (int i = 0; i < array.length; i++) {
                nut_name_list.add(getStringPart(array[i]));
                nut_size_list.add(Integer.parseInt(getIntegerPart(array[i])));
                    System.out.println("nut_name_list :"+nut_name_list);
            }


            Map<String, Integer> map = new LinkedHashMap<>(); //영양성분이랑 탄=몇g, 단=몇g 딕셔너리 형태로 제작
            for (int i=0; i<nut_name_list.size(); i++) {
                map.put(nut_name_list.get(i), nut_size_list.get(i));
            }
            System.out.println(map);

        } catch(NumberFormatException ex){
        System.out.println("오류다슬프다");
        }

*/


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


