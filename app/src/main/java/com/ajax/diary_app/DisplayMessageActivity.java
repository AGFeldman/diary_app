package com.ajax.diary_app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.dropbox.core.DbxException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

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

        InputStream in = new ByteArrayInputStream(message.getBytes());
        try {
            DropboxClientFactory.getClient().files().uploadBuilder("/test_dropbox_app/megalodon_test.txt").uploadAndFinish(in);
        } catch (DbxException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
