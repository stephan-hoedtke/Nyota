package com.stho.software.nyota;

import android.os.Bundle;

import com.stho.software.nyota.universe.AbstractPlanet;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.views.PlanetView;

public final class PlanetActivity extends AbstractElementAdapterActivity {

    private static class ViewHolder {
        private PlanetView image;
    }

    private final ViewHolder holder = new ViewHolder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        Universe.getInstance().updateForNow();
        updateUI();
    }

    @Override
    protected void setupViews() {
        setContentView(R.layout.activity_view_planet);
        holder.image = (PlanetView)findViewById(R.id.elementImage);
        super.setupViews();
    }

    @Override
    protected void updateUI(Moment moment, IElement element) {

        AbstractPlanet planet = (AbstractPlanet)element;

        holder.image.setImageResource(planet.getLargeImageId());
        holder.image.setPhase(planet.phase, planet.phaseAngle, planet.zenithAngle);

        updateBasics(planet.getBasics(moment));
        updateDetails(planet.getDetails(moment));
    }
}
