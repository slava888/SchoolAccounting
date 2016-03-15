package de.slava.schoolaccounting.room;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import de.slava.schoolaccounting.model.Scholar;

/**
 * @author by V.Sysoltsev
 */
public class RoomScholarItemAdapter extends ArrayAdapter<Scholar> {
    private Context context;
    int layoutResourceId;

    public RoomScholarItemAdapter(Context context, int resource, List<Scholar> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RoomScholarItem itemView = (RoomScholarItem)convertView;
        if (itemView == null) {
            itemView = new RoomScholarItem(context);
        }
        itemView.dataInit(getItem(position));
        return itemView;
    }
}
