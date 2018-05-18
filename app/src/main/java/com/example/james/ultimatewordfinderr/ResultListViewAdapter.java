package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by James on 30/11/2015.
 */
public class ResultListViewAdapter extends BaseAdapter {

    private LinkedHashMap<String, Integer> words;
    private ArrayList<Integer> selectedItems;
    private int layout;
    private String[] keys;

    public ResultListViewAdapter(Activity context, LinkedHashMap<String, Integer> words, int layout) {
        this.layout = layout;
        this.words = words;
        this.selectedItems = new ArrayList<>();
        this.keys = words.keySet().toArray(new String[words.size()]);
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public String getItem(int position) {
        return keys[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }

        String word = keys[position];

        if (word != null) {
            TextView textView = (TextView) convertView.findViewById(R.id.textViewItem);

            if (textView != null) {
                textView.setText(word);
            }
        }

        return convertView;
    }

}