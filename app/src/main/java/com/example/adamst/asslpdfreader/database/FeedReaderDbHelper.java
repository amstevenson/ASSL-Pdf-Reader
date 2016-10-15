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

import java.util.ArrayList;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedReader.db";

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

    public long addTableRow(SQLiteDatabase db, ContentValues values, String tableName) throws Exception{

        // Insert the new row; returns the new ID as a long value.
        return(db.insert(tableName, null, values));
    }

    public ArrayList<ContentValues> getTableRows(SQLiteDatabase db, String[] selectedColumns,
                                      ContentValues whereValues, String tableName, String sortBy) throws Exception {

        // Create the selection statement; the columns for the where clause.
        // Also create the values for those where clauses.
        String selection = "";
        String[] selectionArgs = new String[whereValues.size()];
        int i = 0;
        if(whereValues.size() > 0) {
            for (String whereKey : whereValues.keySet()) {
                if (i == 0) {
                    selection += whereKey + " = ? ";
                } else {
                    selection += "AND " + whereKey + " = ? ";
                }

                selectionArgs[i] = whereValues.get(whereKey).toString();

                i++;
            }
        }
        else
            selection = null;

        // How you want the results sorted in the resulting Cursor
        if(sortBy != null)
            sortBy += " DESC";

        Cursor c = db.query(
                tableName,                                // The table to query
                selectedColumns,                          // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortBy                                    // The sort order
        );

        // Get the return values for all rows retrieved from the above query.
        // All of these are contained within the above Cursor.
        ArrayList<ContentValues> returnValues = new ArrayList<>();

        if( c != null && c.moveToFirst() ) {

            if(selectedColumns.length >= 1) {

                for(int j = 0; j < c.getCount(); j++){

                    // Add the values for each row to the ArrayList of ContentValues
                    // For each column requested, get that value from the cursor for each row.
                    int k = 0;
                    ContentValues returnHash = new ContentValues();

                    for(String value: selectedColumns)
                    {
                        returnHash.put(value, c.getString(c.getColumnIndex(selectedColumns[k])));
                        k++;
                    }

                    // Add the HashMap to the ArrayList and move to the next row in the cursor
                    returnValues.add(returnHash);
                    c.moveToNext();
                }

                // Close the cursor and return the rows.
                c.close();
                return returnValues;
            }
            else {
                // Create and return an error HashMap
                ContentValues returnHash = new ContentValues();
                returnHash.put("error", "Column projection error in get_table_rows");
                returnValues.add(returnHash);
                c.close();
                Log.d("get_database_values", "No projection tags found.");
                return returnValues;
            }
        }
        else {
            // Create and return an error HashMap
            ContentValues returnHash = new ContentValues();
            returnHash.put("error", "Failed to retrieve cursor value.");
            returnValues.add(returnHash);
            Log.d("get_database_values", "Test: get_database_rows failed to retrieve cursor value.");
            return returnValues;
        }
    }

    public Boolean updateTableRow(SQLiteDatabase db, String columnName, String oldValue, String newValue, String tableName) throws Exception{

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

        return count != 0;
    }

    public void removeTableRow(SQLiteDatabase db, String columnName, String columnValue, String tableName) throws Exception{

        // Define 'where' part of query.
        String selection = columnName + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { columnValue };

        // Issue SQL statement.
        db.delete(tableName, selection, selectionArgs);
    }
}
