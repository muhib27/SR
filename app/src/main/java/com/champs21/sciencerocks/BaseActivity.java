package com.champs21.sciencerocks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by BLACK HAT on 19-Apr-16.
 */
public abstract  class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        setTitle(getActivityTitle());
    }

    public abstract int getLayoutResourceId();
    public abstract String getActivityTitle();
}
