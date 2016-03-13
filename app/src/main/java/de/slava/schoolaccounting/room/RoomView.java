package de.slava.schoolaccounting.room;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import de.slava.schoolaccounting.R;

/**
 * TODO: document your custom view class.
 */
public class RoomView extends View {
    private String roomName = "";
    private int colorBorder = Color.RED;
    private int colorBackground = 0xFF99FF99;

    private Paint backgroundPaint;
    private TextPaint textPaint;
    private float textWidth;
    private float textHeight;

    public RoomView(Context context) {
        super(context);
        init(null, 0);
    }

    public RoomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoomView, defStyle, 0);

        roomName = a.getString(R.styleable.RoomView_roomName);
        if (roomName == null)
            roomName = "";
        colorBorder = a.getColor(R.styleable.RoomView_colorBorder, colorBorder);
        colorBackground = a.getColor(R.styleable.RoomView_colorBackground, colorBackground);

        a.recycle();

        // Set up a default TextPaint object
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.roomFontSize));

        backgroundPaint = new Paint();
        backgroundPaint.setStrokeWidth(3);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        textPaint.setColor(colorBorder);
        textWidth = textPaint.measureText(roomName);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textHeight = fontMetrics.bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int needWidth = (int)textWidth + paddingLeft + paddingRight;
        int needHeight = (int)textHeight + paddingBottom + paddingTop;

        setMeasuredDimension(Math.min(widthMeasureSpec, needWidth), Math.min(heightMeasureSpec, needHeight));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // draw background and border
        backgroundPaint.setColor(colorBackground);
        backgroundPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0.f, 0.f, (float) contentWidth, (float) contentHeight, backgroundPaint);
        backgroundPaint.setColor(colorBorder);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0.f, 0.f, (float) contentWidth, (float) contentHeight, backgroundPaint);

        // Draw the text.
        canvas.drawText(roomName, paddingLeft, paddingTop + textHeight, textPaint);

        // Draw a separator
        float y = paddingTop + textHeight + 10;
        canvas.drawLine(0.f, y, (float) contentWidth, y, backgroundPaint);
    }

}
