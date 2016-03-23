package de.slava.schoolaccounting.room;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.Child;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.SchoolModel;

/**
 * @author by V.Sysoltsev
 */
public class RoomFragment extends Fragment {

    private Room roomModel;
    private PropertyChangeListener roomListener = this::onRoomChanges;
    private RoomChildItemAdapter listAdapter;

    @Bind(R.id.textHeader) TextView textHeader;
    @Bind(R.id.textNumber) TextView textNumber;
    @Bind(R.id.listView) ListView listScholars;

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
        Log.d(Main.getTag(), String.format("Room %s changes to %s", roomModel.getName(), roomModel));
        syncModelWithUI();
    }

    private void syncModelWithUI() {
        if (textHeader == null || this.roomModel == null)
            return;
        if (listAdapter == null) {
            listAdapter = new RoomChildItemAdapter(this, this.getContext(), R.layout.room_child_item, new ArrayList<>());
            listScholars.setAdapter(listAdapter);
        }
        textHeader.setText(roomModel.getName());
        Set<Child> children = roomModel.getChildrenReadOnly();
        textNumber.setText(String.format("%d", children.size()));
        listAdapter.clear();
        listAdapter.addAll(children);
    }

}
