package com.example.adamst.asslpdfreader.database;

import android.provider.BaseColumns;

/**
 * Created by Adamst on 13/10/2016.
 */

public final class FeedReaderContract implements BaseColumns {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FileEntry implements BaseColumns {
        public static final String TABLE_NAME = "file";
        public static final String COLUMN_NAME_BOOK_NAME = "book_name";
       //  public static final String COLUMN_BOOK_PAGES = "book_pages";
        // public static final String COLUMN_AUTHOR_NAME = "author_name";
        public static final String COLUMN_NAME_DATE_ADDED = "date_added";
        // public static final String COLUMN_NAME_DATE_LAST_READ = "date_last_read";
    }
}
