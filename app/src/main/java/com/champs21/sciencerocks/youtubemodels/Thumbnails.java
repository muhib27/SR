package com.champs21.sciencerocks.youtubemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by BLACK HAT on 13-Apr-16.
 */
public class Thumbnails {
    @SerializedName("default")
    @Expose
    private Default _default;
    @SerializedName("medium")
    @Expose
    private Medium medium;
    @SerializedName("high")
    @Expose
    private High high;
    @SerializedName("maxres")
    @Expose
    private Maxres maxres;

    /**
     *
     * @return
     * The _default
     */
    public Default getDefault() {
        return _default;
    }

    /**
     *
     * @param _default
     * The default
     */
    public void setDefault(Default _default) {
        this._default = _default;
    }

    /**
     *
     * @return
     * The medium
     */
    public Medium getMedium() {
        return medium;
    }

    /**
     *
     * @param medium
     * The medium
     */
    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    /**
     *
     * @return
     * The high
     */
    public High getHigh() {
        return high;
    }

    /**
     *
     * @param high
     * The high
     */
    public void setHigh(High high) {
        this.high = high;
    }

    public Maxres getMaxres() {
        return maxres;
    }

    public void setMaxres(Maxres maxres) {
        this.maxres = maxres;
    }
}
