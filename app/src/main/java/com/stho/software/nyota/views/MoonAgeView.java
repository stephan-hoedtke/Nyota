package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.stho.software.nyota.R;

/**
 * Created by shoedtke on 23.09.2016.
 */
public class MoonAgeView extends ImageView {

    private Paint white = new Paint();
    private Paint red = new Paint();
    private Path path = new Path();

    private double age = 0;
    private double before = 0.5;
    private double after = 0.5;

    public MoonAgeView(Context context) {
        super(context);
        onCreate();
    }

    public MoonAgeView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        onCreate();
    }


    private void onCreate() {
        white.setColor(Color.WHITE);
        white.setAlpha(200);
        white.setStyle(Paint.Style.STROKE);

        red.setColor(Color.RED);
        red.setAlpha(200);
        red.setStrokeWidth(3);
        red.setStyle(Paint.Style.STROKE);

        this.setImageResource(R.drawable.moonly); // altitude := wrap_content, width = match_parent
    }

    public void setAge(double age, double before, double after) {
        this.age = age;
        this.before = before;
        this.after = after;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int cy = h / 2;
        int l = w - h - h;
        int w1 = (int) (l * before);
        int w2 = (int) (l * after);
        int c1 = w/2 - w1;
        int c2 = w/2 + w2;

        float r = h / 2;
        float p = c1 + (float)age * (w1 + w2);

        canvas.drawCircle(c1, cy, r, white);
        canvas.drawCircle(c2, cy, r, white);
        canvas.drawLine(p, 0, p, h, red);
    }
}

