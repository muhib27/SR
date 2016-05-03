package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 26-Apr-16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option {

    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("correct")
    @Expose
    private String correct;

    /**
     *
     * @return
     * The answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     *
     * @param answer
     * The answer
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     *
     * @return
     * The correct
     */
    public String getCorrect() {
        return correct;
    }

    /**
     *
     * @param correct
     * The correct
     */
    public void setCorrect(String correct) {
        this.correct = correct;
    }

}