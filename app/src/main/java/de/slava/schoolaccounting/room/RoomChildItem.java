package de.slava.schoolaccounting.room;

import android.content.ClipData;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.ChildDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.RoomDao;

/**
 * @author by V.Sysoltsev
 */
public class RoomChildItem extends LinearLayout {
    public static final String CHILD_TAG = "Child";

    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.textName) TextView textName;

    private Child child;
    private boolean beingDragged = false;

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Log.d(Main.getTag(), "Longpress detected");
        }
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            startDrag();
            return true;
        };
    });

    public RoomChildItem(Context context) {
        super(context);
        init(null, 0);
    }

    public RoomChildItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoomChildItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View view = inflate(getContext(), R.layout.room_child_item, this);
        ButterKnife.bind(this, view);
        setOnTouchListener((_view, _event) -> gestureDetector.onTouchEvent(_event));
//        setOnClickListener(_view -> {
//            ClipData dragData = ClipData.newPlainText(CHILD_TAG, child.getNameFull());
//            View.DragShadowBuilder myShadow = new View.DragShadowBuilder(this);
//            //showContextMenu();
//            Log.d(Main.getTag(), String.format("Drag started with %s", child));
//            _view.startDrag(dragData, myShadow, child, 0);
//        });
        syncModelWithUI();
    }

    public void dataInit(Child child) {
        this.child = child;
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (textName == null || child == null)
            return;
        textName.setText(child.getNameFull());
        int resId = R.drawable.person_1;
        if (child.getImageId() == 2)
            resId = R.drawable.person_2;
        else if (child.getImageId() == 3)
            resId = R.drawable.person_3;
        if (child.getImageId() == 4)
            resId = R.drawable.person_4;
        if (child.getImageId() == 5)
            resId = R.drawable.person_5;
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        Log.d(Main.getTag(), "Request to create context menu");
        super.onCreateContextMenu(menu);
        menu.setHeaderTitle("Geht nach =>");
        for (Room room : getDb().getDao(RoomDao.class).getAll(null, null)) {
            final Room _room = room;
            if (room != child.getRoom()) {
                MenuItem item = menu.add(Menu.NONE, room.getId(), room.getId(), String.format("=> %s", room.getName()));
                item.setOnMenuItemClickListener((menuitem) -> {
                    Log.d(Main.getTag(), String.format("Move to %s", _room));
                    child.moveToToom(_room);
                    return true;
                });
            }
        }
    }

    private void startDrag() {
        ClipData dragData = ClipData.newPlainText(child.getNameFull(), CHILD_TAG);
        View.DragShadowBuilder myShadow = new View.DragShadowBuilder(this);
        Log.d(Main.getTag(), String.format("Drag started with %s", child));
        startDrag(dragData, myShadow, child, 0);
    }
}
