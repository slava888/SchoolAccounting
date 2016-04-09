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
import de.slava.schoolaccounting.model.Room;
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
        RoomFragment fragment = (RoomFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentRoom);
        if (fragment != null) {
            // two fragment layout
            fragment.dataInit(room);
        } else {
            // single fragment layout
            fragment = new RoomFragment();
            fragment.dataInit(room);
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainLayout, fragment, room.getName())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private boolean openJournal() {
        Intent intent = new Intent(this, JournalActivity.class);
        startActivity(intent);
        return true;
    }
}
