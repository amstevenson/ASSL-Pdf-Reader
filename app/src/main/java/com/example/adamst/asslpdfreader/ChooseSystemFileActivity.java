package com.example.adamst.asslpdfreader;

/**
 * Created by Adamst on 16/10/2016.
 */

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Search files within the android devices file directory. When a file is clicked, the
 * user will be able to save it to the internal storage for this specific app, to be used
 * in conjunction with the MainActivities methods that list the saved pdf's/epubs.
 *
 * TODO: Remove all warnings for this class
 * TODO: Update back button on this activity to go up a directory and not finish it
 *
 * @author Adamst
 *
 */
public class ChooseSystemFileActivity extends ListActivity {

    private File file;
    private List<String> items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_system_file);

        items = new ArrayList<>();

        //
        // Check to see if external storage is available on device
        //
        Boolean isWritable = isExternalStorageWritable();
        Boolean isReadable = isExternalStorageReadable();

        if(isWritable || isReadable)
        {
            try{
                //String root_sd = Environment.getExternalStorageDirectory().toString();
                file = new File( Environment.getExternalStorageDirectory().getPath() ) ;
                File list[] = file.listFiles();

                for( int i=0; i< list.length; i++)
                {
                    items.add( list[i].getName() );
                }

                setListAdapter(new ArrayAdapter<>(this,
                        android.R.layout.simple_list_item_1, items ));

            }catch(NullPointerException e)
            {
                Log.d("Error: null pointer", "Null pointer encountered: " + e.toString());
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "There is no external storage available" +
                    "for reading or writing.", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose_system_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id){

        super.onListItemClick(l, v, position, id);

        final File temp_file = new File( file, items.get( position ) );

        // The selected extension of the file (excluding directories)
        String temp_file_path       = temp_file.getAbsolutePath();

        if(temp_file_path.contains(".") && temp_file.isFile())
        {

            // If the user wishes to save the file, add it to internal storage and save
            // book related values to the file database table
            new AlertDialog.Builder(v.getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Save this file")
                    .setMessage("Are you sure you want to add this book?")
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Progress Dialog
                            final ProgressDialog pDialog;
                            pDialog = new ProgressDialog(ChooseSystemFileActivity.this);
                            pDialog.setMessage("Saving file, this may take a moment.");
                            pDialog.setIndeterminate(false);
                            pDialog.setCancelable(true);
                            pDialog.show();

                            // TODO: Create the file and check that the type is pdf or epub
                            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(
                                    Uri.fromFile(temp_file).toString());
                            final String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            final int size = (int) temp_file.length();

                            byte[] bytes = new byte[size];

                            try {

                                // Get the byte information from the file
                                BufferedInputStream fileBuffer = new BufferedInputStream(new FileInputStream(temp_file));
                                fileBuffer.read(bytes, 0, bytes.length);
                                fileBuffer.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // TODO: Save the file to the internal storage

                            // TODO: Add the file values to the database

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    //
                                    // Finish the updating process
                                    //
                                    if(pDialog.isShowing()) pDialog.dismiss();

                                    Toast toast = Toast.makeText(getApplicationContext(), "File: '"
                                            + temp_file.getName() +  "' has been saved." , Toast.LENGTH_LONG);
                                    toast.show();

                                    finish();
                                }
                            }, 2000);
                        }

                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        }

        // If we do not click on a file, we can assume that we would have a directory
        // simply because there is no "." in the file.
        else if( !temp_file.isFile()) // If it is not a file
        {
            file = new File( file, items.get( position ));

            // List all files within the directory and list them
            File list[] = file.listFiles();

            items.clear();

            for( int i=0; i< list.length; i++)
            {
                items.add( list[i].getName() );
            }

            // Set the adapter
            setListAdapter(new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, items ));
        }
    }

    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state);
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

}
