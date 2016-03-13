package de.slava.schoolaccounting.room;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.slava.schoolaccounting.R;


public class RoomView extends RelativeLayout {
    private String roomName = "";
    private int colorBorder = Color.RED;
    private int colorBackground = 0xFF99FF99;

    private TextView header;

    public RoomView(Context context) {
        super(context, null, R.attr.roomStyle);
        init(null, 0);
    }

    public RoomView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.roomStyle);
        init(attrs, 0);
    }

    public RoomView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // init this
        inflate(getContext(), R.layout.room_view, this);
        header = (TextView)findViewById(R.id.header);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoomView, defStyle, 0);
        roomName = a.getString(R.styleable.RoomView_roomName);
        if (roomName == null)
            roomName = "";
        colorBorder = a.getColor(R.styleable.RoomView_colorBorder, colorBorder);
        colorBackground = a.getColor(R.styleable.RoomView_colorBackground, colorBackground);

        header.setText(roomName);

        a.recycle();
    }

}
