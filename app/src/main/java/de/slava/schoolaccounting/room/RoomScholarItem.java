package de.slava.schoolaccounting.room;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.Scholar;
import de.slava.schoolaccounting.model.SchoolModel;

/**
 * @author by V.Sysoltsev
 */
public class RoomScholarItem extends LinearLayout {

    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.textName) TextView textName;

    private SchoolModel model;
    private Scholar scholar;

    public RoomScholarItem(Context context) {
        super(context);
        init(null, 0);
    }

    public RoomScholarItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RoomScholarItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View view = inflate(getContext(), R.layout.room_scholar_item, this);
        ButterKnife.bind(this, view);
        setOnClickListener(_view -> {
            showContextMenu();
        });
        syncModelWithUI();
    }

    public void dataInit(SchoolModel model, Scholar scholar) {
        this.model = model;
        this.scholar = scholar;
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (textName == null || scholar == null)
            return;
        textName.setText(scholar.getNameFull());
        int resId = R.drawable.person_1;
        if (scholar.getImageId() == 2)
            resId = R.drawable.person_2;
        else if (scholar.getImageId() == 3)
            resId = R.drawable.person_3;
        if (scholar.getImageId() == 4)
            resId = R.drawable.person_4;
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), resId));
    }

    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        Log.d(Main.getTag(), "Request to create context menu");
        super.onCreateContextMenu(menu);
        menu.setHeaderTitle("Geht nach =>");
        for (Room room : model.getRooms()) {
            final Room _room = room;
            if (room != scholar.getRoom()) {
                MenuItem item = menu.add(Menu.NONE, room.getId(), room.getId(), String.format("=> %s", room.getName()));
                item.setOnMenuItemClickListener((menuitem) -> {
                    Log.d(Main.getTag(), String.format("Move to %s", _room));
                    scholar.setRoom(_room);
                    return true;
                });
            }
        }
    }

}
