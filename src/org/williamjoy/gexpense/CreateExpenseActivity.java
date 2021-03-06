package org.williamjoy.gexpense;

import java.util.Calendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class CreateExpenseActivity extends AbstractExpenseActivity {
    private long calendar_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    public void doSubmit() {
        EditText money = (EditText) this.findViewById(R.id.editTextMoney);
        EditText title = (EditText) this.findViewById(R.id.editTextTitle);
        EditText location = (EditText) this.findViewById(R.id.editTextLocation);

        Calendar beginTime = mDate;
        Calendar endTime = Calendar.getInstance();
        endTime = (Calendar) beginTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 1);
        String strCalTitle = title.getText() + "|" + money.getText();
        String strLocation = location.getText().toString();

        Spinner spinner = (Spinner) this.findViewById(R.id.category_spinner);
        String category_name = spinner.getSelectedItem().toString();
        spinner = (Spinner) this.findViewById(R.id.payfrom_spinner);
        String payFrom = spinner.getSelectedItem().toString();
        if (category_name == null || category_name.equals("Expense Type")) {

            Toast.makeText(getApplicationContext(),
                    "Please Choose an Expense Category!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        if (title.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(),
                    "Please fill in Title field!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (money.getText().toString().equals("")) {

            Toast.makeText(getApplicationContext(),
                    "Please fill in Money field!", Toast.LENGTH_SHORT).show();
            return;
        }
        StringBuffer sb = new StringBuffer(
                "#auto generated by GExpense App, http://goo.gl/0Xv4A\n");
        sb.append("version=1.0\n");
        sb.append("expense.").append(CalendarContract.Events.TITLE).append('=')
                .append(title.getText()).append('\n');
        sb.append("expense.").append(ExpenseConstants.ExpenseEvents.MONEY)
                .append('=').append(money.getText()).append('\n');
        sb.append("expense.").append(ExpenseConstants.ExpenseEvents.CATEGORY)
                .append('=').append(category_name).append('\n');
        sb.append("expense.").append(ExpenseConstants.ExpenseEvents.PAYFROM)
                .append('=').append(payFrom).append('\n');
        String properties = sb.toString();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, beginTime.getTimeInMillis());
        values.put(Events.DTEND, endTime.getTimeInMillis());

        values.put(Events.TITLE, strCalTitle);
        values.put(Events.DESCRIPTION, properties);
        values.put(Events.CALENDAR_ID, this.calendar_id);
        values.put(Events.HAS_ALARM, false);
        values.put(Events.EVENT_LOCATION, strLocation);
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        
        Toast.makeText(getApplicationContext(),
                "Creating expense event...",
                Toast.LENGTH_SHORT).show();
        cr.insert(Events.CONTENT_URI, values);

        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        this.finish();
    }

    @Override
    public void onNewIntent(Intent intent) {
        calendar_id = getSelectedCalendarID();
        Log.d("onNewIntent", "getSelectedCalendarID()=" + calendar_id);
    }

    private long getSelectedCalendarID() {
        long cal_id = -1L;
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        String cal = sharedPref.getString(ExpenseConstants.ExpenseEvents._ID,
                ExpenseConstants.ExpenseEvents._ID + "");
        try {
            cal_id = Long.parseLong(cal);
        } catch (NumberFormatException e) {
            Log.d("GET CALENDAR ID FAILED", cal, e);
        }
        return cal_id;
    }


    @Override
    protected void fillInForm() {
       
    }

}
