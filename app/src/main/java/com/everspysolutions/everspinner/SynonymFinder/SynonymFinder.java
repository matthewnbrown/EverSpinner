package com.everspysolutions.everspinner.SynonymFinder;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
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

    public ArrayList<Synonym> findSynonyms(Context ctx, String word) {

        word = word.toLowerCase();

        if(synonymCacher.cacheContains(word)){
            return synonymCacher.fetchSynonymsFromCache(word);
        }

        DatamuseQuery query = new DatamuseQuery();
        String jsonData = query.findSynonym(word);

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

        if(result.size() > 0) {
            return result.get(random.nextInt(result.size())).getWord();
        }
        return null;
    }

    /**
     * Returns a synonym weighted based on the "score" provided by Datamuse
     * @param word A word or phrase
     * @return A synonym
     */
    // TODO: IMPLEMENT WEIGHTED SYNONYM FINDER
    public String findRandomWeightedSynonym(Context ctx, String word){
        ArrayList<Synonym>  result = findSynonyms(ctx, word);

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
