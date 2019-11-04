package com.stho.software.nyota;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Satellite;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;

public final class SatelliteActivity extends AbstractElementAdapterActivity {

    private Handler handler = new Handler();

    private static class ViewHolder {
        private ImageView image;
        private Button download;
    }

    private final ViewHolder holder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        Universe.getInstance().updateForNow();
        updateUI();

        holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Framework.openMapActivity(SatelliteActivity.this, getElement(), getMoment());
                }
        });
        holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Framework.openFinderActivity(SatelliteActivity.this, getElement(), getMoment());
                    return true;
                }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Framework.openTLELoaderActivity(SatelliteActivity.this, getElement(), getMoment());
                }
        });
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_view_satellite);
        holder.image = (ImageView)findViewById(R.id.elementImage);
        holder.download = (Button)findViewById(R.id.downloadTLE);
        super.setupViews();
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {

        Satellite satellite = (Satellite)element;
        satellite.update(moment);

        if (satellite.getTLE().isOutdated())
            holder.download.setTextColor(Color.RED);
        else
            holder.download.setTextColor(Color.WHITE);

        holder.image.setImageResource(satellite.getLargeImageId());

        updateBasics(satellite.getBasics(moment));
        updateDetails(satellite.getDetails(moment));
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableTimer();
    }


    private void enableTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Universe.getInstance().updateForNow();
                updateUI();
                handler.postDelayed(this, 2000); // once a second.
            }
        }, 100);
    }

    private void disableTimer() {
        handler.removeCallbacksAndMessages(null);
    }

}


