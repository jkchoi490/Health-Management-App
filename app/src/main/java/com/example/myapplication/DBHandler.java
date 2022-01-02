package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {


    private static final String DB_NAME = "userDB";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "user";

    private static final String ID_COL = "id";

    private static final String NAME_COL = "name";

    // 성별 column.
    private static final String GENDER_COL = "gender";

    // 평소운동량 column.
    private static final String EXERCISE_COL = "exercise";

    //키 column.
    private static final String HEIGHT_COL = "height";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + GENDER_COL + " TEXT,"
                + EXERCISE_COL + " TEXT,"
                + HEIGHT_COL + " INTEGER)";

        db.execSQL(query);
    }


    public void addNewCourse(String sName, String sGender, String sExercise, String sHeight) {


        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();


        values.put(NAME_COL, sName);
        values.put(GENDER_COL, sGender);
        values.put(EXERCISE_COL, sExercise);
        values.put(HEIGHT_COL, sHeight);


        db.insert(TABLE_NAME, null, values);

        db.close();
    }

    public ArrayList<CourseModal> readCourses() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<CourseModal> courseModalArrayList = new ArrayList<>();

        if (cursorCourses.moveToFirst()) {
            do {
                courseModalArrayList.add(new CourseModal(cursorCourses.getString(1),
                        cursorCourses.getString(2),
                        cursorCourses.getString(4),
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
