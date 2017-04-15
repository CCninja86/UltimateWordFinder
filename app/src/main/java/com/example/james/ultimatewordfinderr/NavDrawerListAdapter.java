package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by james on 15/04/2017.
 */

public class NavDrawerListAdapter extends ArrayAdapter<String> {

    private String[] items;
    private  ArrayList<Integer> selectedItems;
    private int layout;

    public NavDrawerListAdapter(Activity context, String[] items, int layout){
        super(context, layout, items);
        this.layout = layout;
        this.items = items;
        this.selectedItems = new ArrayList<>();
    }

    public ArrayList<Integer> getSelectedItems(){
        return  this.selectedItems;
    }

    public void toggleSelected(Integer position){
        if(selectedItems.contains(position)){
            selectedItems.remove(position);
        } else {
            selectedItems.add(position);
        }
    }

    @Override
    public int getCount(){
        return items.length;
    }

    @Override
    public String getItem(int position){
        return items[position];
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
        }

        String word = items[position];

        if(word != null){
            TextView textView = (TextView) convertView.findViewById(R.id.textViewItem);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewIcon);

            if(textView != null){
                textView.setText(word);
            }

            if(imageView != null){
                switch (textView.getText().toString().toLowerCase()){
                    case "players":
                        imageView.setImageResource(R.drawable.player);
                        break;
                    case "scores":
                        imageView.setImageResource(R.drawable.numbered_list);
                        break;
                    case "tile breakdown":
                        imageView.setImageResource(R.drawable.scrabble_letter);
                        break;
                    case "word finder":
                        imageView.setImageResource(R.drawable.magnifier_tool);
                        break;
                    case "dictionary":
                        imageView.setImageResource(R.drawable.dictionary);
                        break;

                }
            }
        }

        if(selectedItems.contains(position)){
            convertView.setSelected(true);
            convertView.setPressed(true);
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setSelected(false);
            convertView.setPressed(false);
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return  convertView;
    }
}
