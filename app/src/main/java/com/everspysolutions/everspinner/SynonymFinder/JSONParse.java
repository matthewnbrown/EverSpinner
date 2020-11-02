package com.everspysolutions.everspinner.SynonymFinder;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParse {

    public JSONParse() {

    }

    /**
     * A JSON parser for the words returned in the data.
     * @param in JSON data returned from the DatamuseQuery class.
     * @return An array of the words.
     */
    public String[] parseWords(String in) {
        String [] words = null;
        try {
            JSONArray array = new JSONArray(in);
            words = new String[array.length()];
            for(int i = 0; i < array.length(); i++) {
                words[i] = array.getJSONObject(i).getString("word");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return words;
    }

    /**
     * A JSON parser for the word scores returned in the data.
     * @param in JSON data returned from the DatamuseQuery class.
     * @return An array of the scores.
     */
    public int[] parseScores(String in) {
        int [] scores = null;
        try {
            JSONArray array = new JSONArray(in);
            scores = new int[array.length()];
            for(int i = 0; i < array.length(); i++) {
                scores[i] = array.getJSONObject(i).getInt("score");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return scores;
    }

    public Synonym[] parseSynonyms(String in) {

        try {
            return parseSynonyms(new JSONArray(in));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Synonym[] parseSynonyms(JSONArray array) {
        Synonym[] synonyms;

        try {
            synonyms = new Synonym[array.length()];
            for(int i = 0; i < array.length(); i++) {
                JSONObject object =  array.getJSONObject(i);
                synonyms[i] = new Synonym(object.getString("word"), object.getInt("score"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            synonyms = new Synonym[0];
        }

        return synonyms;
    }
}
