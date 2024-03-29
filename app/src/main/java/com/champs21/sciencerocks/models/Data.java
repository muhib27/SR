package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 25-Apr-16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Data {

    @SerializedName("topics")
    @Expose
    private List<Topic> topics = new ArrayList<Topic>();

    @SerializedName("level")
    @Expose
    private List<Level> level = new ArrayList<Level>();

    @SerializedName("questions")
    @Expose
    private List<Question> questions = new ArrayList<Question>();

    @SerializedName("total")
    @Expose
    private String total;

    @SerializedName("has_next")
    @Expose
    private Boolean hasNext;

    @SerializedName("dailydose")
    @Expose
    private List<Dailydose> dailydose = new ArrayList<Dailydose>();

    @SerializedName("asktheanchor")
    @Expose
    private List<Asktheanchor> asktheanchor = new ArrayList<Asktheanchor>();

    @SerializedName("episodes")
    @Expose
    private List<Episode> episodes = new ArrayList<Episode>();
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("total_mark")
    @Expose
    private String totalMark;
    @SerializedName("total_question")
    @Expose
    private String totalQuestion;
    @SerializedName("list")
    @Expose
    private List<LeaderList> leaderList = new ArrayList<LeaderList>();


    /**
     *
     * @return
     * The topics
     */
    public List<Topic> getTopics() {
        return topics;
    }

    /**
     *
     * @param topics
     * The topics
     */
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    /**
     *
     * @return
     * The level
     */
    public List<Level> getLevel() {
        return level;
    }

    /**
     *
     * @param level
     * The level
     */
    public void setLevel(List<Level> level) {
        this.level = level;
    }


    /**
     *
     * @return
     * The questions
     */
    public List<Question> getQuestions() {
        return questions;
    }

    /**
     *
     * @param questions
     * The questions
     */
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }



    /**
     *
     * @return
     * The total
     */
    public String getTotal() {
        return total;
    }

    /**
     *
     * @param total
     * The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

    /**
     *
     * @return
     * The hasNext
     */
    public Boolean getHasNext() {
        return hasNext;
    }

    /**
     *
     * @param hasNext
     * The has_next
     */
    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    /**
     *
     * @return
     * The dailydose
     */
    public List<Dailydose> getDailydose() {
        return dailydose;
    }

    /**
     *
     * @param dailydose
     * The dailydose
     */
    public void setDailydose(List<Dailydose> dailydose) {
        this.dailydose = dailydose;
    }

    /**
     *
     * @return
     * The asktheanchor
     */
    public List<Asktheanchor> getAsktheanchor() {
        return asktheanchor;
    }

    /**
     *
     * @param asktheanchor
     * The asktheanchor
     */
    public void setAsktheanchor(List<Asktheanchor> asktheanchor) {
        this.asktheanchor = asktheanchor;
    }

    /**
     *
     * @return
     * The episodes
     */
    public List<Episode> getEpisodes() {
        return episodes;
    }

    /**
     *
     * @param episodes
     * The episodes
     */
    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    /**
     *
     * @return
     * The score
     */
    public String getScore() {
        return score;
    }

    /**
     *
     * @param score
     * The score
     */
    public void setScore(String score) {
        this.score = score;
    }

    /**
     *
     * @return
     * The totalMark
     */
    public String getTotalMark() {
        return totalMark;
    }

    /**
     *
     * @param totalMark
     * The total_mark
     */
    public void setTotalMark(String totalMark) {
        this.totalMark = totalMark;
    }

    /**
     *
     * @return
     * The totalQuestion
     */
    public String getTotalQuestion() {
        return totalQuestion;
    }

    /**
     *
     * @param totalQuestion
     * The total_question
     */
    public void setTotalQuestion(String totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    /**
     *
     * @return
     * The list
     */
    public java.util.List<LeaderList> getLeaderList() {
        return leaderList;
    }

    /**
     *
     * @param list
     * The list
     */
    public void setLeaderList(List<LeaderList> list) {
        this.leaderList = list;
    }

}