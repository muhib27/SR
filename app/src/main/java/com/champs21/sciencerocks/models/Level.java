package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 26-Apr-16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Level {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("priority")
    @Expose
    private Object priority;
    @SerializedName("mark")
    @Expose
    private Object mark;
    @SerializedName("time")
    @Expose
    private Object time;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("category_id")
    @Expose
    private Object categoryId;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("total_played")
    @Expose
    private Object totalPlayed;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
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
     * The details
     */
    public String getDetails() {
        return details;
    }

    /**
     *
     * @param details
     * The details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     *
     * @return
     * The priority
     */
    public Object getPriority() {
        return priority;
    }

    /**
     *
     * @param priority
     * The priority
     */
    public void setPriority(Object priority) {
        this.priority = priority;
    }

    /**
     *
     * @return
     * The mark
     */
    public Object getMark() {
        return mark;
    }

    /**
     *
     * @param mark
     * The mark
     */
    public void setMark(Object mark) {
        this.mark = mark;
    }

    /**
     *
     * @return
     * The time
     */
    public Object getTime() {
        return time;
    }

    /**
     *
     * @param time
     * The time
     */
    public void setTime(Object time) {
        this.time = time;
    }

    /**
     *
     * @return
     * The status
     */
    public Object getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Object status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The categoryId
     */
    public Object getCategoryId() {
        return categoryId;
    }

    /**
     *
     * @param categoryId
     * The category_id
     */
    public void setCategoryId(Object categoryId) {
        this.categoryId = categoryId;
    }

    /**
     *
     * @return
     * The image
     */
    public Object getImage() {
        return image;
    }

    /**
     *
     * @param image
     * The image
     */
    public void setImage(Object image) {
        this.image = image;
    }

    /**
     *
     * @return
     * The totalPlayed
     */
    public Object getTotalPlayed() {
        return totalPlayed;
    }

    /**
     *
     * @param totalPlayed
     * The total_played
     */
    public void setTotalPlayed(Object totalPlayed) {
        this.totalPlayed = totalPlayed;
    }

}