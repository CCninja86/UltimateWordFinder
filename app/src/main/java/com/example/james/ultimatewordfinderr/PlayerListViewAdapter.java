package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Connection;

import java.util.ArrayList;

/**
 * Created by James on 30/11/2015.
 */
public class PlayerListViewAdapter extends ArrayAdapter {

    private ArrayList<Player> players;
    private  ArrayList<Integer> selectedItems;
    private int layout;

    public PlayerListViewAdapter(Activity context, ArrayList<Player> players, int layout){
        super(context, layout, players);
        this.layout = layout;
        this.players = players;
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
        return players.size();
    }

    @Override
    public Player getItem(int position){
        return players.get(position);
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

        Player player = players.get(position);

        if(player != null){
            TextView textView = (TextView) convertView.findViewById(R.id.textViewItem);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewPlayerColour);

            if(textView != null){
                textView.setText(player.getName());
            }

            if(imageView != null){
                imageView.setBackgroundColor(player.getColour());
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
