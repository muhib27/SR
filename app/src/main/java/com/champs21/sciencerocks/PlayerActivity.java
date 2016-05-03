package com.champs21.sciencerocks;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.champs21.sciencerocks.utils.AppConstants;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{


    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private String vidId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(AppConstants.YOUTUBE_CHANNEL_ID, this);


        if(getIntent().getExtras()!=null){
            vidId = getIntent().getExtras().getString(AppConstants.ID_VIDEO);
            Log.e("VID_ID", "is: "+vidId);
        }
    }



    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(vidId); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            Toast.makeText(this, "Player error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(AppConstants.YOUTUBE_CHANNEL_ID, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

}
