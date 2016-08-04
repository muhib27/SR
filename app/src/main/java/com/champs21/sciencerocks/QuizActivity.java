package com.champs21.sciencerocks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.models.Option;
import com.champs21.sciencerocks.models.Question;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.realm.HighScoreAttempts;
import com.champs21.sciencerocks.realm.RealmLevel;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.CountDownTimerPausable;
import com.champs21.sciencerocks.utils.ScoreManager;
import com.champs21.sciencerocks.utils.TextViewPlus;
import com.champs21.sciencerocks.utils.TimeWatch;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.realm.Realm;


public class QuizActivity extends AppCompatActivity {


    private TextViewPlus txtTimer;
    private TextView txtScore;
    private TextView txtQuestion;

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private CircularProgressView progressView;
    private QuestionAdapter adapter;
    private List<Question> listQuestion;
    private String levelId = "";

    private int currentPosition = 0;
    private CountDownTimerPausable timer = null;

    private int score = 0;
    private ScoreManager scoreManager;
    private List<Boolean> listCorrect;
    private TimeWatch timeWatch;
    private long ellapsedTime = 0;
    private int totalMarks = 0;

    private MediaPlayer mp = null;
    private boolean isSoundOff = false;

    private int musicLength = 0;

    private int ranNum = 0;
    private TextView txtToolbarTitle;

    private String currentLanguage = AppConstants.LANG_BN;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtToolbarTitle = (TextView)toolbar.findViewById(R.id.txtToolbarTitle);
        txtToolbarTitle.setText("0/0");

        if(getIntent().getExtras()!=null){
            levelId = getIntent().getExtras().getString(AppConstants.QUIZ_LEVEL_ID);
        }

        listQuestion = new ArrayList<Question>();
        listCorrect = new ArrayList<Boolean>();

        setTitle(ApplicationSingleton.getInstance().getPrefString(AppConstants.QUIZ_PLAY_TITLE));

        if(TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER))){
            currentLanguage = AppConstants.LANG_BN;
            ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_BN);
        }else{
            currentLanguage = ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER);
        }

        initView();
        initApiCall();
        initAction();

        ranNum = getRandomNumberinRange();


        ApplicationSingleton.getInstance().requestAdMob(this);


        if(ApplicationSingleton.getInstance().getPrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE) == true){
            isSoundOff = true;
        }else {
            isSoundOff = false;
            initMusic();
        }

        Realm realm = ApplicationSingleton.getInstance().getRealm();
        realm.beginTransaction();
        /*RealmTopic realmTopic = realm.where(RealmTopic.class).findFirst();

        for(int i=0;i<realmTopic.getListLevels().size();i++){

            RealmLevel realmLevel = realmTopic.getListLevels().get(i);
            if(realmLevel.getId().equals(levelId)) {
                realmLevel.setNew(false);
                realmLevel.setId(levelId+getIntent().getExtras().getString(AppConstants.QUIZ_LEVEL_NAME));
                realmLevel.setVisitedQuiz(true);
                realm.copyToRealmOrUpdate(realmLevel);
            }

        }*/
        RealmLevel realmLevel = null;
        realmLevel = realm.where(RealmLevel.class).equalTo("id", levelId).findFirst();
        if(realmLevel==null){
            realmLevel = realm.createObject(RealmLevel.class);
            realmLevel.setId(levelId);
            realmLevel.setVisitedQuiz(true);
            realmLevel.setNew(false);
        }


        realm.commitTransaction();

        createOrUpdateAttempt(realm, levelId);


    }

    private void createOrUpdateAttempt(Realm  realm, String key) {
        HighScoreAttempts highScoreAttempts = null;
        highScoreAttempts = realm.where(HighScoreAttempts.class).equalTo("keyAttempts", key).findFirst();
        realm.beginTransaction();
        if (highScoreAttempts == null) {
            highScoreAttempts = realm.createObject(HighScoreAttempts.class);
            highScoreAttempts.setKeyAttempts(key);
            highScoreAttempts.setValueAttempts(highScoreAttempts.getValueAttempts()+1);
        } else {
            highScoreAttempts.setValueAttempts(highScoreAttempts.getValueAttempts()+1);
        }
        realm.commitTransaction();

        Log.e("ATTEMPTS", "is: "+highScoreAttempts.getValueAttempts());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_quiz_page, menu);

        if(isSoundOff == true){
           menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_volume_off_white_24dp));
        }else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.ic_volume_up_white_24dp));
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        else if(menuItem.getItemId() == R.id.action_quiz_music){

            isSoundOff = !isSoundOff;

            if(isSoundOff){
                menuItem.setIcon(R.drawable.ic_volume_off_white_24dp);
                ApplicationSingleton.getInstance().savePrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE, true);
                stopMusic();
            }else{
                menuItem.setIcon(R.drawable.ic_volume_up_white_24dp);
                ApplicationSingleton.getInstance().savePrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE, false);
                initMusic();
            }

        }
        else if(menuItem.getItemId() == R.id.action_champs){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.champs21.schoolapp"));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            mp.pause();
            musicLength = mp.getCurrentPosition();
        }

        if(timer!=null && !timer.isPaused()) {
            timer.pause();
        }

        Log.e("CALLED", "on pause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null) {
            mp.seekTo(musicLength);
            mp.start();
        }

        if(timer!=null && timer.isPaused()) {
            timer.start();
        }

        Log.e("CALLED", "on resume");
    }

    private void initView(){

        txtTimer = (TextViewPlus) this.findViewById(R.id.txtTimer);
        txtScore = (TextView)this.findViewById(R.id.txtScore);
        txtQuestion = (TextView)this.findViewById(R.id.txtQuestion);

        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);

    }

    private void initAction(){
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void populateData(int currentPosition){
        txtTimer.setText(listQuestion.get(currentPosition).getTime());
        if(currentLanguage.equals(AppConstants.LANG_EN)){
            txtQuestion.setText(listQuestion.get(currentPosition).getEnQuestion());
        }else{
            txtQuestion.setText(listQuestion.get(currentPosition).getQuestion());
        }

        initTimer((Long.parseLong(listQuestion.get(currentPosition).getTime()) * 1000)+1*1000);

        if(adapter==null){
            adapter = new QuestionAdapter(listQuestion.get(currentPosition).getOptions());
            recyclerView.setAdapter(adapter);
        }
        adapter.notifyDataSetChanged();

        timeWatch = TimeWatch.start();

        int i;
        int sum = 0;
        for(i = 0; i < listQuestion.size(); i++)
            sum += Integer.parseInt(listQuestion.get(i).getMark().trim());

        totalMarks = sum;

        txtToolbarTitle.setText(String.valueOf(currentPosition+1)+"/"+ String.valueOf(listQuestion.size()));

    }

    private void initTimer(final long time){

        if(timer == null){
            timer = new CountDownTimerPausable(time, 1) {
                @Override
                public void onTick(long millisUntilFinished) {

                    txtTimer.setText(convertSecondsToHMS(String.valueOf(millisUntilFinished / 1000)));
                }

                @Override
                public void onFinish() {
                    //showDoalogTimeUp();
                    //timer = null;
                    refreshSnippetForTimeOut();

                }
            };

            this.timer.start();
        }

    }
    private String convertSecondsToHMS(String seconds){
        String result = "";

        int second = Integer.parseInt(seconds);

        int hr = (int)(second/3600);
        int rem = (int)(second%3600);
        int mn = rem/60;
        int sec = rem%60;
        //String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;

        //result = hrStr+":"+mnStr+":"+secStr;
        result = mnStr+":"+secStr;

        return result;
    }

    private void
    initApiCall(){
        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        listQuestion.clear();


        final Map<String,String> params = new HashMap<String,String>();
        params.put("level_id", levelId);


        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_GET_QUESTION), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                ModelBase mb = ModelBase.getInstance().setResponse(response);

                Gson gson = new Gson();
                String value = gson.toJson(mb.getData().getQuestions());
                ApplicationSingleton.getInstance().savePrefString(AppConstants.QUESTION_ANSWER_EXP_SUMMERY, value);


                if(mb.getStatus().getCode() == 200){

                    listQuestion.addAll(mb.getData().getQuestions());


                    /*if(adapter == null){
                        adapter = new QuestionAdapter(listQuestion.get(currentPosition).getOptions());
                        recyclerView.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();*/

                    populateData(currentPosition);

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

    public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.MyViewHolder>{

        private List<Option> dataSet;
        private CardView rightAnswerCardView;

        public CardView getRightAnswerCardView() {
            return rightAnswerCardView;
        }

        public void setRightAnswerCardView(CardView rightAnswerCardView) {
            this.rightAnswerCardView = rightAnswerCardView;
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtOption;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtOption = (TextView) itemView.findViewById(R.id.txtOption);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);

            }
        }

        public QuestionAdapter(List<Option> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_quiz_answer_layout, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtOption = holder.txtOption;
            final CardView cardView = holder.cardView;

            if(currentLanguage.equals(AppConstants.LANG_EN)){
                txtOption.setText(dataSet.get(listPosition).getEnAnswer());
            }else{
                txtOption.setText(dataSet.get(listPosition).getAnswer());
            }


            if(dataSet.get(listPosition).getCorrect().equalsIgnoreCase("1")){
                setRightAnswerCardView(cardView);
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //option clicked here
                    refreshDataAndView((CardView)view, dataSet.get(listPosition));
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public void setAllViewsEnable(boolean enable){

            for(int i=0;i<dataSet.size();i++){

                recyclerView.getLayoutManager().findViewByPosition(i).setEnabled(enable);
            }
        }

    }


    private void showDialog(boolean isLastQuestion){

        if(!isFinishing()){
            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_title_quiz)
                    .customView(R.layout.layout_dialog_explanation_next, false)
                    .cancelable(false)
                    .show();

            View dialogView = dialog.getCustomView();

            LinearLayout layoutExplanation = (LinearLayout) dialogView.findViewById(R.id.layoutExplanation);
            TextView txtExplanation = (TextView)dialogView.findViewById(R.id.txtExplanation);
            LinearLayout layoutAnswer = (LinearLayout) dialogView.findViewById(R.id.layoutAnswer);
            TextView txtAnswer = (TextView)dialogView.findViewById(R.id.txtAnswer);



            AppCompatButton btnNext = (AppCompatButton) dialogView.findViewById(R.id.btnNext);
            if(isLastQuestion){
                if(currentLanguage.equals(AppConstants.LANG_EN)){
                    txtExplanation.setText(listQuestion.get(currentPosition-1).getEnExplanation());
                }else{
                    txtExplanation.setText(listQuestion.get(currentPosition-1).getExplanation());
                }

                for(Option op : listQuestion.get(currentPosition-1).getOptions()){
                   if(op.getCorrect().equalsIgnoreCase("1"))
                       if(currentLanguage.equals(AppConstants.LANG_EN)){
                           txtAnswer.setText(op.getEnAnswer());
                       }else{
                           txtAnswer.setText(op.getAnswer());
                       }

                    }
                btnNext.setText(R.string.btn_text_finish);

                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                Log.e("GO_TO", "result page");

                                scoreManager = new ScoreManager(listQuestion.size(), listCorrect, score, ellapsedTime, totalMarks);
                                Intent intent = new Intent(QuizActivity.this, ResultPageActivity.class);
                                String data = new Gson().toJson(scoreManager);
                                intent.putExtra(AppConstants.QUIZ_SCORE_MANAGER, data);
                                intent.putExtra(AppConstants.QUIZ_LEVEL_ID, levelId);
                                startActivity(intent);
                                finish();
                                }
                            });
                }
            else {
                if(currentPosition-1<listQuestion.size()) {
                    if(currentLanguage.equals(AppConstants.LANG_EN)){
                        txtExplanation.setText(listQuestion.get(currentPosition - 1).getEnExplanation());
                    }else{
                        txtExplanation.setText(listQuestion.get(currentPosition - 1).getExplanation());
                    }

                    for(Option op : listQuestion.get(currentPosition-1).getOptions()){
                        if(op.getCorrect().equalsIgnoreCase("1"))
                            if(currentLanguage.equals(AppConstants.LANG_EN)){
                                txtAnswer.setText(op.getEnAnswer());
                            }else{
                                txtAnswer.setText(op.getAnswer());
                            }

                        }

                    btnNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                           if(currentPosition<listQuestion.size()){
                               populateData(currentPosition);
                               dialog.dismiss();
                           }
                        }
                    });
                }
            }

        }
    }


    private void refreshDataAndView(final CardView cardView, final Option option){

        cardView.setEnabled(false);
        if(option.getCorrect().equalsIgnoreCase("1")){
            cardView.setCardBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.rightAnswerColor));

            if (currentPosition<listQuestion.size()) {
                score = score + Integer.parseInt(listQuestion.get(currentPosition).getMark().trim());
                listCorrect.add(true);
            }


        }
        else{
            cardView.setCardBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.wrongAnswerColor));
            listCorrect.add(false);
        }

        adapter.getRightAnswerCardView().setCardBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.rightAnswerColor));
        txtScore.setText(String.valueOf(score));

        adapter.setAllViewsEnable(false);

        ellapsedTime = ellapsedTime + timeWatch.time();


        Log.e("CURRENT_SCORE", "is: "+score);


        if(!timer.isPaused())
            timer.pause();


        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {


                cardView.setEnabled(true);
                cardView.setCardBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.colorWhite));
                adapter.getRightAnswerCardView().setCardBackgroundColor(ContextCompat.getColor(QuizActivity.this, R.color.colorWhite));

                currentPosition++;
                Log.e("CUR_POS", "is: "+currentPosition);

                if (currentPosition<listQuestion.size()) {

                    timer.cancel();
                    timer =  null;

                    recyclerView.removeAllViews();
                    adapter = null;

                    showDialog(false);

                    timeWatch.reset();

                    //populateData(currentPosition);

                }
                if(currentPosition==listQuestion.size()){
                    timer.cancel();
                    timer =  null;

                    recyclerView.removeAllViews();
                    adapter = null;

                    showDialog(true);

                    timeWatch.reset();
                }

                Log.e("ELLAPSED_TIME", "is: "+ellapsedTime/1000);




            }
        };

        handler.postDelayed(runnable, 2000);


    }

    private void refreshSnippetForTimeOut(){
        currentPosition++;
        ellapsedTime = ellapsedTime + timeWatch.time();
        if (currentPosition<listQuestion.size()) {

            timer.cancel();
            timer =  null;

            recyclerView.removeAllViews();
            adapter = null;
            timeWatch.reset();

            showDialog(false);



            //populateData(currentPosition);

        }
        if(currentPosition==listQuestion.size()){
            timer.cancel();
            timer =  null;

            recyclerView.removeAllViews();
            adapter = null;
            timeWatch.reset();

            showDialog(true);


        }

        Log.e("ELLAPSED_TIME", "is: "+ellapsedTime/1000);
    }

    private void initMusic(){
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }

        AudioManager mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)/2, 0);

        Log.e("RANDOM_NUM", "is: "+ranNum);
        switch (ranNum){
            case 0:

                mp = MediaPlayer.create(this, R.raw.first);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        mp.start();
                    }
                });
                mp.setLooping(true);

                break;
            case 1:

                mp = MediaPlayer.create(this, R.raw.second);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        mp.start();
                    }
                });
                mp.setLooping(true);

                break;
            case 2:

                mp = MediaPlayer.create(this, R.raw.third);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        mp.start();
                    }
                });
                mp.setLooping(true);

                break;
            default:

                mp = MediaPlayer.create(this, R.raw.first);
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {

                        mp.start();
                    }
                });
                mp.setLooping(true);

                break;
        }



    }

    private void stopMusic(){
        if (mp != null) {
            mp.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mp!=null){
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    private int getRandomNumberinRange(){
        Random r = new Random();
        int num = r.nextInt(3 - 0) + 0;
        return num;
    }

    private void showExitPopup(){

        if(timer!=null && !timer.isPaused()) {
            timer.pause();
        }

        if(!isFinishing()){

            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_title_quiz)
                    .content(R.string.exit_message)
                    .cancelable(true)
                    .positiveText(R.string.msg_no)
                    .negativeText(R.string.msg_yes)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            if(timer!=null && timer.isPaused()) {
                                timer.start();
                            }
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .cancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            if(timer!=null && timer.isPaused()) {
                                timer.start();
                            }
                        }
                    })
                    .show();

            dialog.show();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showExitPopup();
        }
        return true;
    }
}
