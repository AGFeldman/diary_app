package com.ajax.diary_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.core.android.Auth;
import com.dropbox.core.v2.users.FullAccount;

public class MainActivity extends DropboxActivity {
    public static final String EXTRA_MESSAGE = "com.ajax.diary_app.MESSAGE";
    public static final String CHOSEN_DATE_STRING = "com.ajax.diary_app.CHOSEN_DATE_STRING";
    private static final DatePickerFragment datePickerFragment = new DatePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(v -> Auth.startOAuth2Authentication(MainActivity.this, getString(R.string.app_key)));

        Button dateButton = findViewById(R.id.date_button);
        datePickerFragment.setButton(dateButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetChosenDateIfNeeded();
        setEmailAndNameVisibility();
    }

    private void resetChosenDateIfNeeded() {
        // If entry box is empty then reset the chosen date
        if (((EditText) findViewById(R.id.edit_text)).getText().toString().length() == 0) {
            Log.i("MainActivity", "resetting date");
            datePickerFragment.resetDate();
        } else {
            Log.i("MainActivity", "not resetting date");
        }
    }

    private void setEmailAndNameVisibility() {
        if (hasToken()) {
            findViewById(R.id.email_text).setVisibility(View.VISIBLE);
            findViewById(R.id.name_text).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.email_text).setVisibility(View.GONE);
            findViewById(R.id.name_text).setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.edit_text);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        intent.putExtra(CHOSEN_DATE_STRING, datePickerFragment.getDateString());
        startActivity(intent);
    }

    public void launchSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /** Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    @Override
    protected void loadData() {
        new GetCurrentAccountTask(DropboxClientFactory.getClient(), new GetCurrentAccountTask.Callback() {
            @Override
            public void onComplete(FullAccount result) {
                ((TextView) findViewById(R.id.email_text)).setText(result.getEmail());
                ((TextView) findViewById(R.id.name_text)).setText(result.getName().getDisplayName());
            }

            @Override
            public void onError(Exception e) {
                Log.e(getClass().getName(), "Failed to get account details.", e);
            }
        }).execute();
    }

    public void showDatePickerDialog(View v) {
        FragmentManager a = getSupportFragmentManager();
        datePickerFragment.show(a, "datePicker");
    }
}
