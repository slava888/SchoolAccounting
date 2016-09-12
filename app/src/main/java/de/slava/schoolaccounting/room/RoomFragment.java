package de.slava.schoolaccounting.room;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.util.NullSafeCompare;

/**
 * @author by V.Sysoltsev
 */
public class RoomFragment extends Fragment {

    private Room roomModel;
    private PropertyChangeListener roomListener = this::onRoomChanges;
    private RoomChildItemAdapter listAdapter;

    @Bind(R.id.textHeader) TextView textHeader;
    @Bind(R.id.textNumber) TextView textNumber;
    @Bind(R.id.listView) GridView listScholars;
    private FilterModel filterModel;
    private PropertyChangeListener filterChangeListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.room_fragment, container, false);
        ButterKnife.bind(this, view);
        syncModelWithUI();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        syncModelWithUI();
    }

    public void dataInit(Room room) {
        if (this.roomModel != null)
            this.roomModel.removeChangeListener(roomListener);
        this.roomModel = room;
        if (this.roomModel != null)
            this.roomModel.addChangeListener(roomListener);
        roomListener.propertyChange(new PropertyChangeEvent(this, "all", this.roomModel, this.roomModel));
    }

    private void onRoomChanges(PropertyChangeEvent event) {
        // Log.d(Main.getTag(), String.format("Room %s changes to %s", roomModel.getName(), roomModel));
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (textHeader == null || this.roomModel == null)
            return;
        if (listAdapter == null) {
            RelativeLayout background = (RelativeLayout)getActivity().findViewById(R.id.mainLayout);
            assert background != null;
            listAdapter = new RoomChildItemAdapter(this, this.getContext(), R.layout.room_child_item, new ArrayList<>(), background);
            listScholars.setAdapter(listAdapter);
        }
        textHeader.setText(roomModel.getName());
        ArrayList<Child> children = new ArrayList<>(roomModel.getChildrenFiltered(filterModel));
        Collections.sort(children, new NullSafeCompare<>(Child::getNameFull));
        textNumber.setText(String.format("%d", children.size()));
        listAdapter.clear();
        listAdapter.addAll(children);
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

}
