package com.champs21.sciencerocks.utils;

/**
 * Created by BLACK HAT on 17-Apr-16.
 */
public class UrlHelper {

    public static final String YOUTUBE_URL_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlists?part=snippet%2C+contentDetails&channelId="
            +AppConstants.YOUTUBE_CHANNEL_ID+"&maxResults=20"+
            "&key=AIzaSyCMnZ5p2UpbEGnctTX6DJdi-k9ELH22r0I";

    public static String getYoutubePlaylistWithPageToken(String pageToken){
        String str = "https://www.googleapis.com/youtube/v3/playlists?part=snippet%2C+contentDetails&channelId="
                +AppConstants.YOUTUBE_CHANNEL_ID+"&maxResults=20"+"&pageToken="+pageToken+
                "&key=AIzaSyCMnZ5p2UpbEGnctTX6DJdi-k9ELH22r0I";
        return str;
    }

    public static String getYoutubeUrlPlaylistItems(String playListId, String pageToken){
        String str = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&pageToken="+pageToken+"&playlistId="+playListId+"&key=AIzaSyCMnZ5p2UpbEGnctTX6DJdi-k9ELH22r0I";
        return str;
    }

    public static final String baseUrl = "http://api.champs21.com/api/sciencerocks";

    public static String newUrl(String url){
        String str = baseUrl+"/"+url;

        return str;
    }

    public static final String URL_GET_LEVEL = "getlevel";
    public static final String URL_GET_QUESTION = "getquestion";
    public static final String URL_SAVE_SCORE = "savescore";
    public static final String URL_DAILY_DOZE = "getdailydosehistory";
    public static final String URL_GET_ANCHOR_LIST = "getanchorquestion";
    public static final String URL_ASK_QUESTION = "ask";
    public static final String URL_GET_EPISODE = "getepisode";
    public static final String URL_GET_HIGH_SCORE = "gethighscore";
    public static final String URL_GET_LEADER_BOARD = "getscoreboard";
    public static final String URL_SEARCH = "search";




}
