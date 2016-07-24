package com.champs21.sciencerocks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by user on 7/25/2016.
 */
public class LeaderList {

    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;

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
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
