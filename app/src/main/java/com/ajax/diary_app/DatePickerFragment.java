package com.ajax.diary_app;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private int year;
    private int month;
    private int day;
    private Button button;

    public DatePickerFragment() {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        this.year = year;
        this.month = month;
        this.day = day;
        if (button != null) {
           button.setText(getText());
        }
    }

    private String intToPaddedString(int i) {
        String str = String.valueOf(i);
        if (str.length() == 1) {
            return "0" + str;
        }
        return str;
    }

    private String getText() {
        // Month is zero-indexed
        return intToPaddedString(month + 1) + "-" + intToPaddedString(day);
    }

    void setButton(Button button) {
        this.button = button;
        button.setText(getText());
    }

    String getDateString() {
        // Month is zero-indexed
        return String.valueOf(year) + intToPaddedString(month + 1) + intToPaddedString(day);
    }
}
