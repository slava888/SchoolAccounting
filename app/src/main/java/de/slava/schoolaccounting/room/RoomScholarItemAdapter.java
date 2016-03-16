package de.slava.schoolaccounting.room;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import de.slava.schoolaccounting.model.Scholar;
import de.slava.schoolaccounting.model.SchoolModel;

/**
 * @author by V.Sysoltsev
 */
public class RoomScholarItemAdapter extends ArrayAdapter<Scholar> {
    private final Fragment parentFragment;
    private final SchoolModel model;
    private Context context;
    int layoutResourceId;

    public RoomScholarItemAdapter(Fragment parent, Context context, int resource, SchoolModel model, List<Scholar> objects) {
        super(context, resource, objects);
        this.parentFragment = parent;
        this.model = model;
        this.context = context;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomScholarItem itemView = (RoomScholarItem)convertView;
        if (itemView == null) {
            itemView = new RoomScholarItem(context);
            this.parentFragment.registerForContextMenu(itemView);
        }
        itemView.dataInit(model, getItem(position));
        return itemView;
    }
}
