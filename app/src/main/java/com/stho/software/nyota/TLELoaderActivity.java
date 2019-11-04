package com.stho.software.nyota;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.universe.Satellite;
import com.stho.software.nyota.universe.TLELoader;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Formatter;
import com.stho.software.nyota.utilities.IIconValue;
import com.stho.software.nyota.utilities.IconValueList;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class TLELoaderActivity extends Activity {

    private ViewHolder holder = new ViewHolder();
    private Handler handler = new Handler();

    private class ViewHolder {
        ProgressDialog progress;
        Button accept;
        Button download;
    }

    private BasicsViewAdapter basicsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tleloader);

        holder.download = (Button)findViewById(R.id.buttonDownloadTLE);
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });
        holder.accept = (Button)findViewById(R.id.buttonAccept);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accept();
            }
        });
        holder.accept.setVisibility(View.INVISIBLE);

        basicsAdapter = new BasicsViewAdapter(this, R.id.basicsContainer);
        basicsAdapter.clear();

        Satellite satellite = Universe.getInstance().findSatelliteByName("ISS");
        showSatelliteInfo(satellite);
    }

    private void startDownload() {
        try {
            Satellite satellite = Universe.getInstance().findSatelliteByName("ISS");
            TLELoaderTask task = new TLELoaderTask(this);
            task.execute(satellite.getTLE().SatelliteNumber);
        }
        catch(Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void setTLE(String elements) {
        Satellite satellite = Universe.getInstance().findSatelliteByName("ISS");

        if (satellite.getTLE().deserialize(elements)) {
            holder.accept.setVisibility(View.VISIBLE);
        }

        showSatelliteInfo(satellite);
    }

    private void showError(final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert = new AlertDialog.Builder(TLELoaderActivity.this);
                alert.setTitle("Error");
                alert.setMessage(errorMessage);
                alert.setPositiveButton("OK",null);
                alert.show();
            }
        });
    }

    private void accept() {
        Satellite satellite = Universe.getInstance().findSatelliteByName("ISS");
        String elements = satellite.getTLE().serialize();
        save(elements);
        holder.accept.setVisibility(View.INVISIBLE);
        finish();
    }

    private void save(String elements) {
        try {
            Settings settings = new Settings();
            settings.issElements = elements;
            settings.save(this);
        }
        catch(Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void showSatelliteInfo(Satellite satellite) {
        this.basicsAdapter.update(getSatelliteBasics(satellite));
    }

    private ArrayList<IIconValue> getSatelliteBasics(Satellite satellite) {
        IconValueList info = new IconValueList();

        info.add(R.drawable.star, Integer.toString(satellite.getTLE().SatelliteNumber));
        info.add(R.drawable.horizontal, satellite.getPosition().toString());
        info.add(R.drawable.distance, Formatter.df0.format(satellite.getPosition().distance));
        info.add(R.drawable.equatorial, satellite.getLocation().toString());
        info.add(R.drawable.time, Formatter.dateTime.format(satellite.getTLE().getDate()));

        if (satellite.getTLE().isOutdated())
            info.add(R.drawable.time, "OUTDATED");

        return info;
    }

    private static class TLELoaderTask extends AsyncTask<Integer, Void, String> { // input parameter / progress = null / response

        // to close memory leak, the TLELoaderTask is static and uses a weak reference...
        // see: https://developer.android.com/reference/android/os/AsyncTask
        private WeakReference<TLELoaderActivity> activityReference;

        TLELoaderTask(TLELoaderActivity context) {
            activityReference = new WeakReference<>(context);
        }


        @Override
        protected void onPreExecute() {
            // running in UI thread
            TLELoaderActivity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.holder.accept.setVisibility(View.INVISIBLE);
                activity.showProgressDialog();
            }
        }

        @Override
        protected String doInBackground(Integer... params) {
            // running in background thread
            String satelliteNumber = (params.length > 0) ? Integer.toString(params[0]) : "25544";

            try {
                return TLELoader.download(satelliteNumber);

            } catch (Exception ex) {
                TLELoaderActivity activity = activityReference.get();
                if (activity != null && !activity.isFinishing()) {
                    activity.showError(ex.toString());
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(String elements) { // it receives the response of doInBackground()
            // running in UI thread again after background thread finished
            TLELoaderActivity activity = activityReference.get();
            if (activity != null && !activity.isFinishing()) {
                activity.dismissProgressDialog();
                activity.setTLE(elements);
            }
        }
    }

    private void showProgressDialog() {
        holder.progress = new ProgressDialog(this);
        holder.progress.setMessage("Download ...");
        holder.progress.show();
    }

    private void dismissProgressDialog() {
        holder.progress.dismiss();
    }
}
