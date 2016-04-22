package de.slava.schoolaccounting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;
import de.slava.schoolaccounting.journal.JournalActivity;
import de.slava.schoolaccounting.model.AppOption;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.OptionsDao;
import de.slava.schoolaccounting.model.db.RoomDao;
import de.slava.schoolaccounting.room.IRoomSelectionListener;
import de.slava.schoolaccounting.room.RoomFragment;

public class Main extends AppCompatActivity implements IRoomSelectionListener {

    private final static String TAG = "ScAcc";

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

        MainFragment main = (MainFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentMain);

        Integer lastSelectedRoom = getDb().getDao(OptionsDao.class).getOption(AppOption.LAST_VIEWED_ROOM, Integer.class);
        Log.d(Main.getTag(), String.format("TODO: restore last selected room: %s", lastSelectedRoom));
        if (lastSelectedRoom != null) {
            Room room = getDb().getDao(RoomDao.class).getById(lastSelectedRoom);
            if (room != null) {
                selectRoom(room, true);
            }
        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(getTag(), "SlavaSuper");
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
        switch (item.getItemId()) {
            case R.id.menuJournal:
                return openJournal();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRoomSelected(Room room) {
        Log.d(Main.getTag(), String.format("Selected room %s", room));
        selectRoom(room, false);
    }

    private void selectRoom(Room room, boolean justStarting) {
        RoomFragment fragment = (RoomFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentRoom);
        if (fragment != null) {
            // two fragment layout
            fragment.dataInit(room);
            if (!justStarting) {
                // save last selected room in options
                Log.d(Main.getTag(), String.format("Save last selected room: %s", room.getId()));
                getDb().getDao(OptionsDao.class).setOption(AppOption.LAST_VIEWED_ROOM, room.getId());
            }
        } else {
            // single fragment layout
            if (!justStarting) {
                fragment = new RoomFragment();
                fragment.dataInit(room);
                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainLayout, fragment, room.getName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    private EntityManager getDb() {
        return EntityManager.instance(this);
    }

    private boolean openJournal() {
        Intent intent = new Intent(this, JournalActivity.class);
        startActivity(intent);
        return true;
    }
}
