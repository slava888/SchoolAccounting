package de.slava.schoolaccounting.room;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import java.util.List;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.SchoolModel;

/**
 * @author by V.Sysoltsev
 */
public class RoomChildItemAdapter extends ArrayAdapter<Child> {
    private final Fragment parentFragment;
    private final Context context;
    private final int layoutResourceId;
    private final RelativeLayout background;

    public RoomChildItemAdapter(Fragment parent, Context context, int resource, List<Child> objects, RelativeLayout background) {
        super(context, resource, objects);
        this.parentFragment = parent;
        this.context = context;
        this.layoutResourceId = resource;
        this.background = background;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomChildItem itemView = (RoomChildItem)convertView;
        if (itemView == null) {
            itemView = new RoomChildItem(context, background);
            this.parentFragment.registerForContextMenu(itemView);
        }
        itemView.dataInit(getItem(position));
        return itemView;
    }
}
