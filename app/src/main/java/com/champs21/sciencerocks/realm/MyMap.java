package com.champs21.sciencerocks.realm;

import io.realm.RealmObject;

/**
 * Created by user on 7/25/2016.
 */
public class MyMap extends RealmObject{

    private String key;
    private Integer value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
