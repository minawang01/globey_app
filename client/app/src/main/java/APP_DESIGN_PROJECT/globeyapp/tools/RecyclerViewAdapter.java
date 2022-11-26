package APP_DESIGN_PROJECT.globeyapp.tools;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import APP_DESIGN_PROJECT.globeyapp.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Trips> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, List<Trips> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.trip_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        Trips trip = mData.get(position);
        holder.location.setText(trip.getLocation());
        holder.name.setText(trip.getName());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView location;
        TextView name;
        TextView countdown;
        Button delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trip_name);
            location = itemView.findViewById(R.id.trip_location);
            countdown = itemView.findViewById(R.id.count_dwn);
            delete = itemView.findViewById(R.id.delete_btn);
            itemView.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == delete.getId()) {
                if (mClickListener != null)
                    mClickListener.onDeleteBtnClick(view, getBindingAdapterPosition());
            } else {
                if (mClickListener!=null) mClickListener.onItemClick(view, getBindingAdapterPosition());
            }
        }
    }

    public Trips getItem(int id) {
        return mData.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener=itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnClick(View view, int position);
    }

}

