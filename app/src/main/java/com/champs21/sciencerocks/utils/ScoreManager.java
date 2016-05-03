package com.champs21.sciencerocks.utils;

import java.util.List;

/**
 * Created by BLACK HAT on 27-Apr-16.
 */
public class ScoreManager {

    private int totalQuestion;
    private List<Boolean> listCorrect;
    private int score;
    private long time;
    private int totalScore;


    public ScoreManager(int totalQuestion, List<Boolean> listCorrect, int score, long time, int totalScore) {
        this.totalQuestion = totalQuestion;
        this.listCorrect = listCorrect;
        this.score = score;
        this.time = time;
        this.totalScore = totalScore;

    }

    public int getTotalQuestion() {
        return totalQuestion;
    }

    public void setTotalQuestion(int totalQuestion) {
        this.totalQuestion = totalQuestion;
    }

    public List<Boolean> getListCorrect() {
        return listCorrect;
    }

    public void setListCorrect(List<Boolean> listCorrect) {
        this.listCorrect = listCorrect;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
}
