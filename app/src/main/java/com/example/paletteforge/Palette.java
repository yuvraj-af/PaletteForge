package com.example.paletteforge;

import java.util.List;

public class Palette {
    private String id;
    private String label;
    private List<Integer> colors;

    public Palette() {
        // Default constructor
    }

    public Palette(String id, String label, List<Integer> colors) {
        this.id = id;
        this.label = label;
        this.colors = colors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }
}
