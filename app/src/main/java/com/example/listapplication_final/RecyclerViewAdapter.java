package com.example.listapplication_final;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<DataModel> itemList;
    private  ViewGroup parent;
    private Context context;
    public RecyclerViewAdapter(List<DataModel> itemList,Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tworzenie widoku dla elementu
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        this.parent = parent;
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Wiązanie danych z modelem

        //todo image
        DataModel item = itemList.get(position);
        holder.image.setImageResource(R.drawable.ic_launcher_background);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        holder.creation_time.setText(item.getCreationTime());
        holder.execution_time.setText(item.getExecutionTime());
        holder.status.setChecked(item.getFinished());
        holder.notifications.setChecked(item.getNotifications());
        byte [] image = item.getImage();
        if(image!=null)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.image.setImageBitmap(bmp);

        }
        MyDatabaseHelper database = new MyDatabaseHelper(context);
        ArrayList<String> attachments = database.getAttachmentsList(item.getPrimaryKey());
        MyListAdapter listAdapter = new MyListAdapter(context,20,attachments);
        holder.attachments.setAdapter(listAdapter);


        holder.categoryTag.setText(database.getTagName(item.getCategoryId()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public TextView description;
        public TextView creation_time;
        public TextView execution_time;

        public SwitchCompat status;
        public SwitchCompat notifications;
        public TextView categoryTag;
        public ListView attachments;
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            creation_time = itemView.findViewById(R.id.creation_time);
            execution_time = itemView.findViewById(R.id.execution_time);
            status = itemView.findViewById(R.id.status);
            notifications = itemView.findViewById(R.id.notifications);
            categoryTag = itemView.findViewById(R.id.categoryTag);
            attachments = itemView.findViewById(R.id.attachments);
        }
    }

}
