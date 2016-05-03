package com.champs21.sciencerocks.youtubemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by BLACK HAT on 13-Apr-16.
 */
public class Snippet {
    @SerializedName("publishedAt")
    @Expose
    private String publishedAt;
    @SerializedName("channelId")
    @Expose
    private String channelId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnails")
    @Expose
    private Thumbnails thumbnails;
    @SerializedName("channelTitle")
    @Expose
    private String channelTitle;
    @SerializedName("localized")
    @Expose
    private Localized localized;
    @SerializedName("playlistId")
    @Expose
    private String playlistId;
    @SerializedName("position")
    @Expose
    private Integer position;
    @SerializedName("resourceId")
    @Expose
    private ResourceId resourceId;

    /**
     *
     * @return
     * The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     *
     * @param publishedAt
     * The publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     *
     * @return
     * The channelId
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     *
     * @param channelId
     * The channelId
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The thumbnails
     */
    public Thumbnails getThumbnails() {
        return thumbnails;
    }

    /**
     *
     * @param thumbnails
     * The thumbnails
     */
    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     *
     * @return
     * The channelTitle
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     *
     * @param channelTitle
     * The channelTitle
     */
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    /**
     *
     * @return
     * The localized
     */
    public Localized getLocalized() {
        return localized;
    }

    /**
     *
     * @param localized
     * The localized
     */
    public void setLocalized(Localized localized) {
        this.localized = localized;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }
}
