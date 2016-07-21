package com.champs21.sciencerocks.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by BLACK HAT on 21-Jul-16.
 */
public class RealmLevel extends RealmObject{


    @PrimaryKey
    private String key;
    private String id;
    private boolean isNew;
    private boolean isVisitedQuiz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isVisitedQuiz() {
        return isVisitedQuiz;
    }

    public void setVisitedQuiz(boolean visitedQuiz) {
        isVisitedQuiz = visitedQuiz;
    }
}
