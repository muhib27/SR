package com.champs21.sciencerocks.youtubemodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by BLACK HAT on 13-Apr-16.
 */
public class PageInfo {
    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;
    @SerializedName("resultsPerPage")
    @Expose
    private Integer resultsPerPage;

    /**
     *
     * @return
     * The totalResults
     */
    public Integer getTotalResults() {
        return totalResults;
    }

    /**
     *
     * @param totalResults
     * The totalResults
     */
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    /**
     *
     * @return
     * The resultsPerPage
     */
    public Integer getResultsPerPage() {
        return resultsPerPage;
    }

    /**
     *
     * @param resultsPerPage
     * The resultsPerPage
     */
    public void setResultsPerPage(Integer resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}
