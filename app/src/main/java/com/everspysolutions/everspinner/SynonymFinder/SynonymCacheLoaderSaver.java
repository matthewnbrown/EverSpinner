package com.everspysolutions.everspinner.SynonymFinder;


import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class SynonymCacheLoaderSaver {
    private static final String FILENAME = "synonymcache.json";
    private static final String TAG = "SynonymCacheLoader";
    private static FileWriter file;

    public static SynonymCacher loadLocalSynonymCache (Context ctx) {
        return new SynonymCacher();
    }

    public static void saveLocalSynonymCache(Context ctx, SynonymCacher cache) {
        JSONObject obj = cache.toJSONObject();
        File outputDir = ctx.getFilesDir();
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
        } finally {
            try{
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Date getLastSaveTime(Context ctx) {
        File outputDir = ctx.getFilesDir();
        File file = new File(outputDir, FILENAME);

        return new Date(file.lastModified());

    }

}
