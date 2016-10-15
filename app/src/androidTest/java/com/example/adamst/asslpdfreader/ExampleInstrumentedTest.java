package com.example.adamst.asslpdfreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.adamst.asslpdfreader.database.FeedReaderDbHelper;
import com.example.adamst.asslpdfreader.database.FeedReaderContract.FileEntry;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Hashtable;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.adamst.asslpdfreader", appContext.getPackageName());
    }

    @Test
    public void test_db_file_table() throws Exception {

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        add_table_values(db, "nameOne", "dateOne");
        add_table_values(db, "nameTwo", "dateTwo");

        // Retrieve values
        String nameOne, nameTwo;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FileEntry._ID,
                FileEntry.COLUMN_NAME_NAME,
                FileEntry.COLUMN_NAME_DATE_ADDED
        };

        ContentValues testValuesOne = get_table_values(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "nameOne",
                FileEntry.TABLE_NAME);

        ContentValues testValuesTwo = get_table_values(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "nameTwo",
                FileEntry.TABLE_NAME);

        nameOne = testValuesOne.get(FileEntry.COLUMN_NAME_NAME).toString();
        nameTwo = testValuesTwo.get(FileEntry.COLUMN_NAME_NAME).toString();

        // Assert that the values equal what they should be in the database
        assertEquals(nameOne, "nameOne");
        assertEquals(nameTwo, "nameTwo");
        assertNotEquals(nameOne, "");
        assertNotEquals(nameTwo, "");

        // Update the rows in the table
        String newNameOne, newNameTwo;
        newNameOne = "new value one";
        newNameTwo = "new value two";

        assertEquals(true, update_table_row(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameOne, newNameOne,
                FileEntry.TABLE_NAME));
        assertEquals(true, update_table_row(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameTwo,
                newNameTwo,
                FileEntry.TABLE_NAME));

        // Get the returned updated values
        testValuesOne = get_table_values(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "new value one",
                FileEntry.TABLE_NAME);

        testValuesTwo = get_table_values(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "new value two",
                FileEntry.TABLE_NAME);

        String changedNameOne = testValuesOne.get(FileEntry.COLUMN_NAME_NAME).toString();
        String changedNameTwo = testValuesTwo.get(FileEntry.COLUMN_NAME_NAME).toString();

        // Assert that the changes have taken place
        assertEquals(changedNameOne, "new value one");
        assertEquals(changedNameTwo, "new value two");
        assertNotEquals(changedNameOne, "");
        assertNotEquals(changedNameOne, "");

        // Remove the values in the table
        remove_table_row(db, FileEntry.COLUMN_NAME_NAME, newNameOne, FileEntry.TABLE_NAME);
        remove_table_row(db, FileEntry.COLUMN_NAME_NAME, newNameTwo, FileEntry.TABLE_NAME);
    }

    private void add_table_values(SQLiteDatabase db, String name, String dateAdded) throws Exception{

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, name);
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, dateAdded);

        // Insert the new row, returning the primary key value of the new row
        db.insert(FileEntry.TABLE_NAME, null, values);
    }

    private ContentValues get_table_values(SQLiteDatabase db, String[] selectedColumns, String whereColumnName, String whereColumnValue, String tableName) throws Exception {

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

    private Boolean update_table_row(SQLiteDatabase db, String columnName, String oldValue, String newValue, String tableName) throws Exception{

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

    private void remove_table_row(SQLiteDatabase db, String columnName, String columnValue, String tableName) throws Exception{

        // Remove the test database
        // Define 'where' part of query.
        String selection = columnName + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { columnValue };

        // Issue SQL statement.
        db.delete(tableName, selection, selectionArgs);
    }
}
