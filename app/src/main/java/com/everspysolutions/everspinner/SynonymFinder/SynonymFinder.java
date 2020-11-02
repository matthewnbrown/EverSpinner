package com.everspysolutions.everspinner.SynonymFinder;

import android.content.Context;

import java.util.Random;


public class SynonymFinder {

    private SynonymCacher synonymCacher;
    private JSONParse jsonParse = new JSONParse();
    private Random random = new Random();
    public SynonymFinder(){
        this.synonymCacher = new SynonymCacher();
    }

    public SynonymFinder(SynonymCacher cacher){
        this.synonymCacher = cacher;
    }

    public Synonym[] findSynonyms(Context ctx, String word) {

        word = word.toLowerCase();

        if(synonymCacher.cacheContains(word)){
            return synonymCacher.fetchSynonymsFromCache(word);
        }

        DatamuseQuery query = new DatamuseQuery();
        String jsonData = query.findSynonym(word);

        Synonym[] synonyms  = jsonParse.parseSynonyms(jsonData);
        synonymCacher.addItemToCache(word, synonyms);


        return synonyms;
    }

    /**
     * Returns a random synonym
     * @param word A word or phrase
     * @return A random synonym
     */
    public String findRandomSynonym(Context ctx, String word) {
        Synonym[] result = findSynonyms(ctx, word);

        if(result.length > 0) {
            return result[random.nextInt(result.length)].getWord();
        }
        return null;
    }

    /**
     * Returns a synonym weighted based on the "score" provided by Datamuse
     * @param word A word or phrase
     * @return A synonym
     */
    public String findRandomWeightedSynonym(Context ctx, String word){
        Synonym[] result = findSynonyms(ctx, word);

        int scoreSum = 0;
        for(Synonym syn : result){
            scoreSum += syn.getScore();
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
