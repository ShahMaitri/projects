package com.project.presentme.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by maitri on 8/1/18.
 *
 */

public class DatabaseUtils extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Present.db";

    private final String TABLE_DISTANCE = "Distance";
    private final String DIST_ID = "dist_id";
    private final String DIST_DATE_TIME = "date_time";
    private final String DIST_VAL = "distance";

    private final String CREATE_DISTANCE = "CREATE TABLE " + TABLE_DISTANCE + " (" + DIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DIST_DATE_TIME + " TEXT, " + DIST_VAL + " TEXT)";

    private final String DROP_DISTANCE = "DROP TABLE IF EXISTS " + TABLE_DISTANCE;

    public DatabaseUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DISTANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(DROP_DISTANCE);
        onCreate(db);
    }

    public void insertDistance(Distance distance) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(DIST_DATE_TIME, distance.distDateTime);
        values.put(DIST_VAL, distance.distValue);

// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_DISTANCE, null, values);
        this.close();
    }

    public ArrayList<Distance> getDistances() {
        ArrayList<Distance> distances = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_DISTANCE, new String[] {DIST_ID, DIST_DATE_TIME, DIST_VAL}, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Distance distance = new Distance();
                distance.setDistId(cursor.getString(cursor.getColumnIndex(DIST_ID)));
                distance.setDistDateTime(cursor.getString(cursor.getColumnIndex(DIST_DATE_TIME)));
                distance.setDistValue(cursor.getString(cursor.getColumnIndex(DIST_VAL)));
                distances.add(distance);
            }
        }
        cursor.close();
        return distances;
    }
}
