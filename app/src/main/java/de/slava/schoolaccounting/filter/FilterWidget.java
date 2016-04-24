package de.slava.schoolaccounting.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.beans.PropertyChangeEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.db.CategoryDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import lombok.Getter;

/**
 * @author by V.Sysoltsev
 */
public class FilterWidget extends LinearLayout {
    public static interface IFilterListener {
        public void onFilterChanges(FilterModel filter);
    }

    @Bind(R.id.txtFilterName) EditText txtFilterName;

    @Getter
    private FilterModel model;

    private Set<IFilterListener> filterListeners = new HashSet<>();

    private static class SavedState extends View.BaseSavedState {
        @Getter
        private Set<Integer> categories;
        @Getter
        private String text;

        public SavedState(Parcelable source, FilterModel model) {
            super(source);
            this.categories = model.getCategories();
            this.text = model.getText();
        }

        public SavedState(Parcel source) {
            super(source);
            int a[] = source.createIntArray();
            categories = new HashSet<>();
            if (a != null) {
                for (int v : a) {
                    categories.add(v);
                }
            }
            text = source.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(categories.size());
            int[] a = new int[categories.size()];
            int i = 0;
            for (Integer v : categories)
                a[i++] = v;
            out.writeIntArray(a);
            out.writeString(text);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    ;

    public FilterWidget(Context context) {
        super(context, null, R.attr.filterWidgetStyle);
        init(null, 0);
    }

    public FilterWidget(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.filterWidgetStyle);
        init(attrs, 0);
    }

    public FilterWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, model);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        model.setCategories(ss.getCategories());
        model.setText(ss.getText());
    }

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    private void init(AttributeSet attrs, int defStyle) {
        // init this
        View view = inflate(getContext(), R.layout.filter_widget, this);
        ButterKnife.bind(this, view);
        model = new FilterModel();

        // add buttons
        CategoryDao dao = getDb().getDao(CategoryDao.class);
        List<Category> categories = dao.getAll(null, null, CategoryDao.COLUMN_NAME + " ASC");
        ViewGroup container = (ViewGroup)findViewById(R.id.catContainer);
        for (Category category : categories) {
            ImageButton btn = new ImageButton(getContext());
            container.addView(btn);
            int resId = category.getImage().getSid().getResourceId();
            Drawable image = ContextCompat.getDrawable(getContext(), resId);
            btn.setBackground(image);
            btn.setOnClickListener(v -> {
                Log.d(Main.getTag(), String.format("Button %s clicked", category.getName()));
            });
        }

        // bind text fields
        txtFilterName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                model.setText(txtFilterName.getText().toString());
                // TODO: delay by 1 sec?
                notifyFilterListeners(model);
            }
        });
    }

    public void addFilterListener(IFilterListener listener) {
        filterListeners.add(listener);
    }

    public void removeFilterListener(IFilterListener listener) {
        filterListeners.remove(listener);
    }

    public void notifyFilterListeners(FilterModel model) {
        for (IFilterListener l : filterListeners) {
            l.onFilterChanges(model);
        }
    }
}