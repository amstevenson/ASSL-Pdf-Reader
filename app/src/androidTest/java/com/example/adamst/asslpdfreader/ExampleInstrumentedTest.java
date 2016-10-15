package com.example.adamst.asslpdfreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.example.adamst.asslpdfreader.database.FeedReaderDbHelper;
import com.example.adamst.asslpdfreader.database.FeedReaderContract.FileEntry;


import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

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
    public void testDbFileTable() throws Exception {

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        feedReaderDbHelper.addTableRow(db, values, FileEntry.TABLE_NAME);

        values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameTwo");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        feedReaderDbHelper.addTableRow(db, values, FileEntry.TABLE_NAME);

        // Retrieve values
        String nameOne, nameTwo;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FileEntry._ID,
                FileEntry.COLUMN_NAME_NAME,
                FileEntry.COLUMN_NAME_DATE_ADDED
        };

        // Define the "where" keys and values for the SQL query.
        ContentValues whereValues = new ContentValues();
        whereValues.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        whereValues.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        ArrayList<ContentValues> testValuesOne = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FileEntry.TABLE_NAME,
                null);

        // Where keys for the second get query.
        whereValues = new ContentValues();
        whereValues.put(FileEntry.COLUMN_NAME_NAME, "nameTwo");
        whereValues.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        ArrayList<ContentValues> testValuesTwo = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FileEntry.TABLE_NAME,
                FileEntry.COLUMN_NAME_NAME);

        nameOne = testValuesOne.get(0).getAsString(FileEntry.COLUMN_NAME_NAME);
        nameTwo = testValuesTwo.get(0).getAsString(FileEntry.COLUMN_NAME_NAME);

        // Assert that the values equal what they should be in the database
        assertEquals(nameOne, "nameOne");
        assertEquals(nameTwo, "nameTwo");
        assertNotEquals(nameOne, "");
        assertNotEquals(nameTwo, "");

        // Update the rows in the table
        String newNameOne, newNameTwo;
        newNameOne = "new value one";
        newNameTwo = "new value two";

        assertEquals(true, feedReaderDbHelper.updateTableRow(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameOne, newNameOne,
                FileEntry.TABLE_NAME));
        assertEquals(true, feedReaderDbHelper.updateTableRow(
                db,
                FileEntry.COLUMN_NAME_NAME,
                nameTwo,
                newNameTwo,
                FileEntry.TABLE_NAME));

        // Get the returned updated values
        whereValues = new ContentValues();
        whereValues.put(FileEntry.COLUMN_NAME_NAME, "new value one");

        testValuesOne = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FileEntry.TABLE_NAME,
                null);

        whereValues = new ContentValues();
        whereValues.put(FileEntry.COLUMN_NAME_NAME, "new value two");

        testValuesTwo = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FileEntry.TABLE_NAME,
                FileEntry.COLUMN_NAME_NAME);

        String changedNameOne = testValuesOne.get(0).getAsString(FileEntry.COLUMN_NAME_NAME);
        String changedNameTwo = testValuesTwo.get(0).getAsString(FileEntry.COLUMN_NAME_NAME);

        // Assert that the changes have taken place
        assertEquals(changedNameOne, "new value one");
        assertEquals(changedNameTwo, "new value two");
        assertNotEquals(changedNameOne, "");
        assertNotEquals(changedNameOne, "");

        // Remove the values in the table
        feedReaderDbHelper.removeTableRow(db, FileEntry.COLUMN_NAME_NAME, newNameOne, FileEntry.TABLE_NAME);
        feedReaderDbHelper.removeTableRow(db, FileEntry.COLUMN_NAME_NAME, newNameTwo, FileEntry.TABLE_NAME);
    }

    @Test
    public void testDBMultipleGet() throws Exception{

        Log.d("-----", "---------------------------------------------");
        Log.d("Calling test", "Test DB Multiple Get");
        Log.d("-----", "---------------------------------------------");

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        feedReaderDbHelper.addTableRow(db, values, FileEntry.TABLE_NAME);

        values = new ContentValues();
        values.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        values.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        feedReaderDbHelper.addTableRow(db, values, FileEntry.TABLE_NAME);

        // Retrieve values
        String nameOne;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FileEntry._ID,
                FileEntry.COLUMN_NAME_NAME,
                FileEntry.COLUMN_NAME_DATE_ADDED
        };

        // Define the "where" keys and values for the SQL query.
        ContentValues whereValues = new ContentValues();
        whereValues.put(FileEntry.COLUMN_NAME_NAME, "nameOne");
        whereValues.put(FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        ArrayList<ContentValues> testValuesOne = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FileEntry.TABLE_NAME,
                null);


        nameOne = testValuesOne.get(0).getAsString(FileEntry.COLUMN_NAME_NAME);

        assertEquals(nameOne, "nameOne");

        // Remove the value from the table
        feedReaderDbHelper.removeTableRow(db, FileEntry.COLUMN_NAME_NAME, nameOne, FileEntry.TABLE_NAME);
    }
}