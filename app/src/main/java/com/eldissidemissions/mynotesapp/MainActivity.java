package com.eldissidemissions.mynotesapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eldissidemissions.mynotesapp.adapter.RecyclerNoteAdapter;
import com.eldissidemissions.mynotesapp.db.NoteHelper;
import com.eldissidemissions.mynotesapp.entity.NoteModel;
import com.eldissidemissions.mynotesapp.helper.MappingHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoadNotesCallback {
    private ProgressBar progressBar;
    private RecyclerView recyclerViewNote;
    private RecyclerNoteAdapter recyclerNoteAdapter;
    private FloatingActionButton floatingActionButton;

    private NoteHelper noteHelper;

    private static final String EXTRA_STATE = "EXTRA_STATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Notes");
        }

        progressBar = findViewById(R.id.progress_bar);
        recyclerViewNote = findViewById(R.id.rv_notes);
        floatingActionButton = findViewById(R.id.fab);
        recyclerViewNote.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNote.setHasFixedSize(true);

        recyclerNoteAdapter = new RecyclerNoteAdapter(this);
        recyclerViewNote.setAdapter(recyclerNoteAdapter);

        floatingActionButton.setOnClickListener(this);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        //proses ambil data (secara asyncronous)
        new LoadNotesAsync(noteHelper, this).execute();

        if (savedInstanceState == null){
            //proses ambil data
            new LoadNotesAsync(noteHelper, this).execute();
        }else {
            ArrayList<NoteModel> noteModels = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (noteModels != null){
                recyclerNoteAdapter.setNoteModels(noteModels);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fab){
            Intent intent = new Intent(MainActivity.this, NoteAddUpdateActivity.class);
            startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null){
            //Akan dipanggil jika requestCode nya add
            if (requestCode == NoteAddUpdateActivity.REQUEST_ADD){
                NoteModel noteModel = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);

                recyclerNoteAdapter.addItem(noteModel);
                recyclerViewNote.smoothScrollToPosition(recyclerNoteAdapter.getItemCount() - 1);

                showSnackbarMessage("Satu item berhasil ditambahkan");
            }
        }
        //Update dan Delete memiliki request code yang sama, tapi result codenya beda-beda
        else if (requestCode == NoteAddUpdateActivity.REQUEST_UPDATE){
            if (requestCode == NoteAddUpdateActivity.RESULT_UPDATE){
                NoteModel noteModel = data.getParcelableExtra(NoteAddUpdateActivity.EXTRA_NOTE);
                int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);

                recyclerNoteAdapter.updateItem(position, noteModel);
                recyclerViewNote.smoothScrollToPosition(position);

                showSnackbarMessage("Satu item berhasil diubah");
            }else if (resultCode == NoteAddUpdateActivity.RESULT_DELETE){
                int position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0);
                recyclerNoteAdapter.removeItem(position);

                showSnackbarMessage("Satu item berhasil dihapus");
            }
        }
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(recyclerViewNote, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.close();
    }

    @Override
    public void preExecute() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<NoteModel> noteModels) {
        progressBar.setVisibility(View.INVISIBLE);
        if (noteModels.size() > 0){
            recyclerNoteAdapter.setNoteModels(noteModels);
        }else {
            recyclerNoteAdapter.setNoteModels(new ArrayList<NoteModel>());
            showSnackbarMessage("Tidak Ada data saat ini");
        }
    }

    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<NoteModel>>{
        private final WeakReference<NoteHelper> noteHelperWeakReference;
        private final WeakReference<LoadNotesCallback> loadNotesCallbackWeakReference;

        public LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback loadNotesCallback) {
            noteHelperWeakReference = new WeakReference<>(noteHelper);
            loadNotesCallbackWeakReference = new WeakReference<>(loadNotesCallback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadNotesCallbackWeakReference.get().preExecute();

        }

        @Override
        protected ArrayList<NoteModel> doInBackground(Void... voids) {
            Cursor cursor = noteHelperWeakReference.get().queryAll();
            return MappingHelper.mapCursorToArrayList(cursor);
        }

        @Override
        protected void onPostExecute(ArrayList<NoteModel> noteModels) {
            super.onPostExecute(noteModels);
            loadNotesCallbackWeakReference.get().postExecute(noteModels);
        }
    }

    //Untuk menjaga state
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}

interface LoadNotesCallback{
    void preExecute();
    void postExecute(ArrayList<NoteModel> noteModels);
}
