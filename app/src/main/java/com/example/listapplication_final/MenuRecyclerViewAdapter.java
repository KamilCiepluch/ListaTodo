package com.example.listapplication_final;

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

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MenuRecyclerViewAdapter(List<TagModel> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tworzenie widoku dla elementu
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_row, parent, false);
        return new ViewHolder(view, listener);
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
        public TextView tagName;
        public Switch status;
        private final OnItemClickListener listener;
        public ViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            tagName = itemView.findViewById(R.id.tagName);
            status = itemView.findViewById(R.id.switchStatus);
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.wtf("Testtuje xdd", "Status = " + status.isChecked());
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


