package com.eldissidemissions.mynotesapp.helper;

import android.database.Cursor;

import com.eldissidemissions.mynotesapp.db.DatabaseContract;
import com.eldissidemissions.mynotesapp.entity.NoteModel;

import java.util.ArrayList;

public class MappingHelper {
    public static ArrayList<NoteModel> mapCursorToArrayList(Cursor cursor){
        ArrayList<NoteModel> noteModels = new ArrayList<>();

        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE));
            noteModels.add(new NoteModel(id, title, description, date));
        }

        return noteModels;
    }
}
