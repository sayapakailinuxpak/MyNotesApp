package com.eldissidemissions.mynotesapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eldissidemissions.mynotesapp.db.NoteHelper;
import com.eldissidemissions.mynotesapp.entity.NoteModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.eldissidemissions.mynotesapp.db.DatabaseContract.NoteColumns.DATE;
import static com.eldissidemissions.mynotesapp.db.DatabaseContract.NoteColumns.DESCRIPTION;
import static com.eldissidemissions.mynotesapp.db.DatabaseContract.NoteColumns.TITLE;

public class NoteAddUpdateActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextTitle, editTextDescription;
    private Button buttonSubmit;

    private boolean isEdit = false;
    private NoteModel noteModel;
    private int position;
    private NoteHelper noteHelper;

    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_POSITION = "extra_position";
    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD = 101;
    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE = 201;
    public static final int RESULT_DELETE = 301;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_add_update);
        editTextTitle = findViewById(R.id.edt_title);
        editTextDescription = findViewById(R.id.edt_description);
        buttonSubmit = findViewById(R.id.btn_submit);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        noteModel = getIntent().getParcelableExtra(EXTRA_NOTE);
        if (noteModel != null){
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        }else {
            noteModel = new NoteModel();
        }

        String actionBarTitle = "Ubah";
        String btnTitle = "Update";

        if (isEdit){
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (noteModel != null){
                editTextTitle.setText(noteModel.getTitle());
                editTextDescription.setText(noteModel.getDescription());
            }
        }else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }


        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }

        buttonSubmit.setText(btnTitle);

        buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit){
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title)){
                editTextTitle.setError("Field cannot be blank!");
                return;
            }

            noteModel.setTitle(title);
            noteModel.setDescription(description);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_NOTE, noteModel);
            intent.putExtra(EXTRA_POSITION, position);

            //Gunakan ContentValues untuk menampung data
            ContentValues contentValues = new ContentValues();
            contentValues.put(TITLE, title);
            contentValues.put(DESCRIPTION, description);

            if (isEdit){
                long result = noteHelper.update(String.valueOf(noteModel.getId()), contentValues);
                if (result > 0){
                    setResult(RESULT_UPDATE, intent);
                    finish();
                }else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();
                }
            }else {
                noteModel.setDate(getCurrentDate());
                contentValues.put(DATE, getCurrentDate());
                long result = noteHelper.insert(contentValues);

                if (result > 0){
                    noteModel.setId((int) result);
                    setResult(REQUEST_ADD, intent);
                    finish();
                }else {
                    Toast.makeText(NoteAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit){
            getMenuInflater().inflate(R.menu.menu_form, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete :
                showAlertDialog(ALERT_DIALOG_DELETE);
                break;
            case android.R.id.home :
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ALERT_DIALOG_CLOSE);
    }

    private void showAlertDialog(int type){
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;

        if (isDialogClose){
            dialogTitle = "Batal";
            dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?";
        }else {
            dialogMessage = "Apakah anda yaki ingin menghapus item ini?";
            dialogTitle = "Hapus Note";
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isDialogClose){
                            finish();
                        }else {
                            long result = noteHelper.deleteById(String.valueOf(noteModel.getId()));
                            if (result > 0){
                                Intent intent = new Intent();
                                intent.putExtra(EXTRA_POSITION, position);
                                setResult(RESULT_DELETE, intent);
                                finish();
                            }else {
                                Toast.makeText(NoteAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
