package com.champs21.sciencerocks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.realm.HighScoreAttempts;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.ScoreManager;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

public class ResultPageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private ScoreManager scoreManager;

    //private TextView txtTotalQuestion;
    //private TextView txtDataCorrectAnswer;
    //private TextView txtDataWrongAnswer;
    private TextView txtTotalMarks;
    private TextView txtYourMarks;
    //private TextView txtTotalTime;

    private int rightScoreCount = 0;
    private int wrongScoreCount = 0;

    private String levelId = "";
    private AppCompatButton btnSaveScore;
    private CircularProgressView progressView;
    private boolean isSavedButonClicked = false;

    //google auth
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private AppCompatImageButton btnShare;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private AppCompatButton btnSummery;
    private AppCompatButton btnTopScore;
    private AppCompatButton btnTryAgain;

    private TextView txtHighestScore;
    private MaterialDialog md = null;
    private TextView txtBestScore;
    private LinearLayout layoutShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_result_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            scoreManager = new Gson().fromJson(getIntent().getExtras().getString(AppConstants.QUIZ_SCORE_MANAGER), ScoreManager.class);
            levelId = getIntent().getExtras().getString(AppConstants.QUIZ_LEVEL_ID);
        }

        Log.e("SCORE_MANAGER", "question: "+scoreManager.getTotalQuestion());
        Log.e("SCORE_MANAGER", "your score: "+scoreManager.getScore());
        Log.e("SCORE_MANAGER", "time: "+scoreManager.getTime());
        Log.e("SCORE_MANAGER", "total score: "+scoreManager.getTotalScore());

        for(int i=0;i<scoreManager.getListCorrect().size();i++){
            Log.e("SCORE_MANAGER", "correct ans: "+scoreManager.getListCorrect().get(i));
        }

        rightScoreCount = Collections.frequency(scoreManager.getListCorrect(), true);
        wrongScoreCount = Collections.frequency(scoreManager.getListCorrect(), false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        initView();
        initAction();
        initApiGetScore();



        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                //Toast.makeText(ResultPageActivity.this, "Successfully posted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                //Toast.makeText(ResultPageActivity.this, "Something went wrong, try to post later!", Toast.LENGTH_SHORT).show();
            }
        });

        ApplicationSingleton.getInstance().requestAdMob(this);

        Log.e("ON_CREATE", "called onCreate()");



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_champs, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        else if(menuItem.getItemId() == R.id.action_champs){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.champs21.schoolapp"));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    private void initView(){
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        progressView.setVisibility(View.GONE);
        //txtTotalQuestion = (TextView)this.findViewById(R.id.txtTotalQuestion);
        //txtDataCorrectAnswer = (TextView)this.findViewById(R.id.txtDataCorrectAnswer);
        //txtDataWrongAnswer = (TextView)this.findViewById(R.id.txtDataWrongAnswer);
        txtTotalMarks = (TextView)this.findViewById(R.id.txtTotalMarks);
        txtYourMarks = (TextView)this.findViewById(R.id.txtYourMarks);
        //txtTotalTime = (TextView)this.findViewById(R.id.txtTotalTime);

        btnSaveScore = (AppCompatButton)this.findViewById(R.id.btnSaveScore);

        btnShare = (AppCompatImageButton) this.findViewById(R.id.btnShare);

        btnSummery = (AppCompatButton)this.findViewById(R.id.btnSummery);
        btnTopScore = (AppCompatButton)this.findViewById(R.id.btnTopScore);
        btnTryAgain = (AppCompatButton)this.findViewById(R.id.btnTryAgain);

        txtHighestScore = (TextView)this.findViewById(R.id.txtHighestScore);
        txtBestScore = (TextView)this.findViewById(R.id.txtBestScore);
        layoutShare = (LinearLayout)this.findViewById(R.id.layoutShare);

    }

    private void initAction(){
        //txtTotalQuestion.setText(String.valueOf(scoreManager.getTotalQuestion()));
        txtTotalMarks.setText(String.valueOf(scoreManager.getTotalScore()));
        txtYourMarks.setText(String.valueOf(scoreManager.getScore()));
        //txtTotalTime.setText(getDurationBreakdown(scoreManager.getTime()));
        //txtDataCorrectAnswer.setText(String.valueOf(rightScoreCount));
        //txtDataWrongAnswer.setText(String.valueOf(wrongScoreCount));

        btnSaveScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isSavedButonClicked == false){
                    if(!TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_USER_ID))){
                        initApiCall();
                    }
                    else {
                        signIn();
                    }
                }
                else{
                    Toast.makeText(ResultPageActivity.this, R.string.msg_already_saved_score, Toast.LENGTH_SHORT).show();
                }



            }
        });


        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.app_name))
                            .setContentDescription("I have scored "+String.valueOf(scoreManager.getScore()+". \n" +
                                    "Watch Channel i every Friday morning at 11:05 am. Keep rocking with science!"))
                            .setContentUrl(Uri.parse(getString(R.string.app_play_store_link)))
                            .setImageUrl(Uri.parse(getString(R.string.app_play_store_image_link)))
                            .build();

                    shareDialog.show(linkContent);
                }

            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.app_name))
                            .setContentDescription("I have scored "+String.valueOf(scoreManager.getScore()+". \n" +
                                    "Watch Channel i every Friday morning at 11:05 am. Keep rocking with science!"))
                            .setContentUrl(Uri.parse(getString(R.string.app_play_store_link)))
                            .setImageUrl(Uri.parse(getString(R.string.app_play_store_image_link)))
                            .build();

                    shareDialog.show(linkContent);
                }

            }
        });

        btnSummery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultPageActivity.this, SummeryActivity.class);
                startActivity(intent);
            }
        });

        btnTopScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ResultPageActivity.this, LeaderBoardActivity.class);
                startActivity(intent);
            }
        });

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultPageActivity.this, QuizActivity.class);
                intent.putExtra(AppConstants.QUIZ_LEVEL_ID, levelId);
                startActivity(intent);
            }
        });

        txtBestScore.setText(String.valueOf(getBestScore(levelId)));


    }

    public String getDurationBreakdown(long millis) {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append(" Days ");
        sb.append(hours);
        sb.append(" Hours ");
        sb.append(minutes);
        sb.append(" Minutes ");
        sb.append(seconds);
        sb.append(" Seconds");

        String value = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds);

        return value;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GOOGLE_AUTH", "onConnectionFailed:" + connectionResult);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {


                }
            });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                }
            });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }


        //fb callbacks
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GOOGLE_AUTH", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("RESULT", acct.getId());
            Log.e("RESULT", acct.getDisplayName());
            Log.e("RESULT", acct.getEmail());
            //Log.e("RESULT", acct.getIdToken());
            ApplicationSingleton.getInstance().savePrefString(AppConstants.GOOGLE_AUTH_USER_ID, acct.getId());
            ApplicationSingleton.getInstance().savePrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME, acct.getDisplayName());
            ApplicationSingleton.getInstance().savePrefString(AppConstants.GOOGLE_AUTH_EMAIL, acct.getEmail());
            if(acct.getPhotoUrl() != null)
                ApplicationSingleton.getInstance().savePrefString(AppConstants.GOOGLE_AUTH_PROFILE_IMAGE, acct.getPhotoUrl().toString());

            initApiCall();


        } else {
            // Signed out, show unauthenticated UI.

        }
    }

    private void initApiCall(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();



        final Map<String,String> params = new HashMap<String,String>();
        params.put("level_id", levelId);
        params.put("score", String.valueOf(scoreManager.getScore()));
        params.put("time", String.valueOf(scoreManager.getTime()/1000));
        params.put("name", ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME));
        params.put("email", ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_EMAIL));
        params.put("auth_id", ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_USER_ID));

        String photoUrl = ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_PROFILE_IMAGE);

        if(!TextUtils.isEmpty(photoUrl)){
            params.put("profile_image", photoUrl);
        }

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_SAVE_SCORE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                ModelBase mb = ModelBase.getInstance().setResponse(response);

                if(mb.getStatus().getCode() == 200){

                    isSavedButonClicked = true;

                    Toast.makeText(ResultPageActivity.this, R.string.msg_score_saved_successfully, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);
            }
        }){
            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }

        };

        RequestQueue rq = Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mpr);

    }

    private void initApiGetScore(){


        if(!isFinishing()){
            md = null;
            md = new MaterialDialog.Builder(ResultPageActivity.this)
                    .content(R.string.msg_please_wait)
                    .progress(true, 0)
                    .show();
        }

        MultiPartStringRequest jor = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_GET_HIGH_SCORE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(md!=null){
                    md.dismiss();
                }

                Log.e("*** RESPONSE ***", "is: "+response);
                ModelBase mb = ModelBase.getInstance().setResponse(response);

                if(mb.getStatus().getCode() == 200){
                    txtHighestScore.setText(mb.getData().getScore());
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public Map<String, String> getStringUploads() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("level_id", levelId);
                return params;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(ResultPageActivity.this, new MultiPartStack());
        rq.add(jor);
    }

    private int getBestScore(String key){
        int score = 0;

        Realm realm = ApplicationSingleton.getInstance().getRealm();
        HighScoreAttempts highScoreAttempts = null;
        realm.beginTransaction();
        highScoreAttempts = realm.where(HighScoreAttempts.class).equalTo("keyScore", key).findFirst();
        if(highScoreAttempts == null){
            highScoreAttempts = realm.createObject(HighScoreAttempts.class);
            highScoreAttempts.setValueScore(scoreManager.getScore());
            highScoreAttempts.setKeyScore(key);
        }

        if(scoreManager.getScore() > highScoreAttempts.getValueScore()){
            highScoreAttempts.setValueScore(scoreManager.getScore());
        }

        score = highScoreAttempts.getValueScore();
        realm.commitTransaction();

        Log.e("SCORE", "is: "+score);

        return score;
    }
}
