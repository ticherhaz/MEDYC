package com.ashrof.medyc.enumerator;

public enum Colour {
    RED("#A02424"),
    GREEN("#2E9E1E"),
    BROWN("#F5CBA7"),
    PURPLE("#821A97");

    private final String codeColor;

    Colour(String codeColor) {
        this.codeColor = codeColor;
    }

    public String getCodeColor() {
        return codeColor;
    }
}
