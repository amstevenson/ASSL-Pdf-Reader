package com.example.adamst.asslpdfreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.adamst.asslpdfreader.database.FeedReaderDbHelper;
import com.example.adamst.asslpdfreader.database.FeedReaderContract.FileEntry;


import org.junit.Test;
import org.junit.runner.RunWith;

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
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        feedReaderDbHelper.add_table_row(db, values, FileEntry.TABLE_NAME);

        values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameTwo");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        feedReaderDbHelper.add_table_row(db, values, FileEntry.TABLE_NAME);

        // Retrieve values
        String nameOne, nameTwo;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FileEntry._ID,
                FileEntry.COLUMN_NAME_NAME,
                FileEntry.COLUMN_NAME_DATE_ADDED
        };

        ContentValues testValuesOne = feedReaderDbHelper.get_table_rows(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "nameOne",
                FileEntry.TABLE_NAME);

        ContentValues testValuesTwo = feedReaderDbHelper.get_table_rows(
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

        assertEquals(true, feedReaderDbHelper.update_table_row(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameOne, newNameOne,
                FileEntry.TABLE_NAME));
        assertEquals(true, feedReaderDbHelper.update_table_row(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameTwo,
                newNameTwo,
                FileEntry.TABLE_NAME));

        // Get the returned updated values
        testValuesOne = feedReaderDbHelper.get_table_rows(
                db,
                selectedColumns,
                FileEntry.COLUMN_NAME_NAME,
                "new value one",
                FileEntry.TABLE_NAME);

        testValuesTwo = feedReaderDbHelper.get_table_rows(
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
        feedReaderDbHelper.remove_table_row(db, FileEntry.COLUMN_NAME_NAME, newNameOne, FileEntry.TABLE_NAME);
        feedReaderDbHelper.remove_table_row(db, FileEntry.COLUMN_NAME_NAME, newNameTwo, FileEntry.TABLE_NAME);
    }
}