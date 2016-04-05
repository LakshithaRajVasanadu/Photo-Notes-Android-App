package com.homework3.lakshitha.photonotes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity {

    List<PhotoNotes> photoNotes;
    ListView listView;
    TextView msgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddNoteActivity.class);
                if (intent != null) {
                    startActivity(intent);
                }

            }
        });

        msgTextView = (TextView) findViewById(R.id.msgText);

        listView = (ListView) findViewById(R.id.listView);
        reloadListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("COEN268", "Clicked position: " + position);
                Intent intent = new Intent(getBaseContext(), DetailsActivity.class);
                intent.putExtra("photoNote", photoNotes.get(position));
                if (intent != null) {
                    startActivity(intent);
                }

            }
        });

    }

    private void reloadListView() {
        query();

        if(photoNotes.size() <= 0) {
            Toast.makeText(ListActivity.this, "No notes to display", Toast.LENGTH_SHORT).show();
            msgTextView.setText("No notes to be displayed");

        } else {
            msgTextView.setText("");
        }

        String[] items = new String[photoNotes.size()];
        for(int i=0; i < photoNotes.size(); i++) {
            items[i] = photoNotes.get(i).getCaption();
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));

    }

    @Override
    protected void onResume() {
        super.onResume();
        query();
        reloadListView();
    }

    private void query() {

        photoNotes = new ArrayList<>();

        SQLiteDatabase db = new PhotoNotesDBHelper(this).getWritableDatabase();
        String where = null;
        String[] whereArgs = null;
        String groupBy = null;
        String having = null;
        String order = null;
        String[] resultColumns = {PhotoNotesDBHelper.ID_COLUMN, PhotoNotesDBHelper.CAPTION_COLUMN, PhotoNotesDBHelper.PHOTO_PATH_COLUMN};
        Cursor cursor = db.query(PhotoNotesDBHelper.DATABASE_TABLE, resultColumns, where, whereArgs, groupBy, having, order);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String caption = cursor.getString(1);
            String imagePath = cursor.getString(2);

            photoNotes.add(new PhotoNotes(id, caption, imagePath));
        }
    }


}
