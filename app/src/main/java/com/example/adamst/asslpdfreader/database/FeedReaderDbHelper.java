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
import java.lang.Long;

import com.example.adamst.asslpdfreader.database.FeedReaderContract.FileEntry;

import java.util.ArrayList;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "FeedReader.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_FILE_ENTRIES =
            "CREATE TABLE " + FileEntry.TABLE_NAME + " (" +
                    FileEntry._ID + " INTEGER PRIMARY KEY," +
                    FileEntry.COLUMN_NAME_BOOK_NAME + TEXT_TYPE + COMMA_SEP +
                    FileEntry.COLUMN_NAME_DATE_ADDED + TEXT_TYPE + " )";

    private static final String SQL_DELETE_FILE_ENTRIES =
            "DROP TABLE IF EXISTS " + FileEntry.TABLE_NAME;

    public FeedReaderDbHelper(Context context) {
        // Creates the database (FeedReader.db) if there is not a file already.
        // If the schema changes, the version parameter will update the db.
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db)
    {
        // Create the database and execution creation SQL for each table.
        // This only works if the db does not exist, or when a new version
        // has been added.
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

    /**
     *
     * Adds a new row to the DB table with the values specified
     *
     * @param db The database
     * @param values The new rows values
     * @param tableName The name of the table
     * @return Returns the ID of the newly added row as a Long
     * @throws Exception
     */
    public Long addTableRow(SQLiteDatabase db, ContentValues values, String tableName) throws Exception {

        // Insert the new row; returns the new ID as a long value.
        return db.insert(tableName, null, values);
    }

    /**
     *
     * Checks to see if a row already exists in the database with the values provided.
     *
     * @param db The database
     * @param values The ContentValues object containing the keys and values to query the database with.
     * @param tableName The name of the table
     * @return Returns true if the row already exists, or false otherwise
     * @throws Exception
     */
    public boolean checkRowExists(SQLiteDatabase db, ContentValues values, String tableName) throws Exception{

        // Identify what the selected columns would be based on the value provided.
        String[] selectedColumns = new String[values.size()];
        ContentValues whereValues = new ContentValues();
        int i = 0;
        if(values.size() > 0) {
            for (String whereKey : values.keySet()) {

                selectedColumns[i] = whereKey;
                whereValues.put(whereKey, values.get(whereKey).toString());
                i++;
            }
        }

        // Retrieve a row that may or may not exist
        ArrayList<ContentValues> multipleRowValues = getTableRows(
                db,
                selectedColumns,
                whereValues,
                tableName,
                null);

        int resultCount = multipleRowValues.size();

        return resultCount != 0;
    }

    /**
     *
     * Returns a new String that has a number appended to the end of it, in order for it to be
     * deemed and used as a unique row value.
     *
     * @param db The database object.
     * @param values The value for the database Row; tag and value. Only pass in ONE value, else
     *               a null value will be returned.
     * @param tableName The name of the database table.
     * @return A String that is a unique value for a specific row in the database.
     * @throws Exception
     */
    public String getUniqueRowValue(SQLiteDatabase db, ContentValues values, String tableName) throws Exception{

        // Check the size of the values object. If it is more than 1 or is 0, return the string as a
        //  null value/error
        // This is because my current project is only allowing for one renamed value.
        if(values.size() != 1 )
            return null;

        // Convert the old value provided into a new appended version.
        // This while loop will keep going until a version of the value is found where
        // there is a unique number identifier for it.
        // E.g "name" would be "name (1)"
        // Or if "name (1)" exists, then it would try "name (2)" and so on.
        int newValueNumber = 1;
        String newTagValue = "";

        while(checkRowExists(db, values, tableName))
        {
            String tagName = "";
            String tagValue = "";

            // Rename the value, appending a number to the end.
            for (String whereKey : values.keySet()) {

                tagName = whereKey;
                tagValue = values.get(tagName).toString();
            }

            newTagValue = getAppendedNumberedValue(tagValue, newValueNumber);

            // Recreate the values ContentValues object and put in the new String
            values = new ContentValues();
            values.put(tagName, newTagValue);

            newValueNumber++;

            // If we go above 9, assume it has failed.
            if(newValueNumber > 9)
                break;
        }

        // Return the new unique String value
        return newTagValue;
    }

    /**
     *
     * Appends a number to the end of a string. This is performed in order to
     * account for values in the database that would otherwise have the same name.
     * Instead of refusing to add a new row because the value already exists, adding
     * a number to the end replicates what's normally seen on OS's when a copy/paste occurs
     * where there is a collision of file names.
     *
     * @param oldValue The string that will be appended to.
     * @param newValueNumber The number to add on to the end of the string.
     * @return The string with a number on the end of it. E.g "name" will be "name (1)"
     */
    private String getAppendedNumberedValue(String oldValue, int newValueNumber) {

        String newValue;

        // Check that the string is not null
        if (oldValue != null) {

            // if number is 1, add ( *number* ) to the end of the string
            if (newValueNumber == 1) {
                newValue = oldValue;
                newValue += " (" + String.valueOf(newValueNumber) + ")";
                return newValue;
            }
            // if it is not 0, find the second to last character and replace the number with
            // the next one in the sequence.
            else {

                newValue = oldValue.substring(0, oldValue.length() - 4);
                newValue += " (" + String.valueOf(newValueNumber) + ")";

                return newValue;
            }

        }
        else
            // If we have a null string, return it as it is.
            return null;

    }

    /**
     *
     * Gets all rows associated with all of the information provided.
     * TODO: add an optional row for the sortBy DESC/ASC.
     *
     * @param db The database object.
     * @param selectedColumns The columns tags which will be used to retrieve values from the
     *                        rows that are retrieved from the database.
     * @param whereValues The "where" arguments for the select query. This will be in the format of
     *                    "TAG_NAME", "TAG_VALUE" without any sql specific syntax being provided;
     *                    just purely tags and values.
     * @param tableName The name of the database table being queried.
     * @param sortBy The name of the column (as a tag) to be used for the sort portion of the sql
     *               query.
     * @return Returns an ArrayList of ContentValues that has one or more rows associated with it.
     * The index of the ArrayList refers to the row, and the values can be accessed by providing
     * the tag as the key with the relevant get methods.
     * @throws Exception
     */
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

        // Execute the query that selects and retrieves all of the rows and stores them in a Cursor.
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
                returnValues.add(returnHash);
                c.close();
                Log.d("getTableRows error", "No projection tags found.");
                return returnValues;
            }
        }
        else {
            // Create and return an error HashMap
            Log.d("getTableRows error", "Test: get_database_rows failed to retrieve cursor value.");
            return returnValues;
        }
    }

    /**
     *
     * Updates a table row.
     *
     * @param db The database object
     * @param columnName The name of the column that will be updated
     * @param oldValue The "like" portion of the sql uses the old value in order to find the row
     *                 that will be updated.
     * @param newValue The new value for the specific row.
     * @param tableName The name of the database table
     * @return Returns true if updated, else false
     * @throws Exception
     */
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

    /**
     *
     * A method used to remove one or more rows from the database that match specific conditions.
     *
     * @param db The database object
     * @param columnName The name of the table column
     * @param columnValue The value for the provided column
     * @param tableName The database table
     * @throws Exception
     */
    public void removeTableRow(SQLiteDatabase db, String columnName, String columnValue, String tableName) throws Exception{

        // Define 'where' part of query.
        String selection = columnName + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { columnValue };

        // Issue SQL statement.
        db.delete(tableName, selection, selectionArgs);
    }
}
