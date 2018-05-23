package com.example.james.ultimatewordfinderr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import nz.co.ninjastudios.datamuseandroid.Word;

/**
 * Created by James on 30/11/2015.
 */
public class SynonymsListViewAdapter extends RecyclerView.Adapter<SynonymsListViewAdapter.ViewHolder> {

    private ArrayList<Word> words;
    private Context context;

    private ResultListViewAdapter.ItemClickListener clickListener;

    SynonymsListViewAdapter(Context context, ArrayList<Word> words) {
        this.context = context;
        this.words = words;
    }

    public int getCount(){
        return words.size();
    }

    public Word getItemAtPosition(int position){
        return words.get(position);
    }

    public void setClickListener(ResultListViewAdapter.ItemClickListener itemClickListener){
        clickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
        boolean onItemLongClick(View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_result_list, parent, false);

        return new SynonymsListViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(getItemAtPosition(position).getWord());
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            this.textView = (TextView) itemView.findViewById(R.id.textViewItem);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            return clickListener != null && clickListener.onItemLongClick(view, getAdapterPosition());

        }
    }

}