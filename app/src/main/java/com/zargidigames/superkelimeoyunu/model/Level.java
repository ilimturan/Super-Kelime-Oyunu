package com.zargidigames.superkelimeoyunu.model;

/**
 * Created by ilimturan on 16/11/15.
 */
public class Level {

    public int id;
    public String name;
    public String language;

    @Override
    public String toString() {
        return "Level{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
