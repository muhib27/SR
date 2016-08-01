package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 25-Apr-16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Topic {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("en_name")
    @Expose
    private String enName;
    @SerializedName("details")
    @Expose
    private String details;
    @SerializedName("priority")
    @Expose
    private Object priority;
    @SerializedName("status")
    @Expose
    private Object status;
    @SerializedName("logo")
    @Expose
    private Object logo;

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
     * The enName
     */
    public String getEnName() {
        return enName;
    }

    /**
     *
     * @param enName
     * The en_name
     */
    public void setEnName(String enName) {
        this.enName = enName;
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
     * The logo
     */
    public Object getLogo() {
        return logo;
    }

    /**
     *
     * @param logo
     * The logo
     */
    public void setLogo(Object logo) {
        this.logo = logo;
    }

}