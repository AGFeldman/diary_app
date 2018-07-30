package com.ajax.diary_app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class DisplayMessageActivity extends AppCompatActivity {

    /** Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public void writeToExternalStorage(String message, String filename) throws IOException {
        if (isExternalStorageWritable()) {
            File dir = new File(Environment.getExternalStorageDirectory(),
                    getString(R.string.diary_directory));
            dir.mkdirs();
            File file = new File(dir, filename);
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(message.getBytes());
            outputStream.close();
        }
    }

    public void uploadWaitingFilesToDropbox() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                getString(R.string.diary_directory));
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null) {
            return;
        }
        String path = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("dropbox_path", "/");
        for (File f : directoryListing) {
            try {
                InputStream in = new ByteArrayInputStream(Files.readAllBytes(f.toPath()));
                DropboxClientFactory.getClient().files().uploadBuilder(path + f.getName()).uploadAndFinish(in);
                Utils.showToast(this, "Uploaded " + f.getName());
                if (!f.delete()) {
                    Utils.showToast(this, "Delete failed for " + f.getName());
                }
            } catch (DbxException | IOException e){
                Log.i("DisplayMessageActivity", "Dropbox upload failed:", e);
                Utils.showToast(this, "Upload failed for " + f.getName() + ", giving up on uploading");
                return;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String chosenDateString = intent.getStringExtra(MainActivity.CHOSEN_DATE_STRING);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            String randomHexString = Long.toHexString((new Random()).nextInt());
            String filename = chosenDateString + "_" + timeStamp + "_" + randomHexString;
            writeToExternalStorage(message, filename + ".txt");
            textView.setTextColor(Color.GREEN);
        } catch (IOException e) {
            try {
                textView.setTextColor(Color.RED);
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                Log.i("DisplayMessageActivity", "Save failed:", e);
            } catch (Throwable ee) {
                Log.i("DisplayMessageActivity", "Caught inner throwable", ee);
            }
        }
        uploadWaitingFilesToDropbox();
    }

    @Override
    public void onBackPressed() {
        // Just do this to clear the EditText. Alternative approaches:
        // https://stackoverflow.com/questions/12408719/resume-activity-in-android
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }
}
