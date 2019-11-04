package com.stho.software.nyota;

import android.os.Bundle;
import android.widget.ImageView;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;

public final class ElementActivity extends AbstractElementAdapterActivity {

    private final ViewHolder holder = new ViewHolder();

    private class ViewHolder {
        ImageView image;
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
        setContentView(R.layout.activity_view_element);
        holder.image = (ImageView)findViewById(R.id.elementImage);
        super.setupViews();
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {
        holder.image.setImageResource(element.getLargeImageId());
        updateBasics(element.getBasics(moment));
        updateDetails(element.getDetails(moment));
    }
}
