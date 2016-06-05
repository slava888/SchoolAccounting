package de.slava.schoolaccounting.manage.children;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.db.ChildDao;
import de.slava.schoolaccounting.model.db.EntityManager;

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

    @Bind(R.id.btnExport) Button btnExport;
    @Bind(R.id.btnImport) Button btnImport;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
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
        btnExport.setOnClickListener(v -> onBtnExport());
        btnImport.setOnClickListener(v -> onBtnImport());
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

    private void onBtnExport() {
        Log.d(Main.getTag(), "TODO: export");
    }

    private void onBtnImport() {
        Log.d(Main.getTag(), "TODO: import");
    }
}
