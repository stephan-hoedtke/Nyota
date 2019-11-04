package com.stho.software.nyota;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.IIconNameValue;
import com.stho.software.nyota.utilities.IIconValue;
import com.stho.software.nyota.utilities.Moment;

import java.util.ArrayList;


abstract class AbstractElementAdapterActivity extends AbstractElementActivity {
    private static final double ONE_DAY_IN_HOURS = 24.0;
    private static final double ONE_HOUR = 1.0;
    private static final double ONE_MINUTE_IN_HOURS = 1.0 / 60.0;
    private BasicsViewAdapter basicsAdapter;
    private DetailsViewAdapter detailsAdapter;

    @Override
    protected void onSetupViews(View view) {

        ImageButton backward = (ImageButton)findViewById(R.id.buttonBackward);
        if (backward != null) {
            backward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(-ONE_DAY_IN_HOURS);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton previous = (ImageButton)findViewById(R.id.buttonPrevious);
        if (previous != null) {
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(-ONE_HOUR);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton previousMinute = (ImageButton)findViewById(R.id.buttonPrevMinute);
        if (previousMinute != null) {
            previousMinute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(-ONE_MINUTE_IN_HOURS);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton home = (ImageButton)findViewById(R.id.buttonHome);
        if (home != null) {
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().forNow();
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton nextMinute = (ImageButton)findViewById(R.id.buttonNextMinute);
        if (nextMinute != null) {
            nextMinute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(ONE_MINUTE_IN_HOURS);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton next = (ImageButton)findViewById(R.id.buttonNext);
        if (next != null) {
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(ONE_HOUR);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        ImageButton forward = (ImageButton)findViewById(R.id.buttonForward);
        if (forward != null) {
            forward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Moment newMoment = getMoment().addHours(ONE_DAY_IN_HOURS);
                    Universe.getInstance().updateFor(newMoment, true);
                    updateUI();
                }
            });
        }

        Button button = (Button)findViewById(R.id.skyViewButton);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Framework.openSkyViewActivity(AbstractElementAdapterActivity.this, getElement(), getMoment());
                }
            });
        }

        View view = (View)findViewById(R.id.elementImage);
        if (view != null) {
            // TODO: find the right way to react on click which is different from scale
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Framework.openFinderActivity(AbstractElementAdapterActivity.this, getElement(), getMoment());
                }
            });
        }

        basicsAdapter = new BasicsViewAdapter(this, R.id.basicsContainer);
        basicsAdapter.clear();

        detailsAdapter = new DetailsViewAdapter(this, R.id.detailsContainer);
        detailsAdapter.clear();

        super.setupViews();
    }

    protected void updateBasics(ArrayList<IIconValue> items) {
        basicsAdapter.update(items);
    }

    protected void updateDetails(ArrayList<IIconNameValue> items) {
        detailsAdapter.update(items);
    }
}

