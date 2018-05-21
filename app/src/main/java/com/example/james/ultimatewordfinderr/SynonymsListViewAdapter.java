package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nz.co.ninjastudios.datamuseandroid.Word;

/**
 * Created by James on 30/11/2015.
 */
public class SynonymsListViewAdapter extends ArrayAdapter<Word> {

    private ArrayList<Word> words;
    private ArrayList<Integer> selectedItems;
    private int layout;

    public SynonymsListViewAdapter(Activity context, ArrayList<Word> words, int layout) {
        super(context, layout, words);
        this.layout = layout;
        this.words = words;
        this.selectedItems = new ArrayList<>();
    }

    public ArrayList<Integer> getSelectedItems() {
        return this.selectedItems;
    }

    public void toggleSelected(Integer position) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
    }

    @Override
    public int getCount() {
        return words.size();
    }

    @Override
    public Word getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }

        String word = words.get(position).getWord();

        if (word != null) {
            TextView textView = (TextView) convertView.findViewById(R.id.textViewItem);

            if (textView != null) {
                textView.setText(word);
            }
        }

        if (selectedItems.contains(position)) {
            convertView.setSelected(true);
            convertView.setPressed(true);
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setSelected(false);
            convertView.setPressed(false);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

}