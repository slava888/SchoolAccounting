package de.slava.schoolaccounting.journal;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.slava.schoolaccounting.Main;
import de.slava.schoolaccounting.R;
import de.slava.schoolaccounting.model.DateRangeFilter;
import de.slava.schoolaccounting.model.JournalEntry;
import de.slava.schoolaccounting.model.db.EntityManager;
import de.slava.schoolaccounting.model.db.JournalDao;

public class JournalActivity extends AppCompatActivity {

    @Bind(R.id.journalListView) GridView listView;
    @Bind(R.id.calendar) DateRangeWidget calendar;
    private ArrayAdapter journalListAdapter;

    private DateRangeFilter dateRangeFilter;

    private EntityManager getDb() {
        return EntityManager.instance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendar.setOnBtnApply(() -> onApplyDateRange(calendar.getModel()));
        calendar.setOnBtnExportToCsv(() -> onExportDateRange(calendar.getModel()));

        onApplyDateRange(calendar.getModel());
    }

    private void syncModelWithUI() {
        if (journalListAdapter == null) {
            journalListAdapter = new JournalEntryListAdapter(this, R.layout.journal_entry_item, new ArrayList<>());
            listView.setAdapter(journalListAdapter);
        }
        List<JournalEntry> entries = getDb().getDao(JournalDao.class).getAllFiltered(dateRangeFilter);
        journalListAdapter.clear();
        journalListAdapter.addAll(entries);
    }

    private void onApplyDateRange(DateRangeFilter filter) {
        dateRangeFilter = filter;
        syncModelWithUI();
    }

    private void onExportDateRange(DateRangeFilter filter) {
        // TODO
        Log.d(Main.getTag(), "TODO: export");
    }

}
