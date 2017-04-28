package com.example.kennybui.ocr;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;


public class Main2Activity extends AppCompatActivity {
    final Context context = this;
    Button addNoteBtn;
    private ArrayList<String> notes;
    private ArrayAdapter<String> notesAdapter;
    private ListView lvnotes;
    private static final String TAG = "Main2Activity";
    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        lvnotes = (ListView) findViewById(R.id.lvnotes);
        notes = new ArrayList<String>();
        notesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,notes);
        lvnotes.setAdapter(notesAdapter);

        setupListViewListerner();
    }


    public void onAddNote(View v){
        final EditText noteEditText = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("New Note");
        dialog.setMessage("Title");
        dialog.setView(noteEditText);
        dialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String note = String.valueOf(noteEditText.getText());
                notesAdapter.add(note);
                filename = note;
                Log.d(TAG, "Note to add: " + note);
            }
        });
        dialog.setNegativeButton("Cancel",null);
        dialog.create();
        dialog.show();

    }

    private void setupListViewListerner(){
        lvnotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(Main2Activity.this, MainActivity1.class);
                intent.putExtra("filename",filename);
                startActivityForResult(intent,0);

            }
        });
        lvnotes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter,View note, int pos, long id){
                notes.remove(pos);
                notesAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }




}
