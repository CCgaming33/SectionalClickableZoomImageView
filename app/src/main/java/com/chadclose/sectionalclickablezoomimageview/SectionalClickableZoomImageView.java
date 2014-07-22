package com.chadclose.sectionalclickablezoomimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Chad Close on 7/22/14.
 * Uses a helper class created by Chintan Rathod
 */
public class SectionalClickableZoomImageView extends TouchImageView {


    float startX = 0;
    float startY = 0;
    int section = 0;
    boolean longPress = false;
    Bitmap bitmapArea;
    boolean inited = false;

    final Handler handler = new Handler();
    Runnable mLongPressed = new Runnable() {
        public void run() {
            Log.i("", "Long press!");
            setLongPress(true);
        }
    };

    //
    int defaultImageResource;
    int[] sectionColors;
    int[] sectionImageResources;
    SectionClickListener sectionListener;

    public interface SectionClickListener {
        public void onSectionClick(int section);
    }

    public SectionalClickableZoomImageView(Context context) {
        super(context);
    }

    public SectionalClickableZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(int defaultImageResource, int areaImageResource, int[] sectionColors, int[] sectionImageResources, SectionClickListener sectionListener) {
        inited = true;
        bitmapArea = BitmapFactory.decodeResource(getResources(), areaImageResource);
        setOnTouchListener(this);
        this.defaultImageResource = defaultImageResource;
        this.sectionColors = sectionColors;
        this.sectionImageResources = sectionImageResources;
        this.sectionListener = sectionListener;
        setBitmap(defaultImageResource);
    }


    private void setBitmap(int resource) {
        setImageResource(resource);
    }

    private int getSection(MotionEvent motionEvent) {
        final int evX = (int) motionEvent.getX();
        final int evY = (int) motionEvent.getY();
        int touchColor = getHotspotColor(evX, evY);
        // Check all sections
        for(int i = 0; i < sectionColors.length; i++) {
            if (colorMatch(touchColor, sectionColors[i], 5)) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean colorMatch(int a, int b, int tolerance) {
        if(Math.abs(Color.red(a) - Color.red(b)) < tolerance &&
                Math.abs(Color.green(a) - Color.green(b)) < tolerance &&
                Math.abs(Color.blue(a) - Color.blue(b)) < tolerance) {
            return true;
        }
        return false;
    }


    public int getHotspotColor (float x, float y) {

        RectF r = new RectF();
        matrix.mapRect(r);
        float newX =  x - r.left;
        float newY =  y - r.top;
        x = Math.round(newX);
        y = Math.round(newY);

        Bitmap hotspots = Bitmap.createBitmap(bitmapArea, 0, 0, bitmapArea.getWidth(), bitmapArea.getHeight(), matrix, false);

        if(x < hotspots.getWidth() && x > 0 && y < hotspots.getHeight() && y > 0) {
            return hotspots.getPixel(Math.round(x), Math.round(y));
        }
        return Color.argb(0, 0, 0, 0);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(inited) {
            if (view.getId() == this.getId()) {
                final int action = motionEvent.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        startX = motionEvent.getX();
                        startY = motionEvent.getY();
                        handler.postDelayed(mLongPressed, 350);
                        section = getSection(motionEvent);
                        setLongPress(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeCallbacks(mLongPressed);
                        if (longPress) {
                            sectionListener.onSectionClick(section - 1);
                        }
                        setLongPress(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (getDistance(motionEvent) > 8) {
                            handler.removeCallbacks(mLongPressed);
                            setLongPress(false);
                        }
                        break;

                }

            }
            return super.onTouch(view, motionEvent);
        }
        return false;
    }

    private void setLongPress(boolean state) {
        if(state) {
            // Highlight section
            if(section > 0) {
                setBitmap(sectionImageResources[section - 1]);
            } else {
                setBitmap(defaultImageResource);
            }
        } else {
            // Go to default
            setBitmap(defaultImageResource);
        }
        longPress = state;
    }

    private float getDistance(MotionEvent ev) {
        float distanceSum = 0;
        final int historySize = ev.getHistorySize();
        for (int h = 0; h < historySize; h++) {
            // historical point
            float hx = ev.getHistoricalX(0, h);
            float hy = ev.getHistoricalY(0, h);
            // distance between startX,startY and historical point
            float dx = (hx-startX);
            float dy = (hy-startY);
            distanceSum += Math.sqrt(dx*dx+dy*dy);
            // make historical point the start point for next loop iteration
            startX = hx;
            startY = hy;
        }
        // add distance from last historical point to event's point
        float dx = (ev.getX(0)-startX);
        float dy = (ev.getY(0)-startY);
        distanceSum += Math.sqrt(dx*dx+dy*dy);
        return distanceSum;
    }

}