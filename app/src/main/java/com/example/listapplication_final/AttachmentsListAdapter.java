package com.example.listapplication_final;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class AttachmentsListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> items;
    public AttachmentsListAdapter(Context context,int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout, parent, false);
        }
        ImageView imageView = convertView.findViewById(R.id.attachmentImg);
        imageView.setImageResource( R.drawable.attachment_icon);
        TextView textView = convertView.findViewById(R.id.attachmentName);
        textView.setText(items.get(position));
        return convertView;
    }
}




