package de.slava.schoolaccounting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.RoomDao;
import de.slava.schoolaccounting.room.RoomView;

/**
 * @author by V.Sysoltsev
 */
public class MainFragment extends Fragment {

    @Bind(R.id.roomHome) RoomView roomHome;
    @Bind(R.id.room011) RoomView room011;
    @Bind(R.id.roomUnknown) RoomView roomUnknown;
    @Bind(R.id.room017) RoomView room017;
    @Bind(R.id.room018) RoomView room018;
    @Bind(R.id.roomTH) RoomView roomTH;
    @Bind(R.id.roomHof) RoomView roomHof;

    private EntityManager getDb() {
        return EntityManager.instance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, ret);
        syncModelWithUI();
        return ret;
    }

    private void syncModelWithUI() {
        if (roomHome == null)
            return;
        Map<String, RoomView> room2View = new HashMap<>();
        room2View.put("Home", roomHome);
        room2View.put("?", roomUnknown);
        room2View.put("011", room011);
        room2View.put("017", room017);
        room2View.put("018", room018);
        room2View.put("TH", roomTH);
        room2View.put("Hof", roomHof);
        List<Room> rooms = getDb().getDao(RoomDao.class).getAll(null, null, null);
        for (Room room : rooms) {
            String id = room.getName();
            RoomView view = room2View.get(id);
            if (view == null) {
                Log.e(getTag(), String.format("No view for room %s", id));
            } else {
                view.dataInit(room);
            }
        }
    }
}
