package de.effnerapp.effner.data.model;

import android.graphics.Color;

import de.effnerapp.effner.data.model.color.ColorParser;

public class AppColor {
    public static final AppColor WHITE = new AppColor("HEX", "#FFFFFF");
    private String id, name, type, color;

    public AppColor(String type, String color) {
        this.type = type;
        this.color = color;
    }

    public AppColor() {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getColor() {
        return color;
    }

    public int getColorValue() {
        int colorValue = 0;
        switch (type) {
            case "HEX":
                colorValue = Color.parseColor(color);
                break;
            case "RGBA":
                colorValue = new ColorParser().parseRGBAColor(color).getColorValue();
                break;
        }
        return colorValue;
    }
}
