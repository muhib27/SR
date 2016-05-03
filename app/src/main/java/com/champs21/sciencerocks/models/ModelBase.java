package com.champs21.sciencerocks.models;

/**
 * Created by BLACK HAT on 26-Apr-16.
 */
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelBase {

    private static ModelBase instance;
    private Gson gson;

    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("status")
    @Expose
    private Status status;

    private ModelBase(){
        gson = new Gson();
    }

    public static ModelBase getInstance(){
        if(instance == null){
            instance = new ModelBase();
        }
        return instance;
    }

    public ModelBase setResponse(JSONObject response){
        try {
            setData(gson.fromJson(response.get("data").toString(), Data.class));
            setStatus(gson.fromJson(response.get("status").toString(), Status.class));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return instance;

    }

    /**
     *
     * @return
     * The data
     */
    public Data getData() {
        return data;
    }

    /**
     *
     * @param data
     * The data
     */
    public void setData(Data data) {
        this.data = data;
    }

    /**
     *
     * @return
     * The status
     */
    public Status getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

}