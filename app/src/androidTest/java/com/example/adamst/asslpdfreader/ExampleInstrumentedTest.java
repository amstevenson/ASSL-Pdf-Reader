package com.example.adamst.asslpdfreader;

import android.content.Context;
import android.provider.Settings;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.adamst.asslpdfreader.database.FeedReaderDbHelper;

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
    public void test_database() throws Exception {

        // Create a test database
        Context appContext = InstrumentationRegistry.getTargetContext();
        FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(appContext);

        System.out.print("test");

        // Add some test values

        // Retrieve values

        // Assert that the values equal what they should be in the database

        // Remove the database so that the next test does not have an exception.
    }

    public void create_database() throws Exception{

        // Create a test database

    }

    public void add_database_values() throws Exception{

        // Add some test values
    }

    public void get_database_values() throws Exception{

        // Get the test values back
    }

    public void remove_database() throws Exception{

        // Remove the test database
    }
}
