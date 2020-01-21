package com.eldissidemissions.mynotesapp.db;

import android.provider.BaseColumns;

//class ini akan memudahkan kita mengakses nama table dan nama kolom
public class DatabaseContract {
    public static String TABLE_NAME = "note";

    public static final class NoteColumns implements BaseColumns{
        public static String TITLE = "title";
        public static String DESCRIPTION = "description";
        public static String DATE = "date";

    }
}
