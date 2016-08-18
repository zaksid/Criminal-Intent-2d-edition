package com.zaksid.dev.android.training.bignerdranch.criminalintent.database;

/**
 * Defines database schema
 */
public class CrimeDbSchema {
    public final static class CrimeTable {
        public final static String NAME = "crimes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
        }
    }
}
