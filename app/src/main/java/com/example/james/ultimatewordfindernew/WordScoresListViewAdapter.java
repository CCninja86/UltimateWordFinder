package com.example.james.ultimatewordfindernew;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by james on 18/02/2017.
 */

public class WordScoresListViewAdapter extends ArrayAdapter<String> {

    private ArrayList<String> words;
    private ArrayList<Integer> selectedItems;
    private int layout;
    private View.OnClickListener letterOnClickListener;
    private View.OnClickListener btnOnClickListener;
    private static int MAX_ROWS = 11;

    public WordScoresListViewAdapter(Activity context, ArrayList<String> words, int layout){
        super(context, layout, words);
        this.words = words;
        this.layout = layout;

        this.letterOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view != null){
                    int colour = 0;
                    Drawable background = view.getBackground();

                    if(background instanceof ColorDrawable){
                        colour = ((ColorDrawable) background).getColor();
                        String hexColor = String.format("#%06X", (0xFFFFFF & colour));
                        TextView textView = (TextView) view;
                        String currentText = textView.getText().toString();
                        String newText = "";

                        switch (hexColor){
                            case "#C3BDA5":
                                view.setBackgroundColor(Color.argb(255, 197, 222, 210));
                                newText = currentText + " x2";
                                textView.setText(newText);
                                break;
                            case "#C5DED2":
                                view.setBackgroundColor(Color.argb(255, 43, 140, 174));
                                newText = currentText.substring(0, currentText.indexOf("x") - 1) + " x3";
                                textView.setText(newText);
                                break;
                            case "#2B8CAE":
                                view.setBackgroundColor(Color.argb(255, 195, 189, 165));
                                textView.setText(currentText.substring(0, currentText.indexOf("x") - 1));
                                break;
                        }
                    }
                }
            }
        };

        this.btnOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view != null){
                    Button button = (Button) view;

                    switch (button.getText().toString()){
                        case "No Word Bonus":
                            button.setBackgroundColor(Color.argb(255, 249, 189, 178));
                            button.setText("Double Word");
                            break;
                        case "Double Word":
                            button.setBackgroundColor(Color.argb(255, 236, 92, 79));
                            button.setText("Triple Word");
                            break;
                        case "Triple Word":
                            button.setBackgroundColor(Color.argb(255, 195, 189, 165));
                            button.setText("No Word Bonus");
                            break;
                    }
                }
            }
        };
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
        if(words != null){
            return  Math.min(words.size(), MAX_ROWS);
        } else {
            return 0;
        }
    }

    @Override
    public String getItem(int position){
        return words.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);

            String word = words.get(position);

            if(word != null){
                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.wordScoreLayout);

                String[] letters = word.split("");

                for(String letter : letters){
                    if(!letter.equals("")){
                        TextView letterTextView = new TextView(getContext());
                        letterTextView.setText(letter.toUpperCase());
                        letterTextView.setBackgroundColor(Color.argb(255, 195, 189, 165));
                        letterTextView.setTextSize(24);
                        letterTextView.setWidth(200);
                        letterTextView.setHeight(100);
                        letterTextView.setGravity(Gravity.CENTER_HORIZONTAL);
                        letterTextView.setOnClickListener(this.letterOnClickListener);




                        textViewLayoutParams.setMargins(0, 0, 10, 0);
                        linearLayout.addView(letterTextView, textViewLayoutParams);

                    }
                }

                Button wordBonusButton = new Button(getContext());
                wordBonusButton.setText("No Word Bonus");
                wordBonusButton.setBackgroundColor(Color.argb(255, 195, 189, 165));
                wordBonusButton.setOnClickListener(this.btnOnClickListener);


                buttonLayoutParams.setMargins(100, 0, 0, 0);
                linearLayout.addView(wordBonusButton, buttonLayoutParams);
            }


        }



        return  convertView;
    }

}
