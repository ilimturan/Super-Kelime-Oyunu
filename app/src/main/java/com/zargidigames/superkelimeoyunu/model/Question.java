package com.zargidigames.superkelimeoyunu.model;

/**
 * Created by ilimturan on 08/11/15.
 */
public class Question {

    public int id;
    public String word;
    public int letter_count;
    public int word_score;
    public String description;

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", word='" + word + '\'' +
                ", letter_count=" + letter_count +
                ", word_score=" + word_score +
                ", description='" + description + '\'' +
                '}';
    }
}
