package de.slava.schoolaccounting.room;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.Scholar;
import de.slava.schoolaccounting.model.SchoolModel;


public class RoomView extends RelativeLayout {
    private int colorBorder = Color.RED;
    private int colorBackground = 0xFF99FF99;

    private SchoolModel schoolModel;
    private Room roomModel;
    private PropertyChangeListener roomListener = this::onRoomChanges;

    @Bind(R.id.textHeader) TextView textHeader;
    @Bind(R.id.textNumber) TextView textNumber;
    @Bind(R.id.listView) ListView listPreview;
    private ArrayAdapter scholarsPreviewAdapter;

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
        if (this.roomModel != null)
            this.roomModel.removeChangeListener(roomListener);
        this.roomModel = room;
        if (this.roomModel != null)
            this.roomModel.addChangeListener(roomListener);
        roomListener.propertyChange(new PropertyChangeEvent(this, "all", this.roomModel, this.roomModel));
    }

    private void onRoomChanges(PropertyChangeEvent event) {
        Log.d(Main.getTag(), String.format("Room %s changes to %s", roomModel.getName(), roomModel));
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (textHeader == null || this.roomModel == null)
            return;
        if (scholarsPreviewAdapter == null) {
            scholarsPreviewAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.room_scholar_preview, new ArrayList<>());
            listPreview.setAdapter(scholarsPreviewAdapter);
        }
        textHeader.setText(roomModel.getName());
        Set<Scholar> scholars = roomModel.getScholarsReadOnly();
        textNumber.setText(String.format("%d", scholars.size()));
        scholarsPreviewAdapter.clear();
        List<String> preview = new ArrayList<>();
        for (Scholar s : roomModel.getScholarsReadOnly()) {
            preview.add(s.getNameFull());
            if (preview.size() >= 3) {
                preview.add("...");
                break;
            }
        }
        scholarsPreviewAdapter.addAll(preview);
    }

}
