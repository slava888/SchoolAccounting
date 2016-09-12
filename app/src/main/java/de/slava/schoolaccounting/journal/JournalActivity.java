package de.slava.schoolaccounting.journal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.scorchworks.demo.SimpleFileDialog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

import static de.slava.schoolaccounting.util.CsvUtils.CSV_EOL;
import static de.slava.schoolaccounting.util.CsvUtils.CSV_SEPARATOR;
import static de.slava.schoolaccounting.util.CsvUtils.wrapCsv;

public class JournalActivity extends AppCompatActivity {

    private final static int FILE_PICKER_RESULT_CODE = 1;

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
        // open file picker dialog
        SimpleFileDialog FileOpenDialog =  new SimpleFileDialog(this, "FileSave", dir -> exportToFile(dir));
        //You can change the default filename using the public variable "Default_File_Name"
        FileOpenDialog.Default_File_Name = "";
        FileOpenDialog.chooseFile_or_Dir();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_PICKER_RESULT_CODE && resultCode == RESULT_OK) {
            // Log.d(Main.getTag(), String.format("Selected: %s", data.getStringExtra(FilePickerActivity.FILE_EXTRA_DATA_PATH)));
        }
    }

    private void exportToFile(String file) {
        Log.d(Main.getTag(), String.format("Export to file %s", file));
        List<JournalEntry> entries = getDb().getDao(JournalDao.class).getAllFiltered(dateRangeFilter);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (JournalEntry entry : entries) {
                writer.write(wrapCsv(entry.getChild().getNameFull()));
                writer.write(CSV_SEPARATOR);
                writer.write(wrapCsv(entry.getTimestampString()));
                writer.write(CSV_EOL);
            }
            writer.close();
        } catch (IOException e) {
            Log.w(Main.getTag(), String.format("%s writing into file %s: %s", e.getClass().getSimpleName(), file, e.getMessage()));
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.protocol_export_error, file))
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
        Toast.makeText(this, getString(R.string.protocol_export_success, file), Toast.LENGTH_LONG).show();
    }
}
