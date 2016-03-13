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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.SchoolModel;


public class RoomView extends RelativeLayout {
    private int colorBorder = Color.RED;
    private int colorBackground = 0xFF99FF99;

    private SchoolModel schoolModel;
    private Room roomModel;

    @Bind(R.id.header) TextView header;

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
        View view = inflate(getContext(), R.layout.room_view, this);
        ButterKnife.bind(this, view);

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoomView, defStyle, 0);
        colorBorder = a.getColor(R.styleable.RoomView_colorBorder, colorBorder);
        colorBackground = a.getColor(R.styleable.RoomView_colorBackground, colorBackground);
        a.recycle();

        syncModelWithUI();
    }

    public void dataInit(SchoolModel model, Room room) {
        this.schoolModel = model;
        this.roomModel = room;
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (header == null || this.roomModel == null)
            return;
        header.setText(roomModel.getName());
    }

}
