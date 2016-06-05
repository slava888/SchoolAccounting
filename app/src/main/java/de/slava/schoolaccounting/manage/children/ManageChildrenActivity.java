package de.slava.schoolaccounting.manage.children;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.filter.FilterWidget;
import de.slava.schoolaccounting.model.Child;

public class ManageChildrenActivity extends AppCompatActivity implements ManageChildrenFragment.OnListFragmentInteractionListener {

    @Bind(R.id.filterWidget) FilterWidget filterWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_children);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayShowTitleEnabled(false);

        ManageChildrenFragment fragmentManageChildren = (ManageChildrenFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentListChildren);
        fragmentManageChildren.setFilterConnection(filterWidget.getModel());

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    public void onListFragmentInteraction(Child item) {
        Log.d(Main.getTag(), String.format("Selected %s", item));
    }
}
