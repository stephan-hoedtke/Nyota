package com.stho.software.nyota;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Moon;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;
import com.stho.software.nyota.views.MoonAgeView;
import com.stho.software.nyota.views.MoonView;

public final class MoonAdapterActivity extends AbstractElementAdapterActivity {

    private Moon moon = null;

    private static class ViewHolder {
        private MoonView image;
        private MoonAgeView age;
        private TextView prev;
        private TextView next;
        private TextView full;
    }

    private final ViewHolder holder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        setupToolbar();
        Universe.getInstance().updateForNow();
        updateUI();
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_view_moon);

        holder.image = (MoonView)findViewById(R.id.elementImage);
        holder.age = (MoonAgeView)findViewById(R.id.moonAge);
        holder.prev = (TextView)findViewById(R.id.prevNewMoon);
        holder.next = (TextView)findViewById(R.id.nextNewMoon);
        holder.full = (TextView)findViewById(R.id.fullMoon);


        super.setupViews();

        holder.age.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float previousX;
            private static final double HOURS_PER_MONTH = 730;
            private Moment newMoment;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = x;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        newMoment = getMoment().addHours((x - previousX));
                        Universe.getInstance().updateFor(newMoment, true);
                        updateUI();
                        break;

                    case MotionEvent.ACTION_UP:
                        if (Math.abs(x - startX) < 3) {
                            if (x < 0.4 * v.getWidth()) {
                                Moment moment = getMoment().forUTC(moon.fullMoon.addHours(-HOURS_PER_MONTH));
                                newMoment = moment.forUTC(Moon.getFullMoon(moment, 0));
                            }
                            else if (x > 0.6 * v.getWidth()) {
                                Moment moment = getMoment().forUTC(moon.fullMoon.addHours(+HOURS_PER_MONTH));
                                newMoment = moment.forUTC(Moon.getFullMoon(moment, 0));
                            }
                            else {
                                newMoment = getMoment();
                            }
                            Universe.getInstance().updateFor(newMoment, true);
                            updateUI();
                        }
                        break;
                }

                previousX = x;
                return true;
           }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_element_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {

        moon = (Moon)element;
        moon.calculateNewFullMoon(moment);

        double total = UTC.gapInHours(moon.prevNewMoon, moon.nextNewMoon);

        holder.image.setPhase(moon.phase, moon.zenithAngle, moon.parallacticAngle);
        holder.age.setAge(moon.age, UTC.gapInHours(moon.prevNewMoon, moon.fullMoon) / total, UTC.gapInHours(moon.fullMoon, moon.nextNewMoon) / total);
        holder.prev.setText(Formatter.toString(moon.prevNewMoon, moment.getTimeZone(), Formatter.TimeFormat.DATETIME));
        holder.full.setText(Formatter.toString(moon.fullMoon, moment.getTimeZone(), Formatter.TimeFormat.DATETIME));
        holder.next.setText(Formatter.toString(moon.nextNewMoon, moment.getTimeZone(), Formatter.TimeFormat.DATETIME));

        updateBasics(moon.getBasics(moment));
        updateDetails(moon.getDetails(moment));
    }
}
