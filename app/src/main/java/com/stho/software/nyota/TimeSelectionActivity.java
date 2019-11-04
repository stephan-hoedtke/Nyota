package com.stho.software.nyota;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.stho.software.nyota.utilities.City;
import com.stho.software.nyota.utilities.UTC;

public class TimeSelectionActivity extends Activity {

    private class ViewHolder {
        TextView labelName;
        TextView labelTimeZone;
        EditText editLocalTime;
        EditText editUTC;
        CheckBox checkBoxAutomatic;
    }

    private City city = null;
    private UTC utc = null;
    private ViewHolder viewHolder = new ViewHolder();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_selection);

        city = Framework.getCity(getIntent());
        utc = Framework.getTime(getIntent());

        if (city == null)
            city = City.createNewDefaultBerlin();

        if (utc == null)
            utc = UTC.forNow();

        viewHolder.labelName = findViewById(R.id.editName);
        viewHolder.labelTimeZone = findViewById(R.id.editTimeZone);
        viewHolder.checkBoxAutomatic = findViewById(R.id.checkBoxAutomatic);
        viewHolder.editLocalTime = findViewById(R.id.editLocalTime);
        viewHolder.editUTC = findViewById(R.id.editUTC);
        viewHolder.checkBoxAutomatic = findViewById(R.id.checkBoxAutomatic);

        findViewById(R.id.buttonAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String str = viewHolder.editLocalTime.getText().toString();
                    boolean automatic = viewHolder.checkBoxAutomatic.isChecked();


                    if (city == null) {
                        city = City.createNewDefaultBerlin();
                    }

                    // TODO: read the time and make use of it!

                    city.setAutomatic(automatic);
                    Framework.finishActivity(TimeSelectionActivity.this, city);
                } catch (Exception ex) {
                    // ignore
                }
            }
        });

        updateUI();
    }

    private void updateUI() {
        viewHolder.labelName.setText(city.getNameEx());
        viewHolder.labelTimeZone.setText(city.getTimeZone().getID());
        //viewHolder.editLocalTime.setText(); HERE !!
    }

}
