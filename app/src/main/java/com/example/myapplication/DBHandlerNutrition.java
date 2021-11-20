package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandlerNutrition extends SQLiteOpenHelper {
    // creating a constant variables for our database.
    // below variable is for our database name.
    private static final String DB_NAME = "NDB";

    // below int is our database version
    private static final int DB_VERSION = 4;

    // table name.
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

    // creating a constructor for our database handler.
    public DBHandlerNutrition(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
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
                + NA_COL + " TEXT)"; //TEXT였는데 바꿈

        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query);
    }

    // this method is use to add new course to our sqlite database.
    public void addNewCourseN(String sName, String sGender, String sExercise, String sHeight,
    String sCal, String sTan, String sDang, String sJi, String sPoji, String sSik, String sCol, String sNa
    ) {

        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();


        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();

        // on below line we are passing all values
        // along with its key and value pair.
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
        // values.put(DAILY_CALORIES,dailycalories); //작성한거

        // after adding all values we are passing
        // content values to our table.
        db.insert(TABLE_NAME, null, values);

        // at last we are closing our
        // database after adding database.
        db.close();
    }

    // we have created a new method for reading all the courses.
    public ArrayList<CourseModalN> readCoursesN() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();

        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        // on below line we are creating a new array list.
        ArrayList<CourseModalN> courseModalArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
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
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        return courseModalArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
