package com.stho.software.nyota;

import android.os.Bundle;
import android.view.MotionEvent;

import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.IconNameValueList;
import com.stho.software.nyota.utilities.IconValueList;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.views.ConstellationView;

public final class ConstellationActivity extends AbstractElementAdapterActivity implements ISkyActivity {

    private final ViewHolder holder = new ViewHolder();

    private class ViewHolder {
        ConstellationView constellation;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        Universe.getInstance().updateForNow();
        updateUI();
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_view_constellation);
        holder.constellation = findViewById(R.id.constellation);
        holder.constellation.setZoomAngle(60);
        holder.constellation.register(this);
        holder.constellation.setConstellation((Constellation) getElement());
        super.setupViews();
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {
        updateBasics(getBasics(moment, element));
        updateDetails(getDetails(moment, element));
        holder.constellation.setConstellation((Constellation) getElement());
        holder.constellation.updateUI();
    }

    private IconValueList getBasics(Moment moment, IElement element) {
        IconValueList list = element.getBasics(moment);
        list.add(R.drawable.navi, moment.getLocation().toString());
        list.add(R.drawable.time, moment.getUTC().toString());
        return list;
    }

    private IconNameValueList getDetails(Moment moment, IElement element) {
        return element.getDetails(moment);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Framework.openFinderActivity(this, getElement(), getMoment());
        return true;
    }

    @Override
    public void onChangeSkyCenter() {
        // do nothing here
    }
}
