package APP_DESIGN_PROJECT.globeyapp.tools;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import APP_DESIGN_PROJECT.globeyapp.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Trips> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ContentResolver contentResolver;

    public RecyclerViewAdapter(Context context, List<Trips> data, ContentResolver cr) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.contentResolver = cr;
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
        if (trip.getUri() != null) {
            Bitmap selectedImageBitmap;
            try {
                selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(trip.getUri()));
                holder.imgBtn.setImageBitmap(selectedImageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



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
        ImageButton imgBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.trip_name);
            location = itemView.findViewById(R.id.trip_location);
            countdown = itemView.findViewById(R.id.count_dwn);
            delete = itemView.findViewById(R.id.delete_btn);
            imgBtn = itemView.findViewById(R.id.imageButton);
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

