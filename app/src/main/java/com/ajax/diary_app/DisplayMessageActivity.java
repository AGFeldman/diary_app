package com.ajax.diary_app;

import android.content.Intent;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.DbxException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.textView);
        textView.setText(message);

        Log.i("DisplayMessageActivity", "hereTODO(agf)");
        InputStream in = new ByteArrayInputStream(message.getBytes());
        String path = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("dropbox_path", "/");
        // String path = "/rpad_mobile/";
        // String path = "/Journal/html/append_5/";
        Log.i("DisplayMessageActivity path", "<" + path + ">");

        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            String randomHexString = Long.toHexString((new Random()).nextInt());
            String filename = timeStamp + "_" + randomHexString;
            DropboxClientFactory.getClient().files().uploadBuilder(path + filename + ".txt").uploadAndFinish(in);
            textView.setTextColor(Color.GREEN);
        } catch (DbxException | IOException e) {
            try {
                textView.setTextColor(Color.RED);
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                Log.i("DisplayMessageActivity", "Dropbox upload failed:", e);
            } catch (Throwable ee) {
                Log.i("DisplayMessageActivity", "Caught inner throwable", ee);
            }
        }
    }
}
