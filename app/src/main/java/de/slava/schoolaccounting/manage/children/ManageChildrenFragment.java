package de.slava.schoolaccounting.manage.children;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scorchworks.demo.SimpleFileDialog;

import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.Category;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Image;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.CategoryDao;
import de.slava.schoolaccounting.model.db.ChildDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.ImageDao;
import de.slava.schoolaccounting.model.db.RoomDao;

import static de.slava.schoolaccounting.util.CsvUtils.CSV_EOL;
import static de.slava.schoolaccounting.util.CsvUtils.CSV_SEPARATOR;
import static de.slava.schoolaccounting.util.CsvUtils.wrapCsv;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ManageChildrenFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;
    @Bind(R.id.childrenList) RecyclerView recyclerView;
    private ChildRecyclerViewAdapter listAdapter;
    private FilterModel filterModel;
    private PropertyChangeListener filterChangeListener;

    @Bind(R.id.btnNew) Button btnNew;
    @Bind(R.id.btnExport) Button btnExport;
    @Bind(R.id.btnImport) Button btnImport;
    @Bind(R.id.btnDeleteDeleted) Button btnDeleteDeleted;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ManageChildrenFragment() {
    }

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        Log.d(Main.getTag(), "onInflate called");

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ManageChildrenFragment);
        int myInteger = a.getInt(R.styleable.ManageChildrenFragment_column_count, -1);
        if (myInteger != -1) {
            Log.d(Main.getTag(), String.format("Received argument through styled attributes: %d", myInteger));
            mColumnCount = myInteger;
        }
        a.recycle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            Log.d(Main.getTag(), String.format("Received argument through bundle: %d", mColumnCount));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_children_list, container, false);
        ButterKnife.bind(this, view);
        // layout the list depending on parameter
        if (mColumnCount == 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mColumnCount));
        }
        btnNew.setOnClickListener(v -> onBtnNew());
        btnExport.setOnClickListener(v -> onBtnExport());
        btnImport.setOnClickListener(v -> onBtnImport());
        btnDeleteDeleted.setOnClickListener(v -> onDeleteDeleted());
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refresh(Child child) {
        // TODO: do more intelligently?
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (listAdapter == null) {
            RelativeLayout background = (RelativeLayout)getActivity().findViewById(R.id.mainLayout);
            assert background != null;
            listAdapter = new ChildRecyclerViewAdapter(this.getContext(), new ArrayList<>(), mListener);
            recyclerView.setAdapter(listAdapter);
        }
        List<Child> children = new ArrayList<>();
        for (Child child : getDb().getDao(ChildDao.class).getAll(null, null, ChildDao.COLUMN_NAME)) {
            if (child.isMatch(filterModel)) {
                children.add(child);
            }
        }
        listAdapter.setItems(children);
        listAdapter.notifyDataSetChanged();
    }

    public static interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Child item);
    }

    public void setFilterConnection(FilterModel filterModel) {
        if (this.filterModel == filterModel) {
            return;
        }
        if (this.filterModel != null && filterChangeListener != null) {
            this.filterModel.removeChangeListener(filterChangeListener);
        }
        this.filterModel = filterModel;
        Runnable applyFilter = () -> {
            syncModelWithUI();
        };
        applyFilter.run();
        if (filterModel != null) {
            if (filterChangeListener == null) {
                filterChangeListener = (event) -> applyFilter.run();
            }
            filterModel.addChangeListener(filterChangeListener);
        }
    }

    private Child makeNew() {
        Child ret = new Child();
        Room initialRoom = getDb().getDao(RoomDao.class).findInitial();
        if (initialRoom == null) {
            Main.toast(R.string.childdao_error_noinitialroom);
        } else {
            ret.setRoom(initialRoom);
        }
        ret.setImage(getDb().getDao(ImageDao.class).getBySid(Image.SID.PERSON_1));
        ret.setActive(true);
        List<Category> allCats = getDb().getDao(CategoryDao.class).getAll(null, null, null);
        if (allCats != null && !allCats.isEmpty())
            ret.setCategory(allCats.get(0));
        else
            Main.toast(R.string.categorydao_error_nocategories);
        return ret;
    }

    private void onBtnNew() {
        if (mListener != null) {
            mListener.onListFragmentInteraction(makeNew());
        }
    }

    private void onBtnExport() {
        SimpleFileDialog FileOpenDialog = new SimpleFileDialog(this.getContext(), "FileSave", this::exportToFile);
        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();
    }

    private void exportToFile(String file) {
        Log.d(Main.getTag(), String.format("Export children to file %s", file));
        List<Child> entries = getDb().getDao(ChildDao.class).getAll(null, null, ChildDao.COLUMN_ID + " ASC");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(getString(R.string.manage_child_export_column_id));
            writer.write(CSV_SEPARATOR);
            writer.write(getString(R.string.manage_child_export_column_name));
            writer.write(CSV_SEPARATOR);
            writer.write(getString(R.string.manage_child_export_column_active));
            writer.write(CSV_SEPARATOR);
            writer.write(getString(R.string.manage_child_export_column_category));
            writer.write(CSV_SEPARATOR);
            writer.write(getString(R.string.manage_child_export_column_image));
            writer.write(CSV_EOL);
            for (Child entry : entries) {
                writer.write(String.format("%d", entry.getId()));
                writer.write(CSV_SEPARATOR);
                writer.write(wrapCsv(entry.getNameFull()));
                writer.write(CSV_SEPARATOR);
                writer.write(String.format("%d", entry.isActive() ? 1 : 0));
                writer.write(CSV_SEPARATOR);
                writer.write(entry.getCategory().getName());
                writer.write(CSV_SEPARATOR);
                writer.write(entry.getImage().getSid().name());
                writer.write(CSV_EOL);
            }
            writer.close();
        } catch (IOException e) {
            Log.w(Main.getTag(), String.format("%s writing into file %s: %s", e.getClass().getSimpleName(), file, e.getMessage()));
            new AlertDialog.Builder(this.getContext())
                    .setTitle(getString(R.string.manage_child_export_error, file))
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
        Toast.makeText(this.getContext(), getString(R.string.manage_child_export_success, entries.size(), file), Toast.LENGTH_LONG).show();
    }

    private void onBtnImport() {
        SimpleFileDialog FileOpenDialog = new SimpleFileDialog(this.getContext(), "FileLoad", this::importFromFile);
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();
    }

    private void importFromFile(String file) {
        StringBuilder errors = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNr = 1;
            while ((line = reader.readLine()) != null) {
                Log.d(Main.getTag(), String.format("Line %d: \"%s\"", lineNr, line));

                lineNr++;
            }
        } catch (IOException e) {
            Log.w(Main.getTag(), String.format("%s loadnig the file %s: %s", e.getClass().getSimpleName(), file, e.getMessage()));
            new AlertDialog.Builder(this.getContext())
                    .setTitle(getString(R.string.manage_child_import_error, file))
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
    }

    private void onDeleteDeleted() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.crying_baby)
                .setTitle("Kinder entfernen")
                .setMessage("Diese Aktion wird alle nicht mehr aktive Kinder aus der Datenbank endgültig entfernen. Drücken Sie Ok zum Fortfahren.")
                .setPositiveButton(R.string.ok, (dialog, which) -> this.implDeleteDeleted())
                .setNegativeButton(R.string.cancel, (dialog, which) -> {})
                .show();
    }

    private void implDeleteDeleted() {
        getDb().getDao(ChildDao.class).removeAllInactive();
        syncModelWithUI();
        if (mListener != null) {
            // just to be sure, that it does not display a just deleted entry
            mListener.onListFragmentInteraction(null);
        }
    }
}
