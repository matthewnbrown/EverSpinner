package com.everspysolutions.everspinner.SynonymFinder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SynonymCacher implements Serializable {
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
        sortSynonyms(synonyms);
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

    public void addFromJSONObject(@NotNull JSONObject object) {
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

    public void removeBaseWord(String baseWord) {
        cache.remove(baseWord);
    }

    public void removeSynonym(String baseword, String synonymWord) {
        removeSynonym(baseword, findSynonymIndex(baseword, synonymWord));
    }

    public void removeSynonym(String baseword, int synonymIndex) {
        ArrayList<Synonym> synonyms = cache.get(baseword);
        synonyms.remove(synonymIndex);
        sortSynonyms(synonyms);
    }

    private int findSynonymIndex(String baseWord, String synonym) {
        ArrayList<Synonym> synonyms = cache.get(baseWord);

        for(int i = 0; i < synonyms.size(); i++) {
            if(synonyms.get(i).getWord().equals(synonym)) {
                return i;
            }
        }

        return -1;
    }

    public void updateSynonymScore(String baseWord, String synonymWord, int newScore) {
        updateSynonymScore(baseWord, findSynonymIndex(baseWord, synonymWord), newScore);
    }

    public void updateSynonymScore(String baseWord, int synonymIndex, int newScore) {
        ArrayList<Synonym> synonyms = cache.get(baseWord);
        synonyms.get(synonymIndex).setScore(newScore);
        sortSynonyms(synonyms);
    }

    public void addSynonymToBaseWord(String baseWord, String synonymWord, int synonymScore) {
        Synonym synonym = new Synonym(synonymWord, synonymScore);
        addSynonymToBaseWord(baseWord, synonym);
    }

    public void addSynonymToBaseWord(String baseWord, Synonym synonym) {
        ArrayList<Synonym> synonyms = cache.get(baseWord);
        int index = synonyms.size();

        boolean repeated = false;

        for(int i = 0; i < synonyms.size(); i++) {
            if (synonym.getScore() > synonyms.get(i).getScore()) {
                index = i;
                break;
            }
//            if (synonyms.get(i).getWord().equals(synonym.getWord())) {
//                repeated = true;
//                break;
//            }
        }

        synonyms.add(index, synonym);
        sortSynonyms(synonyms);
    }

    public void sortSynonyms(ArrayList<Synonym> synonyms) {
        Collections.sort(synonyms, (synonym, t1) -> t1.getScore() - synonym.getScore());
    }

}
