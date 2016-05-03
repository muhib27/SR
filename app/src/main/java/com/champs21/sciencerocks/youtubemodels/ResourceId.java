package com.champs21.sciencerocks.youtubemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by BLACK HAT on 13-Apr-16.
 */
public class ResourceId {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("videoId")
    @Expose
    private String videoId;

    /**
     *
     * @return
     * The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     *
     * @param kind
     * The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     *
     * @return
     * The videoId
     */
    public String getVideoId() {
        return videoId;
    }

    /**
     *
     * @param videoId
     * The videoId
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
