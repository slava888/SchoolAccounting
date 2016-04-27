package de.slava.schoolaccounting.room;

import android.content.ClipDescription;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
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
import de.slava.schoolaccounting.dnd.DNDObject;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.RoomDao;
import lombok.Getter;


public class RoomView extends RelativeLayout {

//    private int colorBorder = Color.RED;
//    private int colorBackground = 0xFF99FF99;

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }
    private Room roomModel;
    private Drawable normalBackground;
    private Drawable savedBackground;
    private FilterModel filterModel;
    private PropertyChangeListener filterChangeListener;

    private static class SavedState extends View.BaseSavedState {
        @Getter
        private int roomId;

        public SavedState(Parcelable source, Room roomModel) {
            super(source);
            this.roomId = roomModel.getId();
        }
        public SavedState(Parcel source) {
            super(source);
            this.roomId = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(roomId);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    };

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

        normalBackground = getBackground();

//        // Load attributes
//        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoomView, defStyle, 0);
//        colorBorder = a.getColor(R.styleable.RoomView_colorBorder, colorBorder);
//        colorBackground = a.getColor(R.styleable.RoomView_colorBackground, colorBackground);
//        a.recycle();

        syncModelWithUI();

        setOnClickListener(this::onClick);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, roomModel);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        Log.d(Main.getTag(), String.format("Restore from state for %d", ss.getRoomId()));
        super.onRestoreInstanceState(ss.getSuperState());
        roomModel = getDb().getDao(RoomDao.class).getById(ss.getRoomId());
    }

    private void onClick(View view) {
        // Log.d(Main.getTag(), String.format("Clicked on %s", view));
        if (getContext() instanceof IRoomSelectionListener) {
            ((IRoomSelectionListener)getContext()).onRoomSelected(roomModel);
        }
    }

    public void dataInit(Room room) {
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
            scholarsPreviewAdapter = new ArrayAdapter<String>(this.getContext(), R.layout.room_child_preview, new ArrayList<>()) {
                // allows to disable click listeners
                @Override
                public boolean isEnabled(int position) {
                    return false;
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View ret = super.getView(position, convertView, parent);
                    ret.setOnClickListener(RoomView.this::onClick);
                    return ret;
                }
            };
            listPreview.setAdapter(scholarsPreviewAdapter);
        }
        textHeader.setText(roomModel.getName());
        Set<Child> children = roomModel.getChildrenFiltered(filterModel);
        int childrenTotal = roomModel.getTotalChildrenCount();
        boolean filterInEffect = childrenTotal != children.size();

        // set text for number of children in the room
        if (filterInEffect)
            textNumber.setText(String.format("%d(%d)", children.size(), childrenTotal));
        else
            textNumber.setText(String.format("%d", children.size()));

        // set special background if no children in the room as effect of filter
        if (children.isEmpty()) {
            Log.d(Main.getTag(), String.format("View %s now has empty background", roomModel.getName()));
            setBackgroundResource(R.drawable.main_room_border_filtered_out);
        } else {
            Log.d(Main.getTag(), String.format("View %s now has normal background", roomModel.getName()));
            setBackgroundResource(R.drawable.main_room_border);
        }
        // if we're in the middle of DnD, update the 'normal' background
        savedBackground = getBackground();

        // children list as preview
        scholarsPreviewAdapter.clear();
        List<String> preview = new ArrayList<>();
        for (Child s : children) {
            // TODO remove fixed 3, write as much entries how much fits the height
            if (preview.size() >= 3) {
                preview.add("...");
                break;
            }
            preview.add(s.getNameFull());
        }
        scholarsPreviewAdapter.addAll(preview);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED: {
                // check if the thing being dragged is something I can accept
                final ClipDescription desc = event.getClipDescription();
                if (desc.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) && DNDObject.isInstanceOf(event.getLocalState(), Child.class)) {
                    //Log.d(Main.getTag(), String.format("The view %s would accept %s", roomModel, event.getLocalState()));
                    savedBackground = getBackground();
                    setBackgroundResource(R.drawable.main_room_border_drag_accept);
                    invalidate();
                    return true;
                }
                break;
            }

            case DragEvent.ACTION_DRAG_ENTERED:
                setBackgroundResource(R.drawable.main_room_border_drag_over);
                invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // need something?
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                setBackgroundResource(R.drawable.main_room_border_drag_accept);
                invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                return moveChildToRoom(((DNDObject<Child>)event.getLocalState()).getObject());

            case DragEvent.ACTION_DRAG_ENDED:
                if (savedBackground != null) {
                    setBackground(savedBackground);
                    invalidate();
                }
                return true;

        }
        return false;
    }

    private boolean moveChildToRoom(Child child) {
        Log.d(Main.getTag(), String.format("Move child %s to room %s", child, roomModel));
        child.moveToToom(roomModel);
        return true;
    }

    public void setFilterConnection(FilterModel filterModel) {
        if (this.filterModel == filterModel) {
            return;
        }
        if (this.filterModel != null && filterChangeListener != null) {
            this.filterModel.removeChangeListener(filterChangeListener);
        }
        this.filterModel = filterModel;
        Runnable applyFilter = () -> {
            syncModelWithUI();
        };
        applyFilter.run();
        if (filterModel != null) {
            if (filterChangeListener == null) {
                filterChangeListener = (event) -> applyFilter.run();
            }
            filterModel.addChangeListener(filterChangeListener);
        }
    }
}
