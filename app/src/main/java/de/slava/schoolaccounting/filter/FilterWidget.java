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
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.db.CategoryDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.util.DelayedSync;
import de.slava.schoolaccounting.util.StringUtils;
import lombok.Getter;

/**
 * @author by V.Sysoltsev
 */
public class FilterWidget extends LinearLayout {

    @Bind(R.id.txtFilterName) EditText txtFilterName;
    private Map<Category, ImageButton> cat2Btn = new HashMap<>();
    @Bind(R.id.btnActivateTextFilter) ImageButton btnActivateTextFilter;

    @Getter
    private FilterModel model = new FilterModel();

    private static class SavedState extends View.BaseSavedState {
        @Getter
        private Set<Integer> categories;
        @Getter
        private String text;
        @Getter
        private boolean textActive;

        public SavedState(Parcelable source, FilterModel model) {
            super(source);
            this.categories = model.getCategories();
            this.text = model.getText();
            this.textActive = model.isTextActive();
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
            textActive = source.readInt() != 0;
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
            out.writeInt(textActive ? 1 : 0);
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
        model.setTextActive(ss.isTextActive());
    }

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    private void init(AttributeSet attrs, int defStyle) {
        // init this
        View view = inflate(getContext(), R.layout.filter_widget, this);
        ButterKnife.bind(this, view);

        // add buttons
        if (!isInEditMode()) {
            CategoryDao dao = getDb().getDao(CategoryDao.class);
            List<Category> categories = dao.getAll(null, null, CategoryDao.COLUMN_NAME + " ASC");
            ViewGroup container = (ViewGroup) findViewById(R.id.catContainer);
            for (Category category : categories) {
                ImageButton btn = new ImageButton(getContext());
                container.addView(btn);
                cat2Btn.put(category, btn);
                int resId = category.getImage().getSid().getResourceId();
                Drawable image = ContextCompat.getDrawable(getContext(), resId);
                btn.setBackground(image);
                final int catId = category.getId();
                getModel().addCategory(catId);
                btn.setOnClickListener(v -> {
                    Log.d(Main.getTag(), String.format("Button %s clicked", category.getName()));
                    if (getModel().isCategoryActivated(catId))
                        getModel().removeCategory(catId);
                    else
                        getModel().addCategory(catId);
                });
            }
        }

        final DelayedSync<String> textSync = new DelayedSync<>(t -> {
            Log.d(Main.getTag(), String.format("Text changes to %s", t));
            model.setText(t);
        });

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
                if (!StringUtils.isBlank(s.toString()))
                    getModel().setTextActive(true);
                textSync.syncDelayed(txtFilterName.getText().toString(), 500);
            }
        });
        txtFilterName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // update model immediately
                textSync.syncImmediately(txtFilterName.getText().toString());
            }
            return false;
        });

        btnActivateTextFilter.setOnClickListener(v -> {
            FilterModel model = getModel();
            if (StringUtils.isBlank(model.getText()))
                model.setTextActive(false);
            else
                getModel().setTextActive(!getModel().isTextActive());
        });

        getModel().addChangeListener(this::syncModelWithUI);
        syncModelWithUI(null);
    }

    private void syncModelWithUI(PropertyChangeEvent event) {
        if (event == null || FilterModel.PROPERTY_CATEGORIES.equals(event.getPropertyName())) {
            for (Map.Entry<Category, ImageButton> it : cat2Btn.entrySet()) {
                int id = it.getKey().getId();
                boolean active = getModel().isCategoryActivated(id);
                it.getValue().setAlpha(active ? 1.f : 0.3f);
            }
        }
        if (event == null || FilterModel.PROPERTY_TEXT.equals(event.getPropertyName())) {
            if (!Objects.equals(txtFilterName.getText().toString(), model.getText())) {
                txtFilterName.setText(model.getText());
            }
        }
        if (event == null || FilterModel.PROPERTY_TEXT_ACTIVE.equals(event.getPropertyName())) {
            btnActivateTextFilter.setAlpha(getModel().isTextActive() ? 1.f : 0.3f);
            txtFilterName.setAlpha(getModel().isTextActive() ? 1.f : 0.3f);
        }
    }
}