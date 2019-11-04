package com.stho.software.nyota.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.stho.software.nyota.universe.Constellation;
import com.stho.software.nyota.utilities.Degree;
import com.stho.software.nyota.universe.IElement;
import com.stho.software.nyota.ISkyActivity;
import com.stho.software.nyota.utilities.Point;
import com.stho.software.nyota.data.Settings;
import com.stho.software.nyota.utilities.SphereProjection;
import com.stho.software.nyota.universe.Star;
import com.stho.software.nyota.utilities.Topocentric;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

abstract class AbstractSkyView extends View implements View.OnDragListener {
    private final Paint green = new Paint();
    private final Paint white = new Paint();
    private final Paint yellow = new Paint();
    private final Paint gray = new Paint();
    private final Paint blue = new Paint();
    private final Paint orange = new Paint();
    private final Path path = new Path();
    private HashMap<Star, PointF> positions = new HashMap<>();
    private HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    protected boolean displayNames = true;
    protected boolean displaySymbols = true;
    protected boolean displayMagnitude = true;
    protected Topocentric center = new Topocentric(0,0);
    protected ScaleGestureDetector scaleGestureDetector;
    protected GestureDetector gestureDetector;
    private double zoomAngle = 45;
    private SphereProjection projection = new SphereProjection();
    private boolean isScrollingEnabled = true;
    private boolean isScalingEnabled = true;
    private boolean drawGrid = true;
    private ISkyActivity activity = null;

    public AbstractSkyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public void register(ISkyActivity activity) {
        this.activity = activity;
    }

    void setScrollingEnabled(boolean enable) {
        isScrollingEnabled = enable;
    }

    void setScalingEnabled(boolean enable) {
        isScalingEnabled = enable;
    }

    private void onCreate(final Context context) {
        loadSettings();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                applyScale(detector.getScaleFactor());
                return true;
            }
        });
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                resetTransformation();
                return false;
            }
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                applyScrolling(distanceX, distanceY);
                return false; // super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (activity != null) {
                    return activity.onSingleTapConfirmed(e);
                }
                return super.onSingleTapConfirmed(e);
            }
        });
        yellow.setColor(Color.YELLOW);
        yellow.setAlpha(120);
        yellow.setStyle(Paint.Style.STROKE);
        green.setColor(Color.GREEN);
        green.setAlpha(200);
        green.setStyle(Paint.Style.FILL_AND_STROKE);
        green.setAntiAlias(true);
        green.setTextSize(50);
        white.setColor(Color.WHITE);
        white.setAlpha(255);
        white.setStyle(Paint.Style.FILL_AND_STROKE);
        white.setAntiAlias(true);
        gray.setColor(Color.GRAY);
        gray.setAlpha(200);
        gray.setStyle(Paint.Style.FILL_AND_STROKE);
        gray.setAntiAlias(true);
        gray.setTextSize(40);
        blue.setColor(Color.BLUE);
        blue.setAlpha(200);
        blue.setStyle(Paint.Style.FILL_AND_STROKE);
        blue.setAntiAlias(true);
        blue.setTextSize(40);
        orange.setColor(Color.rgb(253, 106, 2));
        orange.setAlpha(120);
        orange.setStyle(Paint.Style.FILL_AND_STROKE);
        orange.setAntiAlias(true);
        orange.setTextSize(40);
    }

    private void loadSettings() {
        Settings settings = new Settings();
        settings.load(this.getContext());
        onLoadSettings(settings);
    }

    protected void onLoadSettings(Settings settings) {
        // nothing. overwrite ...
    }

    void raiseOnChangeSkyCenter() {
        if (activity != null) {
            activity.onChangeSkyCenter();
        }
    }

    void applyScrolling(double dx, double dy) {
        if (isScrollingEnabled) {
            double zoom = getZoom();
            center.azimuth += Degree.arcTan2(dx, zoom);
            center.altitude -= Degree.arcTan2(dy, zoom);
            raiseOnChangeSkyCenter();
            updateUI();
        }
    }

    public void applyScale(double scaleFactor) {
        if (isScalingEnabled) {
            final double MIN_ZOOM_ANGLE = 0.5;
            final double MAX_ZOOM_ANGLE = 120;
            zoomAngle = Math.max(MIN_ZOOM_ANGLE, Math.min(MAX_ZOOM_ANGLE, zoomAngle / scaleFactor));
            updateUI();
        }
    }

    void resetTransformation() {
        zoomAngle = 45;
        center.azimuth = getReference().azimuth;
        center.altitude = getReference().altitude;
        raiseOnChangeSkyCenter();
        updateUI();
    }

    public void setZoomAngle(double angle) {
        zoomAngle = angle;
        updateUI();
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    public void updateUI() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        canvas.translate(w / 2, h / 2);
        double zoom = getZoom();
        projection.setCenter(center.azimuth, center.altitude);
        positions.clear();
        if (drawGrid) {
            drawGrid(canvas, w, h, zoom);
        }
        onDrawElements(canvas, zoom);
    }

    public Topocentric getCenter() {
        return center;
    }

    abstract protected Topocentric getReference();

    abstract protected void onDrawElements(Canvas canvas, double zoom);

    private double getZoom() {
        int w = getWidth();
        return 0.5 * w / Degree.tan(0.5 * zoomAngle);
    }

    protected void drawStar(Canvas canvas, double zoom, Star star) {
        calculatePosition(zoom, star);
        drawStar(canvas, star, white);
    }

    protected void drawConstellation(Canvas canvas, double zoom, Constellation constellation) {
        calculatePositions(zoom, constellation);
        drawConstellation(canvas, constellation);
    }

    protected void drawSun(Canvas canvas, double zoom, IElement sun) {
        PointF p = getPosition(zoom, sun.getPosition());
        if (isOnScreen(p)) {
            Bitmap bm = getScaledBitmap(sun.getImageId(), 32, 32);
            canvas.drawBitmap(bm, p.x - 16, p.y - 16, white);
        }
    }

    protected void drawMoon(Canvas canvas, double zoom, IElement moon) {
        PointF p = getPosition(zoom, moon.getPosition());
        if (isOnScreen(p)) {
            Bitmap bm = getScaledBitmap(moon.getImageId(), 48, 48);
            canvas.drawBitmap(bm, p.x - 24, p.y - 24, white);
        }
    }

    protected void drawPlanet(Canvas canvas, double zoom, IElement element) {
        PointF p = getPosition(zoom, element.getPosition());
        if (isOnScreen(p)) {
            Bitmap bm = getScaledBitmap(element.getImageId(), 16, 16);
            canvas.drawBitmap(bm, p.x - 8, p.y - 8, white);
        }
    }

    protected void drawName(Canvas canvas, double zoom, IElement element) {
        drawName(canvas, zoom, element.getPosition(), element.getName());
    }

    protected void drawName(Canvas canvas, double zoom, Topocentric position, String name) {
        PointF p = getPosition(zoom, position);
        if (isOnScreen(p)) {
            canvas.drawText(name, p.x + 10, p.y - 10, orange);
        }
    };



    private Bitmap getScaledBitmap(int resourceId, int newWidth, int newHeight) {
        if (bitmaps.containsKey(resourceId)) {
            return bitmaps.get(resourceId);
        } else {
            Bitmap bm = createScaledBitmap(resourceId, newWidth, newHeight);
            bitmaps.put(resourceId, bm);
            return bm;
        }
    }

    private Bitmap createScaledBitmap(int resourceId, int newWidth, int newHeight) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), resourceId);
        return Bitmap.createScaledBitmap(bm, newWidth, newHeight, false);
    }

    private void calculatePosition(double zoom, Star star) {
        positions.put(star, getPosition(zoom, star.getPosition()));
    }

    private void calculatePositions(double zoom, Constellation constellation) {
        for (Star star : constellation.getStars()) {
            positions.put(star, getPosition(zoom, star.getPosition()));
        }
    }

    private @Nullable PointF getPosition(double zoom, Topocentric topocentric) {
        return getPosition(zoom, topocentric.azimuth, topocentric.altitude);
    }

    private @Nullable PointF getPosition(double zoom, double azimuth, double altitude) {
        Point p = projection.getImagePoint(azimuth, altitude);
        if (p != null) {
            float x = (float) (zoom * p.x);
            float y = (float) (zoom * p.y);
            return new PointF(x, -y);
        }
        return null;
    }

    private void drawConstellation(Canvas canvas, Constellation constellation) {
        for (Star star : constellation.getStars()) {
            drawStar(canvas, star, green);
        }
        for (Star[] line : constellation.getLines()) {
            drawLine(canvas, line);
        }
    }

    private void drawStar(Canvas canvas, Star star, Paint color){
        PointF p = positions.get(star);
        if (isOnScreen(p)) {
            if (displayMagnitude) {
                applyMagnitude(color, star.getBrightness());
            }
            canvas.drawCircle(p.x, p.y, 4, color);
            if (displaySymbols) {
                canvas.drawText(star.getSymbol(), p.x, p.y, gray);
            }
        }
    }

    private void applyMagnitude(Paint color, double magnitude) {
        if (magnitude > 5.0)
            color.setAlpha(100);
        else if (magnitude > 3.0)
            color.setAlpha(150);
        else if (magnitude > 1.0)
            color.setAlpha(200);
        else
            color.setAlpha(255);
    }

    private void drawLine(Canvas canvas, Star[] line) {
        boolean first = true;
        path.reset();
        for (Star star : line) {
            PointF p = positions.get(star);
            if (isOnScreen(p)) {
                if (first) {
                    path.moveTo(p.x, p.y);
                    first = false;
                } else {
                    path.lineTo(p.x, p.y);
                }
            } else {
                first = true;
            }
        }
        canvas.drawPath(path, yellow);
    }

    private void drawGrid(Canvas canvas, int w, int h, double zoom) {
        if (center.altitude > 0) {
            for (float deltaAzimuth = 0; deltaAzimuth < 180; deltaAzimuth += 10) {
                double azimuth = center.azimuth + deltaAzimuth;
                for (float altitude = 85; altitude > -85; altitude -= 5) {
                    PointF p = getPosition(zoom, azimuth, altitude);
                    if (p != null) {
                        if (p.x > w || p.y > h) {
                            break;
                        }
                        if (isOnScreen(p)) {
                            canvas.drawCircle(p.x, p.y, 2, blue);
                            canvas.drawCircle(-p.x, p.y, 2, blue);
                        }
                    }
                }
            }
        } else {
            for (float deltaAzimuth = 0; deltaAzimuth < 180; deltaAzimuth += 10) {
                double azimuth = center.azimuth + deltaAzimuth;
                for (float altitude = -85; altitude < 85; altitude += 5) {
                    PointF p = getPosition(zoom, azimuth, altitude);
                    if (p != null) {
                        if (p.x > w || p.y < -h) {
                            break;
                        }
                        if (isOnScreen(p)) {
                            canvas.drawCircle(p.x, p.y, 2, blue);
                            canvas.drawCircle(-p.x, p.y, 2, blue);
                        }
                    }
                }
            }
        }
    }

    private boolean isOnScreen(PointF p) {
        if (p != null) {
            float w = getWidth();
            float h = getHeight();
            return (Math.abs(p.x) < w && Math.abs(p.y) < h);
        }
        return false;
    }
}
