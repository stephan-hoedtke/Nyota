package com.stho.software.nyota.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.stho.software.nyota.utilities.Orientation;
import com.stho.software.nyota.utilities.Topocentric;

/**
 * Created by shoedtke on 04.10.2016.
 */
public class DirectionPointerView extends View {

    private static Paint pen1 = new Paint();
    private static Paint pen2 = new Paint();
    private static Paint black = new Paint();
    private static Paint green = new Paint();
    private static Path path = new Path();
    private double azimuth = 17;
    private Orientation orientation = new Orientation();

    private static final int[] dots = new int[] { 15, 30, 45, 60, 75, 105, 120, 135, 150, 165, 195, 210, 225, 240, 255, 285, 300, 315, 330, 345 };

    public DirectionPointerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate();
    }

    private void onCreate() {
        pen1.setColor(Color.WHITE);
        pen1.setStyle(Paint.Style.FILL_AND_STROKE);

        pen2.setColor(Color.LTGRAY);
        pen2.setStyle(Paint.Style.FILL_AND_STROKE);

        black.setColor(Color.BLACK);
        black.setStyle(Paint.Style.FILL_AND_STROKE);

        green.setColor(Color.GREEN);
        green.setAlpha(200);
        green.setStyle(Paint.Style.FILL_AND_STROKE);
        green.setAntiAlias(true);
        green.setTextSize(50);
    }

    public void setDirection(Topocentric position, Orientation orientation) {
        this.azimuth = position.azimuth;
        this.orientation = orientation;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        float t = Math.min(w, h);
        float a = t / 2.7f;
        float b = t / 27f;
        float c = t / 23f;
        float middle = t / 2.6f;
        float letter = t / 2.5f;

        canvas.translate(w / 2, h / 2);
        canvas.rotate(0 - (float)orientation.getAzimuth());

        for (int p : dots) {
            float x = (float)(middle * Math.cos(Math.toRadians(p)));
            float y = (float)(middle * Math.sin(Math.toRadians(p)));
            canvas.drawCircle(x, y, 4, green);
        }

        drawTextCentered(canvas, "N", 0, -letter, green);
        drawTextCentered(canvas, "E", letter, 0, green);
        drawTextCentered(canvas, "S", 0, letter, green);
        drawTextCentered(canvas, "W", -letter, 0, green);

        canvas.rotate((float)azimuth);

        path.reset();
        path.moveTo(0, -a);
        path.lineTo(-b, c);
        path.lineTo(0, c);
        path.close();
        canvas.drawPath(path, pen1);

        path.reset();
        path.moveTo(0, -a);
        path.lineTo(b, c);
        path.lineTo(0, c);
        path.close();
        canvas.drawPath(path, pen2);

        canvas.drawCircle(0, 0, 7, black);


    }

    private void drawTextCentered(Canvas canvas, String text, float x, float y, Paint pen) {
        float w = pen.measureText(text);
        float h = pen.descent() + pen.ascent();
        canvas.drawText(text, x - w / 2f, y - h / 2f, pen);
    }
}

