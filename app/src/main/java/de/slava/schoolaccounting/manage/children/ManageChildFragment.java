package de.slava.schoolaccounting.manage.children;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wefika.flowlayout.FlowLayout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.db.CategoryDao;
import de.slava.schoolaccounting.model.db.ChildDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.room.RoomChildItem;


public class ManageChildFragment extends Fragment {
    public final String PARAM_CHILD_ID = "CHILD_ID";
    private Integer originalId;
    private Child child;
    private ChildUpdateListener mListener;

    @Bind(R.id.txtId) TextView txtId;
    @Bind(R.id.imgChild) ImageView imgChild;
    @Bind(R.id.chkActive) CheckBox chkActive;
    @Bind(R.id.txtName) EditText txtName;
    @Bind(R.id.paneCategories) ViewGroup paneCategories;
    @Bind(R.id.btnSave) Button btnSave;
    @Bind(R.id.btnCancel) Button btnCancel;
    private Map<Category, ImageButton> cat2Btn = new HashMap<>();

    public ManageChildFragment() {
        // Required empty public constructor
    }

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(PARAM_CHILD_ID)) {
                int childId = getArguments().getInt(PARAM_CHILD_ID);
                Log.d(Main.getTag(), String.format("Received child ID %d through parameter", childId));
                if (childId >= 0) {
                    Child inDb = getDb().getDao(ChildDao.class).getById(childId);
                    if (inDb != null) {
                        originalId = inDb.getId();
                        child = new Child(inDb);
                    } else {
                        child = new Child();
                    }
                } else {
                    child = new Child();
                }
                syncUIWithData();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_child, container, false);
        ButterKnife.bind(this, view);
        // add categories
        if (!view.isInEditMode()) {
            CategoryDao dao = getDb().getDao(CategoryDao.class);
            List<Category> categories = dao.getAll(null, null, CategoryDao.COLUMN_NAME + " ASC");
            for (Category category : categories) {
                ImageButton btn = new ImageButton(getContext());
                cat2Btn.put(category, btn);
                btn.setLayoutParams(new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT));
                paneCategories.addView(btn);
                int resId = category.getImage().getSid().getResourceId();
                Drawable image = ContextCompat.getDrawable(getContext(), resId);
                btn.setBackground(image);
            }
        }
        // add action listeners
        txtName.setOnEditorActionListener((v, id, event) -> {
            if (id == EditorInfo.IME_ACTION_DONE) {
                if (this.child != null) {
                    this.child.setNameFull(txtName.getText().toString());
                }
            }
            return false;
        });
        chkActive.setOnCheckedChangeListener((v, checked) -> {
            if (this.child != null)
                this.child.setActive(checked);
        });
        for (Map.Entry<Category, ImageButton> it : cat2Btn.entrySet()) {
            final Category category = it.getKey();
            it.getValue().setOnClickListener(v -> {
                if (this.child == null || child.getCategory() == category)
                    return;
                syncCategoryUI(child.getCategory(), false);
                child.setCategory(category);
                syncCategoryUI(child.getCategory(), true);
            });
        }
        btnSave.setOnClickListener(v -> onBtnSave());
        btnCancel.setOnClickListener(v -> onBtnRevert());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChildUpdateListener) {
            mListener = (ChildUpdateListener) context;
        } else {
            Log.w(Main.getTag(), "The activity should implement ChildUpdateListener to get notifications");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setData(Child child) {
        Log.d(Main.getTag(), String.format("Show child %s", child));
        this.originalId = child.getId();
        this.child = new Child(child);
        syncUIWithData();
    }

    private void syncUIWithData() {
        txtId.setText(this.originalId != null ? this.originalId.toString() : "+");
        RoomChildItem.setupImageView(getContext(), imgChild, child);
        chkActive.setChecked(child.isActive());
        txtName.setText(child != null ? child.getNameFull() : "");
        for (Category cat : cat2Btn.keySet()) {
            syncCategoryUI(cat, child != null && child.getCategory() == cat);
        }
    }

    private void syncCategoryUI(Category cat, boolean active) {
        ImageButton btn = cat2Btn.get(cat);
        if (btn == null)
            return;
        btn.setAlpha(active ? 1.f : 0.3f);
    }

    private void onBtnSave() {
        if (child == null)
            return;
        child.setId(originalId);
        setData(getDb().getDao(ChildDao.class).upsert(child));
        if (mListener != null)
            mListener.onChildUpdated(child);
    }

    private void onBtnRevert() {
        if (child == null)
            return;
        if (originalId == null) {
            child = new Child();
        } else {
            Child fromDb = getDb().getDao(ChildDao.class).getById(originalId);
            if (fromDb == null) {
                Main.toast(R.string.manage_child_error_not_found_in_db);
                child = new Child();
            } else {
                child = new Child(fromDb);
            }
        }
        syncUIWithData();
    }

    public static interface ChildUpdateListener {
        void onChildUpdated(Child item);
    }


}
