package de.slava.schoolaccounting.room;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
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
        syncModelWithUI();
    }

    public void dataInit(Scholar scholar) {
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
}
