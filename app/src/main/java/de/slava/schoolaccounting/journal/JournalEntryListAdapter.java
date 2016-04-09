package de.slava.schoolaccounting.journal;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import java.util.List;

import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.JournalEntry;
import de.slava.schoolaccounting.room.RoomChildItem;

/**
 * @author by V.Sysoltsev
 */
public class JournalEntryListAdapter extends ArrayAdapter<JournalEntry> {
    private final Context context;
    private final int layoutResourceId;

    public JournalEntryListAdapter(Context context, int resource, List<JournalEntry> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JournalEntryItemView itemView = (JournalEntryItemView)convertView;
        if (itemView == null) {
            itemView = new JournalEntryItemView(context);
        }
        itemView.initData(getItem(position));
        return itemView;
    }

}
