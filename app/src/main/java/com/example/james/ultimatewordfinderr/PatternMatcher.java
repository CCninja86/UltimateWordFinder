package com.example.james.ultimatewordfinderr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by james on 17/03/2018.
 */

public class PatternMatcher {

    private static final String REGEX_LETTER_WILDCARD = "[a-z]";
    private Dictionary dictionary;

    private Globals g;

    private PatternMatcherResultsListener patternMatcherResultsListener;

    public PatternMatcher(PatternMatcherResultsListener patternMatcherResultsListener){
        this.g = Globals.getInstance();
        this.dictionary = g.getDictionary();
        this.patternMatcherResultsListener = patternMatcherResultsListener;
    }

    public void matchWithPlayerPattern(ArrayList<Word> wordsToMatch, String playerPattern, String boardPattern){
        new MatchWithPlayerPatternTask(wordsToMatch, playerPattern, boardPattern).execute();
    }

    public String generateBoardRegex(String boardPattern){
        return boardPattern.replaceAll("\\?", REGEX_LETTER_WILDCARD);
    }

    public void getAllWordsMatchingRegex(String regex){
        new GetAllWordsMatchingRegexTask(regex).execute();
    }

    private Map<String, Integer> generateLetterMap(String pattern){
        Map<String, Integer> letterMap = new HashMap<>();

        for(int i = 0; i < pattern.length(); i++){
            String letter = String.valueOf(pattern.charAt(i));
            int letterCount = countLetters(pattern, letter);

            letterMap.put(letter, letterCount);
        }

        return letterMap;
    }

    private int countLetters(String string, String letter){
        int letterCount = 0;

        for(int i = 0; i < string.length(); i++){
            if(String.valueOf(string.charAt(i)).equalsIgnoreCase(letter)){
                letterCount++;
            }
        }

        return letterCount;
    }

    private int countLetterMapTotal(Map<String, Integer> letterMap){
        int total = 0;

        for(int value : letterMap.values()){
            total += value;
        }

        return total;
    }

    private class GetAllWordsMatchingRegexTask extends AsyncTask<Void, Void, Void> {

        private String regex;
        private ArrayList<Word> matches;

        public GetAllWordsMatchingRegexTask(String regex){
            this.regex = regex;
            this.matches = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Pattern pattern = Pattern.compile(regex);

            for(Word word : dictionary.getWordList()){
                Matcher matcher = pattern.matcher(word.getWord());

                if(matcher.matches()){
                    matches.add(word);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            patternMatcherResultsListener.onPatternMatcherGetAllWordsMatchingRegexTaskComplete(matches);
        }
    }

    private class MatchWithPlayerPatternTask extends AsyncTask<Void, Void, Void> {

        private String playerPattern;
        private ArrayList<Word> matches;
        private ArrayList<Word> wordsToMatch;

        public MatchWithPlayerPatternTask(ArrayList<Word> wordsToMatch, String playerPattern, String boardPattern){
            this.playerPattern = playerPattern;
            this.matches = new ArrayList<>();
            this.wordsToMatch = wordsToMatch;

            for(int i = 0; i < boardPattern.length(); i++){
                String character = String.valueOf(boardPattern.charAt(i));

                if(!character.equals("?")){
                    this.playerPattern += character;
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Find words in list that can be made with player's tiles
            for(Word word : wordsToMatch){
                Map<String, Integer> playerLetterMap = generateLetterMap(playerPattern);
                Map<String, Integer> wordLetterMap = generateLetterMap(word.getWord());
                int numCountMatches = 0;

                for(Map.Entry<String, Integer> entry : wordLetterMap.entrySet()){
                    String wordLetter = entry.getKey();
                    int wordLetterCount = entry.getValue();
                    int playerLetterCount = 0;

                    if(playerLetterMap.containsKey(wordLetter)){
                        playerLetterCount = playerLetterMap.get(wordLetter);
                    }

                    if(playerLetterMap.containsKey("?")){
                        playerLetterCount += playerLetterMap.get("?");
                    }

                    if(playerLetterCount >= wordLetterCount){
                        numCountMatches += wordLetterCount;

                        for(int i = wordLetterCount; i > 0; i--){
                            if(playerLetterMap.containsKey(wordLetter)){
                                if(playerLetterMap.get(wordLetter) > 0){
                                    playerLetterMap.put(wordLetter, playerLetterMap.get(wordLetter) - 1);
                                } else {
                                    if(playerLetterMap.containsKey("?")){
                                        if(playerLetterMap.get("?") > 0){
                                            playerLetterMap.put("?", playerLetterMap.get("?") - 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(numCountMatches == countLetterMapTotal(wordLetterMap)){
                    matches.add(word);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            patternMatcherResultsListener.onPatternMatcherMatchWithPlayerPatternTaskComplete(matches);
        }
    }
}
