package com.example.james.ultimatewordfinderr;

import android.view.View;

import java.util.ArrayList;

/**
 * Created by james on 17/03/2018.
 */

public interface PatternMatcherResultsListener {
    public void onPatternMatcherGetAllWordsMatchingRegexTaskComplete(ArrayList<Word> matches);

    public void onPatternMatcherMatchWithPlayerPatternTaskComplete(ArrayList<Word> matches);
}
