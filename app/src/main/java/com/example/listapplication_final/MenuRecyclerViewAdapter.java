package com.example.listapplication_final;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MenuRecyclerViewAdapter extends RecyclerView.Adapter<MenuRecyclerViewAdapter.ViewHolder> {
    private final List<TagModel> itemList;
    private OnItemClickListener listener;
    private Context context;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MenuRecyclerViewAdapter(List<TagModel> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tworzenie widoku dla elementu
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_row, parent, false);
        return new ViewHolder(view, listener, context,itemList);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Wiązanie danych z modelem
        TagModel item = itemList.get(position);
        holder.tagName.setText(item.getName());
        holder.status.setChecked(item.isActive());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;
        public TextView tagName;
        public Switch status;
        private final OnItemClickListener listener;
        public ViewHolder(View itemView, OnItemClickListener listener, Context context,List<TagModel> itemList) {
            super(itemView);
            this.context = context;
            tagName = itemView.findViewById(R.id.tagName);
            status = itemView.findViewById(R.id.switchStatus);
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);

                        TagModel tagModel = itemList.get(position);
                        boolean checked = status.isChecked();
                        MyDatabaseHelper database = new MyDatabaseHelper(context);
                        database.updateTagStatus(tagModel.getTagID(), checked);
                        database.close();
                    }
                    Log.wtf("Testtuje xdd", "Position = " + position + " Status = " + status.isChecked());
                }
            });
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }

}


