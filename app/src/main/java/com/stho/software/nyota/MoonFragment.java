package com.stho.software.nyota;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Moon;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.utilities.UTC;
import com.stho.software.nyota.views.MoonAgeView;
import com.stho.software.nyota.views.MoonView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MoonFragment extends AbstractElementAdapterFragment {

    private Moon moon = null;

    private static class ViewHolder {
        private MoonView image;
        private MoonAgeView age;
        private TextView prev;
        private TextView next;
        private TextView full;
    }

    private final ViewHolder holder = new ViewHolder();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_moon, container, false);

        onSetupViews(view);
        onSetupToolbar();

        return view;
    }

    @Override
    protected void onSetupViews(View view) {
        super.onSetupViews(view);

        holder.image = view.findViewById(R.id.elementImage);
        holder.age = view.findViewById(R.id.moonAge);
        holder.prev = view.findViewById(R.id.prevNewMoon);
        holder.next = view.findViewById(R.id.nextNewMoon);
        holder.full = view.findViewById(R.id.fullMoon);

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
                        viewModel.setMoment(newMoment, true);
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
                            viewModel.setMoment(newMoment, true);
                        }
                        break;
                }

                previousX = x;
                return true;
            }
        });
    }

    @Override
    protected void updateUniverse(final Universe universe, final IElement element) {
        final Moment moment = universe.getMoment();

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

