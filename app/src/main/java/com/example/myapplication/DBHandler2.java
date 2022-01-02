package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler2 extends SQLiteOpenHelper {

    // 식단 저장용 DB
    //식단목록
    private static final String DB_NAME = "myfoodDB";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "myfood";

    private static final String ID_COL = "id";

    private static final String DATE_COL = "date";

    private static final String DIET_COL = "diet";

    // 식단 양 입력 (1인분, 2인분) column.
    private static final String SIZE_COL = "size";

    // 메뉴 입력하는 column(메뉴이름)
    private static final String MENU_COL = "menu";

    public DBHandler2(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DATE_COL + " TEXT,"
                + DIET_COL + " TEXT,"
                + SIZE_COL + " TEXT,"
                + MENU_COL + " TEXT)";

        db.execSQL(query);
    }


    public void addNewCourse(String courseName, String courseDuration, String courseDescription, String courseTracks) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DATE_COL, courseName);
        values.put(DIET_COL, courseDuration);
        values.put(SIZE_COL, courseDescription);
        values.put(MENU_COL, courseTracks);


        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<CourseModal2> readCourses() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<CourseModal2> courseModalArrayList = new ArrayList<>();

        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(new CourseModal2(cursorCourses.getString(1),
                        cursorCourses.getString(4),
                        cursorCourses.getString(2),
                        cursorCourses.getString(3)));
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
