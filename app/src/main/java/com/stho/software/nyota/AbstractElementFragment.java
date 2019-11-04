package com.stho.software.nyota;

import android.view.View;

import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.universe.Universe;

public abstract class AbstractElementFragment extends AbstractUniverseFragment {

    private String name;

    IElement getElement() {
        return null;
    }

    @Override
    protected void onSetupViews(View view) {
        super.onSetupViews(view);
    }

    @Override
    protected void updateUniverse(final Universe universe) {
        updateUniverse(universe, getElement());
    }

    abstract protected void updateUniverse(final Universe universe, final IElement element);
}
