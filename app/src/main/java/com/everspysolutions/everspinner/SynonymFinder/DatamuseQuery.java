package com.everspysolutions.everspinner.SynonymFinder;

/*
 * Copyright © 2015 S.J. Blair <https://github.com/sjblair>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A handler for making calls to the Datamuse RESTful API.
 *
 * @author sjblair
 * @since 21/02/15
 */
public class DatamuseQuery {

    public DatamuseQuery() {

    }

    /**
     * Returns a list of synonyms to the word/phrase supplied.
     * @param word A word of phrase.
     * @return A list of synonyms
     */

    public String findSynonym(String word) {
        String s = word.replaceAll(" ", "+");
        return getJSON("https://api.datamuse.com/words?rel_syn="+s);
    }

    public String findSynonym(String word, int max) {
        String s = word.replaceAll(" ", "+");
        return getJSON("https://api.datamuse.com/words?rel_syn="+s+"&max=" + max);
    }

    /**
     * Query a URL for their source code.
     * @param url The page's URL.
     * @return The source code.
     */
    private String getJSON(String url) {
        URL datamuse;
        URLConnection dc;
        StringBuilder s = null;
        try {
            datamuse = new URL(url);
            dc = datamuse.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(dc.getInputStream(), "UTF-8"));
            String inputLine;
            s = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                s.append(inputLine);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s != null ? s.toString() : null;
    }
}