package com.example.james.ultimatewordfinderr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private int numOfTabs;
    private String word;

    PagerAdapter(FragmentManager fragmentManager, int numOfTabs, String word) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
        this.word = word;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("Word", this.word);
        switch (position) {
            case 0:
                WordDetailsDefinitionsTabFragment wordDetailsDefinitionsTabFragment = new WordDetailsDefinitionsTabFragment();
                wordDetailsDefinitionsTabFragment.setArguments(bundle);
                return wordDetailsDefinitionsTabFragment;
            case 1:
                WordDetailsSynonymsTabFragment wordDetailsSynonymsTabFragment = new WordDetailsSynonymsTabFragment();
                wordDetailsSynonymsTabFragment.setArguments(bundle);
                return wordDetailsSynonymsTabFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
