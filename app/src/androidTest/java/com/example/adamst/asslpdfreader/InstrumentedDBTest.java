package com.example.adamst.asslpdfreader;

/**
 * Created by Adamst on 16/10/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.adamst.asslpdfreader.database.FeedReaderContract;
import com.example.adamst.asslpdfreader.database.FeedReaderDbHelper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedDBTest {

    @Test
    /**
     *
     * Tests the "file" database table. Performs checks against CRUD related operations, such
     * as adding table values, updated and deleting. Does not perform checks on if rows exist,
     * nor does it rename files in those specific occurrences.
     *
     */
    public void testDbFileTable() throws Exception {

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "nameOne");
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        feedReaderDbHelper.addTableRow(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "nameTwo");
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        feedReaderDbHelper.addTableRow(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        // Retrieve values
        String nameOne, nameTwo;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FeedReaderContract.FileEntry._ID,
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME,
                FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED
        };

        // Define the "where" keys and values for the SQL query.
        ContentValues whereValues = new ContentValues();
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "nameOne");
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "dateOne");

        ArrayList<ContentValues> testValuesOne = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FeedReaderContract.FileEntry.TABLE_NAME,
                null);

        // Where keys for the second get query.
        whereValues = new ContentValues();
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "nameTwo");
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "dateTwo");

        ArrayList<ContentValues> testValuesTwo = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FeedReaderContract.FileEntry.TABLE_NAME,
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);

        nameOne = testValuesOne.get(0).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);
        nameTwo = testValuesTwo.get(0).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);

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
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME,
                nameOne, newNameOne,
                FeedReaderContract.FileEntry.TABLE_NAME));
        assertEquals(true, feedReaderDbHelper.updateTableRow(
                db,
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME,
                nameTwo,
                newNameTwo,
                FeedReaderContract.FileEntry.TABLE_NAME));

        // Get the returned updated values
        whereValues = new ContentValues();
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "new value one");

        testValuesOne = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FeedReaderContract.FileEntry.TABLE_NAME,
                null);

        whereValues = new ContentValues();
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "new value two");

        testValuesTwo = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FeedReaderContract.FileEntry.TABLE_NAME,
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);

        String changedNameOne = testValuesOne.get(0).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);
        String changedNameTwo = testValuesTwo.get(0).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);

        // Assert that the changes have taken place
        assertEquals(changedNameOne, "new value one");
        assertEquals(changedNameTwo, "new value two");
        assertNotEquals(changedNameOne, "");
        assertNotEquals(changedNameOne, "");

        // Remove the values in the table
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, newNameOne, FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, newNameTwo, FeedReaderContract.FileEntry.TABLE_NAME);
    }

    @Test
    /**
     *
     * Tests to see if the getTableRows method in the FeedReaderDBHelper class retrieves more than
     * one value, when there are are multiple rows associated with it.
     *
     */
    public void testDbMultipleGet() throws Exception{

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        // Note to self, there can only be one name per book at the moment.
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "replicated value");
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date");

        ContentValues values2 = new ContentValues();
        values2.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "replicated value two");
        values2.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date");

        ContentValues values3 = new ContentValues();
        values3.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "replicated value three");
        values3.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date");

        ContentValues values4 = new ContentValues();
        values4.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "replicated value four");
        values4.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date");

        // Add four values
        feedReaderDbHelper.addTableRow(db, values,  FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.addTableRow(db, values2, FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.addTableRow(db, values3, FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.addTableRow(db, values4, FeedReaderContract.FileEntry.TABLE_NAME);

        // Retrieve values
        String nameOne, nameTwo, dateThree, dateFour;

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] selectedColumns = {

                FeedReaderContract.FileEntry._ID,
                FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME,
                FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED
        };

        // Define the "where" keys and values for the SQL query.
        ContentValues whereValues = new ContentValues();
        whereValues.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date");

        ArrayList<ContentValues> multipleRowValues = feedReaderDbHelper.getTableRows(
                db,
                selectedColumns,
                whereValues,
                FeedReaderContract.FileEntry.TABLE_NAME,
                null);

        // Check to see how many rows returned.
        assertEquals(multipleRowValues.size(), 4);

        // Get one value from each returned row; should be four.
        nameOne   = multipleRowValues.get(0).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);
        nameTwo   = multipleRowValues.get(1).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME);
        dateThree = multipleRowValues.get(2).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED);
        dateFour  = multipleRowValues.get(3).getAsString(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED);

        // Check each value matches what is expected for each row.
        assertEquals(nameOne, "replicated value");
        assertEquals(nameTwo, "replicated value two");
        assertEquals(dateThree, "replicated date");
        assertEquals(dateFour, "replicated date");

        // Remove the values from the table
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "replicated date", FeedReaderContract.FileEntry.TABLE_NAME);
    }

    @Test
    /**
     *
     * Checks to see if the checkRowExists method for the FeedReaderDBHelper class correctly checks to
     * see if a row with specific values already exists in the specified table.
     * This method can be used when more than one value should not exist for any specific context.
     *
     */
    public void testDbOnlyOneValueAllowed() throws Exception{

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "same name");
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_DATE_ADDED, "same name date");

        // Check the row does not exist
        Boolean rowAlreadyExistsFalse = feedReaderDbHelper.checkRowExists(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        // Add a new test row
        if(!rowAlreadyExistsFalse) feedReaderDbHelper.addTableRow(db, values, FeedReaderContract.FileEntry.TABLE_NAME);          // Valid row

        // Check that the new test row does not exist
        Boolean rowAlreadyExistsTrue = feedReaderDbHelper.checkRowExists(db, values, FeedReaderContract.FileEntry.TABLE_NAME);   // Invalid row

        // Assert both of the above cases
        assertEquals(rowAlreadyExistsFalse, false);
        assertEquals(rowAlreadyExistsTrue, true);

        // Remove the added table row.
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "same name", FeedReaderContract.FileEntry.TABLE_NAME);
    }

    @Test
    /**
     *
     * If a value already exists for a table row, a created method can be used to append a number
     * to the end of it, that makes it unique. For example, when a file is copied and pasted without
     * being renamed, it is automatically assigned a ( *number* ) value to the end. This test replicates
     * that logic and checks if it works or not. 
     *
     */
    public void testDbAutoRenameValue() throws Exception {

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);
        SQLiteDatabase db = feedReaderDbHelper.getWritableDatabase();

        // Add some test values
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "duplicated name");

        // Check the row does not exist
        Boolean rowAlreadyExistsFalse = feedReaderDbHelper.checkRowExists(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        // Add a new test row
        if(!rowAlreadyExistsFalse) feedReaderDbHelper.addTableRow(db, values, FeedReaderContract.FileEntry.TABLE_NAME);          // Valid row

        // Check that the new test row does not exist
        Boolean rowAlreadyExistsTrue = feedReaderDbHelper.checkRowExists(db, values, FeedReaderContract.FileEntry.TABLE_NAME);   // Invalid row

        // Assert both of the above cases
        assertEquals(rowAlreadyExistsFalse, false);
        assertEquals(rowAlreadyExistsTrue, true);

        // Now that we know the row already exists in the table, we will run the db methods that will rename
        // the value in order to append a number to the end, to mimic the behaviour of copy/pasted files in
        // pretty much all OS's.
        String oldValue = values.get(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME).toString();
        String newValue = feedReaderDbHelper.getUniqueRowValue(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        // Old value would be "duplicated name" and the new value would be "duplicated name (1)"
        assertEquals(oldValue, "duplicated name");
        assertEquals(newValue, "duplicated name (1)");

        // Add that specific row to the database, then check that the rename works again.
        values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, newValue);

        feedReaderDbHelper.addTableRow(db, values, FeedReaderContract.FileEntry.TABLE_NAME);

        // Create a new values object that contains the old value, so that we can check
        // that it is (2) instead of (1)
        values = new ContentValues();
        values.put(FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, oldValue);

        // Old value would be "duplicated name" and the new value would be "duplicated name (2)"
        newValue = feedReaderDbHelper.getUniqueRowValue(db, values, FeedReaderContract.FileEntry.TABLE_NAME);
        assertEquals(oldValue, "duplicated name");
        assertEquals(newValue, "duplicated name (2)");

        // Remove the added table rows.
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "duplicated name", FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "duplicated name (1)", FeedReaderContract.FileEntry.TABLE_NAME);
        feedReaderDbHelper.removeTableRow(db, FeedReaderContract.FileEntry.COLUMN_NAME_BOOK_NAME, "duplicated name (2)", FeedReaderContract.FileEntry.TABLE_NAME);
    }
}
