package com.zargidigames.superkelimeoyunu.model;

/**
 * Created by ilimturan on 14/11/15.
 */
public class OptionLetter {

    public String letter;
    public Boolean isOpen;

    public OptionLetter() {
        letter = "";
        isOpen = false;
    }

    @Override
    public String toString() {
        return "OptionLetter{" +
                "letter='" + letter + '\'' +
                ", isOpen=" + isOpen +
                '}';
    }
}
