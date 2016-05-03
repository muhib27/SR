package com.champs21.sciencerocks.youtubemodels;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

/**
 * Created by BLACK HAT on 13-Apr-16.
 */
public class YoutubeModelBase {

    private static YoutubeModelBase instance;
    private Gson gson;
    private JsonObject objectData;



    private YoutubeModelBase(){
        gson = new Gson();

    }
    public static YoutubeModelBase getInstance(){
        if(instance == null){
            instance = new YoutubeModelBase();
        }
        return instance;
    }

    public YoutubeModelBase setResponse(JSONObject response) {
        setObjectData(gson.fromJson(response.toString(), JsonObject.class));
        return instance;
    }



    public JsonObject getObjectData() {
        return objectData;
    }

    public void setObjectData(JsonObject objectData) {
        this.objectData = objectData;
    }



    public PlaylistRoot getPlaylistRoot(){
        return gson.fromJson(getObjectData().toString(), PlaylistRoot.class);
    }


}
