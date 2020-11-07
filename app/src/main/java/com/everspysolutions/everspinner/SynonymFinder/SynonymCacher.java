package com.everspysolutions.everspinner.SynonymFinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SynonymCacher {
    Hashtable<String, ArrayList<Synonym>> cache;
    Date lastUpdateTime;

    public SynonymCacher () {
        cache = new Hashtable<>();
        lastUpdateTime = new Date();
    }

    public SynonymCacher (JSONObject object, Date lastEditTime) {
        cache = new Hashtable<>();
        this.lastUpdateTime = lastEditTime;
        addFromJSONObject(object);
    }

    public void addItemToCache(String word, ArrayList<Synonym> synonyms) {
        lastUpdateTime = new Date();
        cache.put(word, synonyms);
    }

    public ArrayList<Synonym> fetchSynonymsFromCache(String word) {
        if(cache.containsKey(word)) {
            return cache.get(word);
        }
        return null;
    }

    public Hashtable<String, ArrayList<Synonym>> getCache() {
        return cache;
    }

    public List<String> getBaseWords() {
        Set<String> keys = cache.keySet();

        return new ArrayList<>(keys);
    }

    public Boolean cacheContains(String word) {
        return cache.containsKey(word);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();

        try {
            for (String word : cache.keySet()) {
                JSONArray synonyms = new JSONArray();

                for(Synonym synonym : cache.get(word)) {
                    JSONObject syn = new JSONObject();
                    syn.put("word", synonym.getWord());
                    syn.put("score", synonym.getScore());
                    synonyms.put(syn);
                }

                jsonObject.put(word, synonyms);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void addFromJSONObject(JSONObject object) {
        JSONParse parser = new JSONParse();
        Iterator<String> keys = object.keys();
        lastUpdateTime = new Date();
        try {
            while (keys.hasNext()) {
                String key = keys.next();
                if (object.get(key) instanceof JSONArray) {
                    JSONArray obj = (JSONArray) object.get(key);
                    Synonym[] synonymsArray = parser.parseSynonyms(obj);

                    if(synonymsArray != null) {
                        ArrayList<Synonym> synonyms = new ArrayList<>(Arrays.asList(synonymsArray));
                        cache.put(key, synonyms);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }



}
