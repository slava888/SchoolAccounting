package de.slava.schoolaccounting;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.Scholar;
import de.slava.schoolaccounting.model.SchoolModel;
import de.slava.schoolaccounting.model.Storage;
import de.slava.schoolaccounting.room.RoomView;

public class Main extends AppCompatActivity {

    private final static String TAG = "ScAcc";

    private Storage storage;
    private SchoolModel model;

    @Bind(R.id.roomHome) RoomView roomHome;
    @Bind(R.id.room011) RoomView room011;
    @Bind(R.id.roomUnknown) RoomView roomUnknown;
    @Bind(R.id.room017) RoomView room017;
    @Bind(R.id.room018) RoomView room018;
    @Bind(R.id.roomTH) RoomView roomTH;
    @Bind(R.id.roomHof) RoomView roomHof;

    /**
     * Returns the tag for logging, which contains the calling class:line
     * @return
     */
    public static String getTag() {
        final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (ste[i].getMethodName().equals("getTag")) {
                return String.format("%s/%s:%d", TAG, ste[i + 1].getFileName(), ste[i + 1].getLineNumber());
            }
        }
        return TAG;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(getTag(), "SlavaSuper");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        storage = new Storage();
        model = storage.loadModel();
        Map<String, RoomView> room2View = new HashMap<>();
        room2View.put("Home", roomHome);
        room2View.put("Unknown", roomUnknown);
        room2View.put("011", room011);
        room2View.put("017", room017);
        room2View.put("018", room018);
        room2View.put("TH", roomTH);
        room2View.put("Hof", roomHof);
        for (Room room : model.getRooms()) {
            String id = room.getName();
            RoomView view = room2View.get(id);
            if (view == null) {
                Log.e(getTag(), String.format("No view for room %s", id));
            } else {
                view.dataInit(model, room);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
