package com.eldissidemissions.mynotesapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.eldissidemissions.mynotesapp.CustomOnClickListener;
import com.eldissidemissions.mynotesapp.NoteAddUpdateActivity;
import com.eldissidemissions.mynotesapp.R;
import com.eldissidemissions.mynotesapp.entity.NoteModel;

import java.util.ArrayList;


public class RecyclerNoteAdapter extends RecyclerView.Adapter<RecyclerNoteAdapter.ViewHolder> {
    private ArrayList<NoteModel> noteModels = new ArrayList<>();
    private Activity activity;

    public RecyclerNoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<NoteModel> getNoteModels(){
        return noteModels;
    }

    public void setNoteModels(ArrayList<NoteModel> noteModels){
        if (noteModels.size() > 0){
            this.noteModels.clear();
        }
        this.noteModels.addAll(noteModels);

        notifyDataSetChanged();
    }

    public void addItem(NoteModel noteModel){
        this.noteModels.add(noteModel);
        notifyItemInserted(noteModels.size() - 1);
    }

    public void updateItem(int position, NoteModel noteModel){
        this.noteModels.set(position, noteModel);
        notifyItemChanged(position, noteModel);
    }

    public void removeItem(int position){
        this.noteModels.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, noteModels.size());
    }

    @NonNull
    @Override
    public RecyclerNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerNoteAdapter.ViewHolder holder, int position) {
        holder.textViewTitle.setText(noteModels.get(position).getTitle());
        holder.textViewDescription.setText(noteModels.get(position).getDescription());
        holder.textViewDate.setText(noteModels.get(position).getDate());
        holder.cardViewNote.setOnClickListener(new CustomOnClickListener(position, new CustomOnClickListener.OnItemClickCallBack() {
            @Override
            public void clickable(View view, int position) {
                Intent intent = new Intent(activity, NoteAddUpdateActivity.class);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(NoteAddUpdateActivity.EXTRA_NOTE, noteModels.get(position));
                activity.startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return noteModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textViewTitle, textViewDescription, textViewDate;
        final CardView cardViewNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textview_item_title);
            textViewDescription = itemView.findViewById(R.id.textview_description);
            textViewDate = itemView.findViewById(R.id.textview_item_date);
            cardViewNote = itemView.findViewById(R.id.cardview_item_note);
        }
    }
}
