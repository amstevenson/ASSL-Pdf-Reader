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

import java.io.File;

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
        nameOne = get_table_values(db, "nameOne");
        nameTwo = get_table_values(db, "nameTwo");

        // Assert that the values equal what they should be in the database
        assertEquals(nameOne, "nameOne");
        assertEquals(nameTwo, "nameTwo");
        assertNotEquals(nameOne, "");
        assertNotEquals(nameTwo, "");

        // Remove the values in the table
        remove_table_row(db, nameOne);
        remove_table_row(db, nameTwo);
    }

    private void add_table_values(SQLiteDatabase db, String name, String dateAdded) throws Exception{

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, name);
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, dateAdded);

        // Insert the new row, returning the primary key value of the new row
        db.insert(FileEntry.TABLE_NAME, null, values);
    }

    private String get_table_values(SQLiteDatabase db, String name) throws Exception {

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                FileEntry._ID,
                FileEntry.COLUMN_NAME_NAME,
                FileEntry.COLUMN_NAME_DATE_ADDED
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = FileEntry.COLUMN_NAME_NAME + " = ?";
        String[] selectionArgs = { name };

        // How you want the results sorted in the resulting Cursor
        //String sortOrder =
        //        FeedEntry.COLUMN_NAME_SUBTITLE + " DESC";

        Cursor c = db.query(
                FileEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        if( c != null && c.moveToFirst() ) {

            name = c.getString(c.getColumnIndex(FileEntry.COLUMN_NAME_NAME));
            c.close();

            return name;
        }
        else {
            Log.d("get_database_values", "Test: get_database_values failed to retrieve cursor value.");
            return "Cursor failed";
        }
    }

    private void remove_table_row(SQLiteDatabase db, String name) throws Exception{

        // Remove the test database
        // Define 'where' part of query.
        String selection = FileEntry.COLUMN_NAME_NAME + " LIKE ?";

        // Specify arguments in placeholder order.
        String[] selectionArgs = { name };

        // Issue SQL statement.
        db.delete(FileEntry.TABLE_NAME, selection, selectionArgs);
    }
}
