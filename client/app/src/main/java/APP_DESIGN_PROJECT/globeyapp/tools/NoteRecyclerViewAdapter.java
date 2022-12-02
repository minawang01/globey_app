package APP_DESIGN_PROJECT.globeyapp.tools;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import APP_DESIGN_PROJECT.globeyapp.R;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.ViewHolder> {
    private List<Notes> notes;
    private LayoutInflater mInflater;
    private FocusChangeListener focusChangeListener;


    public NoteRecyclerViewAdapter(Context context, List<Notes> data) {
        this.mInflater = LayoutInflater.from(context);
        this.notes = data;
    }

    @NonNull
    @Override
    public NoteRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.note_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteRecyclerViewAdapter.ViewHolder holder, int position) {
        Notes note = notes.get(position);
        holder.text.setText(note.getNote());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
        public EditText text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.note);
            text.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            focusChangeListener.onFocusChange(v, getBindingAdapterPosition(), hasFocus, text);
        }
    }

    public void setFocusChangeListener(FocusChangeListener listener) {
        focusChangeListener = listener;
    }

    public interface FocusChangeListener {
        void onFocusChange(View view, int position, boolean hasFocus, EditText text);
    }

    public Notes getItem(int id) {
        return notes.get(id);
    }


}

