package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandlerNutrition extends SQLiteOpenHelper {

    private static final String DB_NAME = "NDB";

    private static final int DB_VERSION = 4;

    private static final String TABLE_NAME = "n_table";


    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    //성별 column.
    private static final String GENDER_COL = "gender";
    //평소운동량 column.
    private static final String EXERCISE_COL = "exercise";
    //키 column.
    private static final String HEIGHT_COL = "height";

    private static final String CAL_COL = "cal";
    private static final String TAN_COL = "tan";
    private static final String DAN_COL = "dan";
    private static final String JI_COL = "ji";
    private static final String POJI_COL = "poji";
    private static final String SIK_COL = "sik";
    private static final String COL_COL = "col";
    private static final String NA_COL = "na";

    public DBHandlerNutrition(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + GENDER_COL + " TEXT,"
                + EXERCISE_COL + " TEXT,"
                + HEIGHT_COL + " INTEGER,"
                + CAL_COL + " TEXT,"
                + TAN_COL + " TEXT,"  //+ TAN_COL + "DOUBLE,"
                + DAN_COL + " TEXT,"
                + JI_COL + " TEXT,"
                + POJI_COL + " TEXT,"
                + SIK_COL + " TEXT,"
                + COL_COL + " TEXT,"
                + NA_COL + " TEXT)";

        db.execSQL(query);
    }


    public void addNewCourseN(String sName, String sGender, String sExercise, String sHeight,
    String sCal, String sTan, String sDang, String sJi, String sPoji, String sSik, String sCol, String sNa
    ) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(NAME_COL, sName);
        values.put(GENDER_COL, sGender);
        values.put(EXERCISE_COL, sExercise);
        values.put(HEIGHT_COL, sHeight);
        values.put(CAL_COL, sCal);
        values.put(TAN_COL, sTan);
        values.put(DAN_COL, sDang);
        values.put(JI_COL, sJi);
        values.put(POJI_COL, sPoji);
        values.put(SIK_COL, sSik);
        values.put(COL_COL, sCol);
        values.put(NA_COL, sNa);

        db.insert(TABLE_NAME, null, values);

        db.close();
    }


    public ArrayList<CourseModalN> readCoursesN() {

        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<CourseModalN> courseModalArrayList = new ArrayList<>();

        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(new CourseModalN(
                        cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(4),
                        cursorCourses.getString(3),
                        cursorCourses.getString(5),
                        cursorCourses.getString(6),
                        cursorCourses.getString(7),
                        cursorCourses.getString(8),
                        cursorCourses.getString(9),
                        cursorCourses.getString(10),
                        cursorCourses.getString(11),
                        cursorCourses.getString(12)));
            } while (cursorCourses.moveToNext());

        }
        cursorCourses.close();
        return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
