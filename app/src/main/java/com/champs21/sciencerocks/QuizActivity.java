package com.champs21.sciencerocks;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            levelId = getIntent().getExtras().getString(AppConstants.QUIZ_LEVEL_ID);
        }

        listQuestion = new ArrayList<Question>();
        listCorrect = new ArrayList<Boolean>();


        initView();
        initApiCall();
        initAction();

        if(ApplicationSingleton.getInstance().getPrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE) == false){
            initMusic();
        }

        ApplicationSingleton.getInstance().requestAdMob(this);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
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
        txtQuestion.setText(listQuestion.get(currentPosition).getQuestion());
        initTimer(Long.parseLong(listQuestion.get(currentPosition).getTime()) * 1000);

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

    private void initApiCall(){
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

            txtOption.setText(dataSet.get(listPosition).getAnswer());

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
                txtExplanation.setText(listQuestion.get(currentPosition-1).getExplanation());
                for(Option op : listQuestion.get(currentPosition-1).getOptions()){
                   if(op.getCorrect().equalsIgnoreCase("1"))
                        txtAnswer.setText(op.getAnswer());
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
                    txtExplanation.setText(listQuestion.get(currentPosition - 1).getExplanation());
                    for(Option op : listQuestion.get(currentPosition-1).getOptions()){
                        if(op.getCorrect().equalsIgnoreCase("1"))
                            txtAnswer.setText(op.getAnswer());
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
                cardView.setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
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

        mp = MediaPlayer.create(this, R.raw.bg_music);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {

                mp.start();
            }
        });
        mp.setLooping(true);

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
}
