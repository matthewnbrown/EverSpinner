package com.everspysolutions.everspinner.SynonymFinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class SynonymFinder {

    private SynonymCacher synonymCacher;
    private JSONParse jsonParse = new JSONParse();
    private Random random = new Random();
    private final int maxSynonyms = 10;

    public SynonymFinder(){
        this.synonymCacher = new SynonymCacher();
    }

    public SynonymFinder(SynonymCacher cacher){
        this.synonymCacher = cacher;
    }

    public ArrayList<Synonym> findSynonyms(Context ctx, String word) {

        word = word.toLowerCase();

        if(synonymCacher.cacheContains(word)){
            return synonymCacher.fetchSynonymsFromCache(word);
        }

        DatamuseQuery query = new DatamuseQuery();
        String jsonData = query.findSynonym(word, maxSynonyms);

        Synonym[] synonymsArray  = jsonParse.parseSynonyms(jsonData);
        ArrayList<Synonym> synonyms = new ArrayList<>(Arrays.asList(synonymsArray));
        synonymCacher.addItemToCache(word, synonyms);


        return synonyms;
    }

    /**
     * Returns a random synonym
     * @param word A word or phrase
     * @return A random synonym
     */
    public String findRandomSynonym(Context ctx, String word) {
        ArrayList<Synonym>  result = findSynonyms(ctx, word);

        if(!result.isEmpty()) {
            return result.get(random.nextInt(result.size())).getWord();
        }
        return null;
    }

    /**
     * Returns a synonym weighted based on the "score" provided by Datamuse
     * @param word A word or phrase
     * @return A synonym
     */
    public String findRandomWeightedSynonym(Context ctx, String word){
        ArrayList<Synonym>  result = findSynonyms(ctx, word);

        int scoreSum = 1;
        for(Synonym syn : result){
            scoreSum += syn.getScore();
        }

        if (scoreSum <= 0) {
            return null;
        }

        int cutOff = random.nextInt(scoreSum);

        for(Synonym syn : result){
            cutOff -= syn.getScore();

            if(cutOff <= 0){
                return syn.getWord();
            }
        }

        return null;
    }

    public SynonymCacher getSynonymCacher(){
        return synonymCacher;
    }

    public void setSynonymCacher(SynonymCacher cacher) {
        synonymCacher = cacher;
    }
}
