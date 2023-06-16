package com.example.listapplication_final;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final int textSize;

    public MyListAdapter(Context context, int textSize, ArrayList<String> items) {
        super(context, 0, items);
        this.context = context;
        this.textSize = textSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT));
            textView.setPadding(16, 16, 16, 16);
        } else {
            textView = (TextView) convertView;
        }
        textView.setText(getItem(position));
        textView.setTextSize(textSize);
        return textView;
    }
}
