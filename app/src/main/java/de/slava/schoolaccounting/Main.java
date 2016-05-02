package de.slava.schoolaccounting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sanathp.AndroidDatabaseManager;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.filter.FilterWidget;
import de.slava.schoolaccounting.journal.JournalActivity;
import de.slava.schoolaccounting.model.AppOption;
import de.slava.schoolaccounting.model.Room;
import de.slava.schoolaccounting.model.UserEnvironment;
import de.slava.schoolaccounting.model.db.ChildDao;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.OptionsDao;
import de.slava.schoolaccounting.model.db.RoomDao;
import de.slava.schoolaccounting.room.IRoomSelectionListener;
import de.slava.schoolaccounting.room.RoomFragment;
import de.slava.schoolaccounting.util.DateUtils;
import de.slava.schoolaccounting.util.SeqWatcher;

public class Main extends AppCompatActivity implements IRoomSelectionListener {

    private final static String TAG = "ScAcc";
    private static volatile Context appContext;

    private AccessRight accAdv = AccessRight.USER;
    private SeqWatcher<Room.Name> advUserUnlocker = new SeqWatcher<>(
            new Room.Name[]{Room.Name.ROOM_TURNHALLE, Room.Name.ROOM_017, Room.Name.ROOM_HOF, Room.Name.ROOM_HOME}
            , (i) -> accAdv = AccessRight.USER, null, () -> accAdv = AccessRight.ADMIN
    );
    private AccessRight accDev = AccessRight.USER;
    private SeqWatcher<Room.Name> devUnlocker = new SeqWatcher<>(
            new Room.Name[]{Room.Name.ROOM_HOME, Room.Name.ROOM_111, Room.Name.ROOM_MITTAGSBETREUUNG, Room.Name.ROOM_018, Room.Name.ROOM_TURNHALLE, Room.Name.ROOM_HOF}
            , (i) -> accAdv = AccessRight.USER, null, () -> accAdv = AccessRight.DEV
    );
    private Map<String, Room.Name> roomText2Enum;

    private Menu mainMenu;

    @Bind(R.id.filterWidget)
    FilterWidget filterWidget;

    /**
     * Returns the tag for logging, which contains the calling class:line
     *
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
        appContext = getApplicationContext();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        MainFragment main = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMain);
        main.setFilterConnection(filterWidget.getModel());

        RoomFragment fragment = (RoomFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentRoom);
        if (fragment != null)
            fragment.setFilterConnection(filterWidget.getModel());


        roomText2Enum = new HashMap<>();
        for (Room.Name roomNameEnum : Room.Name.values()) {
            roomText2Enum.put(getString(roomNameEnum.getRoomResourceKey()), roomNameEnum);
        }

        onStartRestoreLastStatus();
        onStartCheckMoveChildrenInInitialRoom();

        initUserAccessListeners();

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
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        updateSeq(null);
        switch (item.getItemId()) {
            case R.id.menuJournal:
                return openJournal();
            case R.id.menuDebugDB:
                return openDebugDB();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRoomSelected(Room room) {
        selectRoom(room, false);
        updateSeq(room.getName());
    }

    private void updateSeq(String roomName) {
        Room.Name roomEnum = roomText2Enum != null && roomName != null ? roomText2Enum.get(roomName) : null;
        advUserUnlocker.update(roomEnum);
        devUnlocker.update(roomEnum);
        AccessRight highest = AccessRight.highestOf(accAdv, accDev);
        UserEnvironment ue = UserEnvironment.instance();
        if (ue.getAccessRight() != highest) {
            ue.setAccessRight(highest);
        }
    }

    private void selectRoom(Room room, boolean justStarting) {
        RoomFragment fragment = (RoomFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentRoom);
        if (fragment != null) {
            // two fragment layout
            fragment.dataInit(room);
            if (!justStarting) {
                // save last selected room in options
                // Log.d(Main.getTag(), String.format("Save last selected room: %s", room.getId()));
                getDb().getDao(OptionsDao.class).setOption(AppOption.LAST_VIEWED_ROOM, room.getId());
            }
        } else {
            // single fragment layout
            if (!justStarting) {
                fragment = new RoomFragment();
                fragment.setFilterConnection(filterWidget.getModel());
                fragment.dataInit(room);
                this.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainLayout, fragment, room.getName())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }

    private void initUserAccessListeners() {
        UserEnvironment.instance().addChangeListener((change) -> {
            if (UserEnvironment.PROPERTY_ACCESS_RIGHT.equals(change.getPropertyName())) {
                Log.d(Main.getTag(), String.format("User access changes from %s to %s", change.getOldValue(), change.getNewValue()));
                MenuItem debugDbMenuItem = mainMenu.findItem(R.id.menuDebugDB);
                if (debugDbMenuItem != null) {
                    debugDbMenuItem.setVisible(UserEnvironment.instance().getAccessRight().higherOrEqualsThan(AccessRight.DEV));
                }
            }
        });
    }

    private EntityManager getDb() {
        return EntityManager.instance(this);
    }

    private boolean openJournal() {
        Intent intent = new Intent(this, JournalActivity.class);
        startActivity(intent);
        return true;
    }

    private boolean openDebugDB() {
        Intent intent = new Intent(this, AndroidDatabaseManager.class);
        startActivity(intent);
        return true;
    }

    /**
     * Restores the UI state from the last start, if makes sense
     */
    private void onStartRestoreLastStatus() {
        Integer lastSelectedRoom = getDb().getDao(OptionsDao.class).getOption(AppOption.LAST_VIEWED_ROOM, Integer.class);
        // Log.d(Main.getTag(), String.format("Restore last selected room: %s", lastSelectedRoom));
        if (lastSelectedRoom != null) {
            Room room = getDb().getDao(RoomDao.class).getById(lastSelectedRoom);
            if (room != null) {
                selectRoom(room, true);
            }
        }
    }

    /**
     * Checks if started on another day -> then move all children into initial room
     */
    private void onStartCheckMoveChildrenInInitialRoom() {
        OptionsDao odao = getDb().getDao(OptionsDao.class);
        final Date lastStarted = odao.getOption(AppOption.LAST_DAY_RESET, Date.class);
        Date now = GregorianCalendar.getInstance().getTime();
        if (lastStarted != null) {
            if (!DateUtils.isSameDay(lastStarted, now)) {
                getDb().getDao(ChildDao.class).moveEveryoneToInitialRoom();
            }
        }
        // save now
        odao.setOption(AppOption.LAST_DAY_RESET, now);
    }

    /**
     * Allows to display toast (short-living notification message)
     *
     * @param resourceId
     * @param params
     */
    public static void toast(int resourceId, Object... params) {
        if (appContext == null) {
            Log.w(Main.getTag(), "Attempt to display a toast before main app is initialized");
            return;
        }
        String message = appContext.getString(resourceId, params);
        Log.i(Main.getTag(), message);
        Toast.makeText(appContext, message, Toast.LENGTH_LONG).show();
    }

}

