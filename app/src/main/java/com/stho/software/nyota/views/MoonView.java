package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by shoedtke on 23.09.2016.
 */
public class MoonView extends ImageView {

    private double phase = 0; // Percentage of illumination
    private double phaseAngle = 0; // Angle of the illuminated limb, from north to east
    private double rotationAngle = 0;   // Local sidereal time (to rotate the moons image)
    private Paint pen = new Paint();
    private Path path = new Path();
    RectF circle = new RectF();
    RectF ellipse = new RectF();

    public MoonView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        onCreate();
    }

    private void onCreate() {
        pen.setColor(Color.BLACK);
        pen.setAlpha(200);
        pen.setStyle(Paint.Style.FILL);
    }

    public void setPhase(double phase, double phaseAngle, double rotationAngle) {
        this.phase = phase;
        this.phaseAngle = phaseAngle;
        this.rotationAngle = rotationAngle;
        this.rotateImage();
        invalidate();
    }

    private void rotateImage() {
        int w = this.getDrawable().getIntrinsicWidth();
        int h = this.getDrawable().getIntrinsicHeight();

        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postRotate((float)rotationAngle, w / 2, h / 2);

        this.setScaleType(ImageView.ScaleType.MATRIX);
        this.setImageMatrix(matrix);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = canvas.getWidth();
        int h = canvas.getHeight();
        int cx = w / 2;
        int cy = h / 2;

        float r = Math.min(cx, cy);
        float d = r * (float) Math.abs(2 * phase - 1);

        canvas.translate(cx, cy);
        canvas.rotate(-(float)phaseAngle);

        circle.set(-r, -r, r, r);
        ellipse.set(-r, -d, r, d);

        if (phase > 0.5) {
            path.reset();
            path.addArc(ellipse, 0, 180);
            path.addArc(circle, 180, -180);
            path.close();
        } else {
            path.reset();
            path.addArc(ellipse, 0, -180);
            path.addArc(circle, 180, -180);
            path.close();
        }

        canvas.drawPath(path, pen);
    }
}

