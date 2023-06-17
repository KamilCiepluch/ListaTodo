package com.example.listapplication_final;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<DataModel> itemList;
    private  ViewGroup parent;
    private Context context;
    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RecyclerViewAdapter(List<DataModel> itemList,Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Tworzenie widoku dla elementu
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        this.parent = parent;
        return new ViewHolder(view,listener);
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Wiązanie danych z modelem

        //todo image
        DataModel item = itemList.get(position);

        MyDatabaseHelper database = new MyDatabaseHelper(context);
        ArrayList<String> attachments = database.getAttachmentsList(item.getPrimaryKey());
        if(!attachments.isEmpty()) {
            holder.imageAttachment.setImageResource(R.drawable.attachment_icon);
        }



        holder.title.setText(item.getTitle());
        holder.execution_time.setText(item.getExecutionTime());
        holder.categoryTag.setText(database.getTagName(item.getCategoryId()));


        holder.status.setText(context.getResources().getString(R.string.statusN));
        if(item.getFinished()) {
            holder.status.setText(context.getResources().getString(R.string.statusF));
        }
        holder.notifications.setText(context.getResources().getString(R.string.notificationsOff));
        if(item.getNotifications()) {
            holder.notifications.setText(context.getResources().getString(R.string.notificationsOn));
        }



    //    holder.description.setText(item.getDescription());
     //   holder.creation_time.setText(item.getCreationTime());

       // holder.status.setText(item.getFinished());
      //  holder.notifications.setText(item.getNotifications());
        byte [] image = item.getImage();
        if(image!=null)
        {
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            holder.image.setImageBitmap(bmp);

        }
     //   MyListAdapter listAdapter = new MyListAdapter(context,20,attachments);
     //   holder.attachments.setAdapter(listAdapter);



    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView image;
        public ImageView imageAttachment;
        public TextView title;
        public TextView description;
        public TextView creation_time;
        public TextView execution_time;

        public TextView status;
        public TextView notifications;
        public TextView categoryTag;
        public ListView attachments;


        private OnItemClickListener listener;
        public ViewHolder(View itemView,OnItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView);
            imageAttachment = itemView.findViewById(R.id.attachmentsImg);
            title = itemView.findViewById(R.id.title);
          //  description = itemView.findViewById(R.id.description);
          //  creation_time = itemView.findViewById(R.id.creation_time);
            execution_time = itemView.findViewById(R.id.execution_time);
            status = itemView.findViewById(R.id.status);
            notifications = itemView.findViewById(R.id.notifications);
            categoryTag = itemView.findViewById(R.id.categoryTag);
          //  attachments = itemView.findViewById(R.id.attachments);
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
