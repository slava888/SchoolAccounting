package de.slava.schoolaccounting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.filter.FilterModel;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.RoomDao;
import de.slava.schoolaccounting.room.RoomView;

/**
 * @author by V.Sysoltsev
 */
public class MainFragment extends Fragment {

    @Bind(R.id.roomHome) RoomView roomHome;
    @Bind(R.id.room111) RoomView room111;
    @Bind(R.id.roomMB) RoomView roomMB;
    @Bind(R.id.room017) RoomView room017;
    @Bind(R.id.room018) RoomView room018;
    @Bind(R.id.roomTH) RoomView roomTH;
    @Bind(R.id.roomHof) RoomView roomHof;
    private final List<RoomView> allRooms = new ArrayList<>();

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, ret);
        initRooms();
        return ret;
    }

    private void initRooms() {
        if (roomHome == null)
            return;
        Map<String, RoomView> room2View = new HashMap<>();
        room2View.put(getContext().getString(Room.Name.ROOM_HOME.getRoomResourceKey()), roomHome);
        room2View.put(getContext().getString(Room.Name.ROOM_MITTAGSBETREUUNG.getRoomResourceKey()), roomMB);
        room2View.put(getContext().getString(Room.Name.ROOM_111.getRoomResourceKey()), room111);
        room2View.put(getContext().getString(Room.Name.ROOM_017.getRoomResourceKey()), room017);
        room2View.put(getContext().getString(Room.Name.ROOM_018.getRoomResourceKey()), room018);
        room2View.put(getContext().getString(Room.Name.ROOM_TURNHALLE.getRoomResourceKey()), roomTH);
        room2View.put(getContext().getString(Room.Name.ROOM_HOF.getRoomResourceKey()), roomHof);
        List<Room> rooms = getDb().getDao(RoomDao.class).getAll(null, null, null);
        for (Room room : rooms) {
            String id = room.getName();
            RoomView view = room2View.get(id);
            if (view == null) {
                Log.e(getTag(), String.format("No view for room %s", id));
            } else {
                view.dataInit(room);
            }
            allRooms.add(view);
        }
    }

    public void setFilterConnection(FilterModel filterModel) {
        for (RoomView r : allRooms) {
            r.setFilterConnection(filterModel);
        }
    }
}
