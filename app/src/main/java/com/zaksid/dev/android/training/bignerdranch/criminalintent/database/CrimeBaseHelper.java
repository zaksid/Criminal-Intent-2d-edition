package com.zaksid.dev.android.training.bignerdranch.criminalintent.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zaksid.dev.android.training.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;

/**
 * Performs base actions with database:
 * <ul>
 * <li>Checks if the database already exists.</li>
 * <li>If it does not, create it and create the tables and initial data it needs.</li>
 * <li>If it does, open it up and see what version of DbSchema it has.</li>
 * <li>If it is an old version, run code to upgrade it to a newer version.</li>
 * </ul>
 */
public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + CrimeTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            CrimeTable.Cols.UUID + ", " +
            CrimeTable.Cols.TITLE + ", " +
            CrimeTable.Cols.DATE + ", " +
            CrimeTable.Cols.SOLVED +
            ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
