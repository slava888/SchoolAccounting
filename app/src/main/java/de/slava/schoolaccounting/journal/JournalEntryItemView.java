package de.slava.schoolaccounting.journal;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.JournalEntry;
import de.slava.schoolaccounting.room.RoomChildItem;
import de.slava.schoolaccounting.util.DateUtils;

/**
 * @author by V.Sysoltsev
 */
public class JournalEntryItemView extends LinearLayout {

    private JournalEntry data;
    @Bind(R.id.imageView) ImageView imageView;
    @Bind(R.id.textName) TextView textName;
    @Bind(R.id.textTime) TextView textTime;

    public JournalEntryItemView(Context context) {
        super(context);
        init(null, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        View view = inflate(getContext(), R.layout.journal_entry_item, this);
        ButterKnife.bind(this, view);
        syncModelWithUI();
    }

    public void initData(JournalEntry entity) {
        this.data = entity;
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (data == null || textName == null)
            return;
        textName.setText(data.getChild().getNameFull());
        imageView.setImageDrawable(RoomChildItem.getChildImage(getContext(), data.getChild().getImage()));
        textTime.setText(DateUtils.dateTimeToString(data.getTimestamp()));
    }

}
