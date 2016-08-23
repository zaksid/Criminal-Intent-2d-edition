package com.zaksid.dev.android.training.bignerdranch.criminalintent.database;

/**
 * Defines database schema
 */
public class CrimeDbSchema {
    public final static class CrimeTable {
        public final static String NAME = "crimes";

        public final static class Cols {
            public final static String UUID = "uuid";
            public final static String TITLE = "title";
            public final static String DATE = "date";
            public final static String SOLVED = "solved";
            public final static String SUSPECT = "suspect";
        }
    }
}
