package com.champs21.sciencerocks.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by BLACK HAT on 24-Jul-16.
 */
public class HighScoreAttempts extends RealmObject{

    @PrimaryKey
    private String key;


    private String keyAttempts;
    private int valueAttempts = 0;
    private String keyScore;
    private int valueScore = 0;
    private RealmList<MyMap> map;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyAttempts() {
        return keyAttempts;
    }

    public void setKeyAttempts(String keyAttempts) {
        this.keyAttempts = keyAttempts;
    }

    public int getValueAttempts() {
        return valueAttempts;
    }

    public void setValueAttempts(int valueAttempts) {
        this.valueAttempts = valueAttempts;
    }

    public String getKeyScore() {
        return keyScore;
    }

    public void setKeyScore(String keyScore) {
        this.keyScore = keyScore;
    }

    public int getValueScore() {
        return valueScore;
    }

    public void setValueScore(int valueScore) {
        this.valueScore = valueScore;
    }

    public RealmList<MyMap> getMap() {
        return map;
    }

    public void setMap(RealmList<MyMap> map) {
        this.map = map;
    }
}
