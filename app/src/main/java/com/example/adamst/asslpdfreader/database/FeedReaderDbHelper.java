package com.example.adamst.asslpdfreader.database;

/**
 * Created by Adamst on 13/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.adamst.asslpdfreader.database.FeedReaderContract.FileEntry;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_FILE_ENTRIES =
            "CREATE TABLE " + FileEntry.TABLE_NAME + " (" +
                    FileEntry._ID + " INTEGER PRIMARY KEY," +
                    FileEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    FileEntry.COLUMN_NAME_DATE_ADDED + TEXT_TYPE + " )";

    private static final String SQL_DELETE_FILE_ENTRIES =
            "DROP TABLE IF EXISTS " + FileEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        // Creates the database (FeedReader.db) if there is not a file already.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        // Create the database and execution creation SQL for each table.
        db.execSQL(SQL_CREATE_FILE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(newVersion>oldVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_FILE_ENTRIES);
            onCreate(db);
        }
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void add_table_row(SQLiteDatabase db, ContentValues values, String tableName) throws Exception{

        // Insert the new row TODO: the primary key value of the new row
        db.insert(tableName, null, values);
    }

    public ContentValues get_table_rows(SQLiteDatabase db, String[] selectedColumns, String whereColumnName, String whereColumnValue, String tableName) throws Exception {

        // Filter results; selected tables have already been sent.
        String selection = whereColumnName + " = ?";
        String[] selectionArgs = { whereColumnValue };

        // How you want the results sorted in the resulting Cursor
        // String sortOrder =
        //        FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor c = db.query(
                tableName,                                // The table to query
                selectedColumns,                          // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        // Get the return values
        ContentValues returnValues = new ContentValues();

        if( c != null && c.moveToFirst() ) {

            if(selectedColumns.length >= 1) {
                for (int i = 0; i < selectedColumns.length; i++) {
                    returnValues.put(selectedColumns[i], c.getString(c.getColumnIndex(selectedColumns[i])));
                }
                c.close();
                return returnValues;
            }
            else {
                Log.d("get_database_values", "No projection tags found.");
                returnValues.put("error", "Column projection error in get_table_values");
                c.close();
                return returnValues;
            }
        }
        else {
            Log.d("get_database_values", "Test: get_database_values failed to retrieve cursor value.");
            returnValues.put("error", "Failed to retrieve cursor value.");
            return returnValues;
        }
    }

    public Boolean update_table_row(SQLiteDatabase db, String columnName, String oldValue, String newValue, String tableName) throws Exception{

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(columnName, newValue);

        // Which row to update, based on the title
        String selection = columnName + " LIKE ?";
        String[] selectionArgs = { oldValue };

        int count = db.update(
                tableName,
                values,
                selection,
                selectionArgs);

        if (count == 0 || count == -1)
            return false;
        else
            return true;
    }

    public void remove_table_row(SQLiteDatabase db, String columnName, String columnValue, String tableName) throws Exception{

        // Remove the test database
        // Define 'where' part of query.
        String selection = columnName + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { columnValue };

        // Issue SQL statement.
        db.delete(tableName, selection, selectionArgs);
    }
}
