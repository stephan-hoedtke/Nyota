package com.stho.software.nyota;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stho.software.nyota.universe.Universe;
import com.stho.software.nyota.utilities.Moment;
import com.stho.software.nyota.viewmodels.NyotaViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public abstract class AbstractUniverseFragment extends Fragment {

    protected NyotaViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity()).get(NyotaViewModel.class);
        viewModel.getUniverse().observe(this, universe -> updateUniverse(universe));
    }

    protected void onSetupViews(View view) {
        // empty, override...
    }

    protected Universe getUniverse() {
        return viewModel.getUniverse().getValue();
    }

    protected Moment getMoment() {
        return getUniverse().getMoment();
    }

    abstract protected void updateUniverse(final Universe universe);
}
