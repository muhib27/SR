package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 26-Apr-16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Question {

    @SerializedName("en_question")
    @Expose
    private String enQuestion;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("en_explanation")
    @Expose
    private String enExplanation;
    @SerializedName("explanation")
    @Expose
    private String explanation;
    @SerializedName("mark")
    @Expose
    private String mark;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("style")
    @Expose
    private String style;
    @SerializedName("options")
    @Expose
    private List<Option> options = new ArrayList<Option>();


    /**
     *
     * @return
     * The enQuestion
     */
    public String getEnQuestion() {
        return enQuestion;
    }

    /**
     *
     * @param enQuestion
     * The en_question
     */
    public void setEnQuestion(String enQuestion) {
        this.enQuestion = enQuestion;
    }

    /**
     *
     * @return
     * The question
     */
    public String getQuestion() {
        return question;
    }

    /**
     *
     * @param question
     * The question
     */
    public void setQuestion(String question) {
        this.question = question;
    }


    /**
     *
     * @return
     * The enExplanation
     */
    public String getEnExplanation() {
        return enExplanation;
    }

    /**
     *
     * @param enExplanation
     * The en_explanation
     */
    public void setEnExplanation(String enExplanation) {
        this.enExplanation = enExplanation;
    }

    /**
     *
     * @return
     * The explanation
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     *
     * @param explanation
     * The explanation
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     *
     * @return
     * The mark
     */
    public String getMark() {
        return mark;
    }

    /**
     *
     * @param mark
     * The mark
     */
    public void setMark(String mark) {
        this.mark = mark;
    }

    /**
     *
     * @return
     * The time
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The style
     */
    public String getStyle() {
        return style;
    }

    /**
     *
     * @param style
     * The style
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     *
     * @return
     * The options
     */
    public List<Option> getOptions() {
        return options;
    }

    /**
     *
     * @param options
     * The options
     */
    public void setOptions(List<Option> options) {
        this.options = options;
    }

}