package de.slava.schoolaccounting;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Layout to place the children in circle around the center point
 *
 * @author by V.Sysoltsev
 */
public class CircleLayout extends ViewGroup {
    private float radius;

    private double START_ANGLE = Math.PI * -0.5;

    public CircleLayout(Context context) {
        this(context, null, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleLayout, 0, 0);
        try {
            radius = a.getDimension(R.styleable.CircleLayout_radius, 50);
            Log.d(Main.getTag(), String.format("Radius is %f", radius));
        } finally {
            a.recycle();
        }

    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        int visibleCount = getVisibleChildCount();

        int childState = 0;
        if (visibleCount < 1) {
            setMeasuredDimension(resolveSizeAndState(0, widthMeasureSpec, childState), resolveSizeAndState(0, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
        } else {
            double x_from = 0;
            double x_to = 0;
            double y_from = 0;
            double y_to = 0;
            double angle = START_ANGLE;
            for (int i = 0; i < count; i++) {
                final View child = getChildAt(i);
                if (child.getVisibility() == GONE)
                    continue;
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                Log.d(Main.getTag(), String.format("Measured %s as %d %d", child, child.getMeasuredWidth(), child.getMeasuredHeight()));
                double pos_x = radius * Math.sin(angle);
                double pos_y = radius * Math.cos(angle);
                double child_size_x2 = child.getMeasuredWidth() * 0.5;
                x_from = Math.min(x_from, pos_x - child_size_x2);
                x_to = Math.max(x_to, pos_x + child_size_x2);
                double child_size_y2 = child.getMeasuredHeight() * 0.5;
                y_from = Math.min(y_from, pos_y - child_size_y2);
                y_to = Math.max(y_to, pos_y + child_size_y2);
                angle += 2*Math.PI/visibleCount;
            }
            int maxWidth = (int)(x_to - x_from) + 10;
            int maxHeight = (int)(y_to - y_from) + 10;

            // Check against our minimum height and width
            maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
            maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

            // Report our final dimensions.
            // setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState), resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
            // this basically ignores layout constraints of the parent
            setMeasuredDimension(maxWidth, maxHeight);
        }

        Log.d(Main.getTag(), String.format("Measured to %d %d", getMeasuredWidth(), getMeasuredHeight()));
    }

    private int getVisibleChildCount() {
        int ret = 0;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            if (getChildAt(i).getVisibility() != GONE)
                ret++;
        }
        return ret;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        int visibleCount = getVisibleChildCount();

        double angle = START_ANGLE;

        int x2 = getMeasuredWidth()/2;
        int y2 = getMeasuredHeight()/2;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE)
                return;

            int pos_x = x2 + (int)(radius * Math.sin(angle));
            int pos_y = y2 + (int)(radius * Math.cos(angle));
            int cx2 = child.getMeasuredWidth()/2;
            int cy2 = child.getMeasuredHeight()/2;
            Log.d(Main.getTag(), String.format("Place %s {with size %d %d} at %d %d", child, child.getMeasuredWidth(), child.getMeasuredHeight(), pos_x-cx2, pos_y-cy2));
            child.layout(pos_x, pos_y, pos_x + cx2 * 2, pos_y + cy2 * 2);
            child.setTranslationX(-cx2);
            child.setTranslationY(-cy2);
            angle += 2 * Math.PI / visibleCount;
        }

        this.setTranslationX(-x2);
        this.setTranslationY(-y2);
    }

}
