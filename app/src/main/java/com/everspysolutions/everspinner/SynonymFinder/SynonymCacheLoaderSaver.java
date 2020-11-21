package com.everspysolutions.everspinner.SynonymFinder;


import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class SynonymCacheLoaderSaver {
    private static final String FILENAME = "synonymcache.json";
    private static final String TAG = "SynonymCacheLoader";
    private static FileWriter file;

    public static SynonymCacher loadLocalSynonymCache (Context ctx) {
        File file = new File(ctx.getCacheDir(), FILENAME);

        if(!file.exists()) {
            return null;
        }

        String inputData;
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);

            char[] inputBuffer = new char[fis.available()];
            int res = isr.read(inputBuffer);

            inputData = new String(inputBuffer);

            isr.close();
            fis.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        SynonymCacher synonymCacher;
        try {
            JSONObject jsonObject = new JSONObject(inputData);
            synonymCacher = new SynonymCacher(jsonObject, getLastSaveTime(ctx));
        }  catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return synonymCacher;
    }

    public static Boolean saveLocalSynonymCache(Context ctx, SynonymCacher cache) {
        JSONObject obj = cache.toJSONObject();
        File outputDir = ctx.getCacheDir();
        File outputFile = new File(outputDir, FILENAME);

        try {
            if (!outputDir.exists() && outputDir.mkdir()) {
                Log.d(TAG, "saveLocalSynonymCache: Create cache directory");
            }
            if (!outputFile.exists() && outputFile.createNewFile()) {
                Log.d(TAG, "saveLocalSynonymCache: Created cache file");
            }

            file = new FileWriter(outputFile);
            file.write(obj.toString());
            Log.d(TAG, "saveLocalSynonymCache: Saved cache to JSON file");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try{
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static Date getLastSaveTime(Context ctx) {
        File outputDir = ctx.getCacheDir();
        File file = new File(outputDir, FILENAME);

        return new Date(file.lastModified());
    }

}
