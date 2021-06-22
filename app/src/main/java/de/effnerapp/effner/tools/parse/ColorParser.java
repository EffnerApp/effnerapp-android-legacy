/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 19:17.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.parse;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.effnerapp.effner.data.api.json.data.color.RGBAColor;

public class ColorParser {
    private final Gson gson;

    public ColorParser() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public RGBAColor parseRGBAColor(String json) {
        return gson.fromJson(json, RGBAColor.class);
    }
}
