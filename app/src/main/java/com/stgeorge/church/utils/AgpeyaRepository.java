package com.stgeorge.church.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.stgeorge.church.models.AgpeyaHour;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Loads the eight Agpeya (كتاب الأجبية / السواعي) prayer hours from the JSON
 * files bundled at assets/agpeya/. Each hour is stored as its own JSON file
 * (assets/agpeya/<id>.json) plus an assets/agpeya/index.json that lists them
 * in display order.
 *
 * Content source: St-Takla.org — transcribed prayer-hour texts.
 */
public class AgpeyaRepository {

    private static final String ASSETS_DIR = "agpeya";
    private static final String INDEX_FILE = ASSETS_DIR + "/index.json";

    private final AssetManager assetManager;

    public AgpeyaRepository(Context context) {
        this.assetManager = context.getApplicationContext().getAssets();
    }

    /** Returns all eight hours, sorted by their canonical order (1..8). */
    public List<AgpeyaHour> getAllHours() {
        List<AgpeyaHour> hours = new ArrayList<>();
        try {
            JSONArray index = new JSONArray(readAsset(INDEX_FILE));
            for (int i = 0; i < index.length(); i++) {
                JSONObject entry = index.getJSONObject(i);
                String file = entry.getString("file");
                AgpeyaHour hour = loadHour(ASSETS_DIR + "/" + file);
                if (hour != null) {
                    hours.add(hour);
                }
            }
        } catch (IOException | org.json.JSONException e) {
            // If assets are missing/corrupted, just return whatever loaded so far.
        }

        Collections.sort(hours, Comparator.comparingInt(AgpeyaHour::getOrder));
        return hours;
    }

    /** Loads a single hour by its id (e.g. "prime", "midnight"). */
    public AgpeyaHour getHourById(String id) {
        try {
            return loadHour(ASSETS_DIR + "/" + id + ".json");
        } catch (IOException e) {
            return null;
        }
    }

    private AgpeyaHour loadHour(String assetPath) throws IOException {
        String json = readAsset(assetPath);
        try {
            JSONObject obj = new JSONObject(json);
            return new AgpeyaHour(
                    obj.optString("id"),
                    obj.optInt("order"),
                    obj.optString("title"),
                    obj.optString("content"),
                    obj.optString("source")
            );
        } catch (org.json.JSONException e) {
            return null;
        }
    }

    private String readAsset(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (InputStream is = assetManager.open(path);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }
}
