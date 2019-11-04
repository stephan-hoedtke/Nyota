package com.stho.software.nyota;

import android.view.MotionEvent;

public interface ISkyActivity {
    boolean onSingleTapConfirmed(MotionEvent e);
    void onChangeSkyCenter();
}

