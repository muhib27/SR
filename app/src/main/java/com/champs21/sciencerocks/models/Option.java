package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 26-Apr-16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option {


    @SerializedName("en_answer")
    @Expose
    private String enAnswer;
    @SerializedName("answer")
    @Expose
    private String answer;
    @SerializedName("correct")
    @Expose
    private String correct;


    /**
     *
     * @return
     * The enAnswer
     */
    public String getEnAnswer() {
        return enAnswer;
    }

    /**
     *
     * @param enAnswer
     * The en_answer
     */
    public void setEnAnswer(String enAnswer) {
        this.enAnswer = enAnswer;
    }

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