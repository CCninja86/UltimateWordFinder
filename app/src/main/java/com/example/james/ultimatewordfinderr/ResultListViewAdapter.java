package com.example.james.ultimatewordfinderr;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by James on 30/11/2015.
 */
public class ResultListViewAdapter extends RecyclerView.Adapter<ResultListViewAdapter.ViewHolder> {

    private Context context;
    private LinkedHashMap<String, Integer> words;
    private HashSet<Integer> mSelected;

    private ItemClickListener clickListener;

    public ResultListViewAdapter(Context context, LinkedHashMap<String, Integer> words) {
        this.context = context;
        this.words = words;
        mSelected = new HashSet<>();
    }

    public Map.Entry getItemAtPosition(int position){
        Map.Entry entryToReturn = null;
        int index = 0;

        for (Map.Entry entry : words.entrySet()){
            if (index == position){
                entryToReturn = entry;
                break;
            }

            index++;
        }

        return entryToReturn;
    }

    public void toggleSelection(int position){
        if(mSelected.contains(position)){
            mSelected.add(position);
        } else {
            mSelected.remove(position);
        }

        notifyItemChanged(position);
    }

    public void select(int position, boolean selected){
        if(selected){
            mSelected.add(position);
        } else {
            mSelected.remove(position);
        }

        notifyItemChanged(position);
    }

    public void selectRange(int start, int end, boolean selected){
        for(int i = start; i <= end; i++){
            if(selected){
                mSelected.add(i);
            } else {
                mSelected.remove(i);
            }
        }

        notifyItemRangeChanged(start, end - start + 1);
    }

    public void deselectAll(){
        mSelected.clear();
        notifyDataSetChanged();
    }

    public void selectAll(){
        for (int i = 0; i < words.size(); i++){
            mSelected.add(i);
        }

        notifyDataSetChanged();
    }

    public HashSet<Integer> getSelection() {
        return mSelected;
    }

    public void setClickListener(ItemClickListener itemClickListener){
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

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        viewHolder.textView.setText((String) getItemAtPosition(position).getKey());

        if (mSelected.contains(position)){
            viewHolder.textView.setBackgroundColor(Color.RED);
        } else {
            viewHolder.textView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public TextView textView;

        public ViewHolder(View itemView) {
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
            if(clickListener != null){
                return clickListener.onItemLongClick(view, getAdapterPosition());
            }

            return false;
        }
    }
}