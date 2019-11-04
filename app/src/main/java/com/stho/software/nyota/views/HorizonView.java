package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.stho.software.nyota.utilities.Angle;
import com.stho.software.nyota.utilities.Orientation;
import com.stho.software.nyota.utilities.Topocentric;

/**
 * Created by shoedtke on 04.10.2016.
 */
public class HorizonView extends View {

    private static Paint pen = new Paint();
    private static Paint green = new Paint();
    private static Paint scale = new Paint();
    private static Paint blue = new Paint();
    private static Paint circle = new Paint();
    private static Path path = new Path();
    private RectF rect = new RectF();
    private double altitude = -17;
    private Orientation orientation = new Orientation();
    private boolean flat = true;

    private static final int[] dots = new int[] { 15, 30, 45, 60, 75, 105, 120, 135, 150, 165 };


    public HorizonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
    }

    private void onCreate() {
        pen.setColor(Color.WHITE);
        pen.setStyle(Paint.Style.STROKE);
        pen.setStrokeWidth(5);

        blue.setColor(Color.rgb(40,60,170));
        blue.setStyle(Paint.Style.FILL);

        green.setColor(Color.GREEN);
        green.setAlpha(200);
        green.setStyle(Paint.Style.FILL_AND_STROKE);
        green.setAntiAlias(true);

        scale.setColor(Color.GREEN);
        scale.setAlpha(200);
        scale.setStyle(Paint.Style.STROKE);
        scale.setAntiAlias(true);
        scale.setStrokeWidth(6);


        circle.setColor(Color.GREEN);
        circle.setStyle(Paint.Style.STROKE);
        circle.setStrokeWidth(2);
    }

    public void setDirection(Topocentric position, Orientation orientation) {
        this.altitude = position.altitude;
        this.orientation = orientation;
        invalidate();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        float t = Math.min(w, h);
        float r = t / 2.9f;
        float a = t / 3.5f;
        float b = t / 9.5f;
        float middle = t / 2.6f;
        float outer = t / 2.2f;

        float alpha = 0 - orientation.getRoll();
        float beta = (flat) ? orientation.getDirection() : orientation.getAltitude();
        float phi = Angle.getAngleDifference((float)altitude, beta);
        if (phi > 90)
            phi = 90;
        if (phi < -90)
            phi = -90;

        canvas.translate(w / 2, h / 2);
        canvas.rotate(alpha);

        rect.set(-r, -r, r, r);

        path.reset();
        path.addArc(rect, 180 + phi, 180 - 2 * phi);
        path.close();
        canvas.drawPath(path, blue);
        canvas.drawCircle(0, 0, r, circle);

        for (int p : dots) {
            float x = (float)(middle * Math.cos(Math.toRadians(p)));
            float y = (float)(middle * Math.sin(Math.toRadians(p)));
            canvas.drawCircle(x, -y, 4, green);
        }

        path.reset();
        path.moveTo(-middle, 0);
        path.lineTo(-outer, 0);
        path.moveTo(middle, 0);
        path.lineTo(outer, 0);
        path.moveTo(0, -middle);
        path.lineTo(0, -outer);
        canvas.drawPath(path, scale);

        canvas.rotate(0 - alpha);
        rect.set(-b, -b, b, b);

        path.reset();
        path.moveTo(-a, 0);
        path.lineTo(-b, 0);
        path.addArc(rect, 180, -180);
        path.lineTo(a, 0);
        canvas.drawPath(path, pen);

        canvas.drawCircle(0, 0, 5, pen);
    }
}
