package com.champs21.sciencerocks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.Question;
import com.champs21.sciencerocks.utils.AppConstants;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SummeryActivity extends AppCompatActivity {

    private List<Question> listQuestion;
    private Gson gson;

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SummeryAdapter adapter;
    private TextView txtMessage;
    private CircularProgressView progressView;

    private String currentLanguage = AppConstants.LANG_BN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summery);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String value = ApplicationSingleton.getInstance().getPrefString(AppConstants.QUESTION_ANSWER_EXP_SUMMERY);

        gson = new Gson();
        listQuestion = new ArrayList<Question>();
        listQuestion.clear();
        listQuestion = gson.fromJson(value, new TypeToken<List<Question>>(){}.getType());

        if(TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER))){
            currentLanguage = AppConstants.LANG_BN;
            ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_BN);
        }else{
            currentLanguage = ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER);
        }

        initView();
        initAction();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initView(){
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        progressView.setVisibility(View.GONE);

        if(listQuestion!=null && listQuestion.size()<=0){
            txtMessage.setVisibility(View.VISIBLE);
        }
    }

    private void initAction(){

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new SummeryAdapter(listQuestion);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public class SummeryAdapter extends RecyclerView.Adapter<SummeryAdapter.MyViewHolder> {

        private List<Question> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtQuestion;
            TextView txtAnswer;
            TextView txtExplanation;
            CardView cardView;



            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtQuestion = (TextView) itemView.findViewById(R.id.txtQuestion);
                this.txtAnswer = (TextView)itemView.findViewById(R.id.txtAnswer);
                this.txtExplanation = (TextView)itemView.findViewById(R.id.txtExplanation);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public SummeryAdapter(List<Question> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_summery, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtQuestion = holder.txtQuestion;
            TextView txtAnswer = holder.txtAnswer;
            TextView txtExplanation = holder.txtExplanation;
            CardView cardView = holder.cardView;

            if(currentLanguage.equals(AppConstants.LANG_EN)){
                txtQuestion.setText(dataSet.get(listPosition).getEnQuestion());
            }else{
                txtQuestion.setText(dataSet.get(listPosition).getQuestion());
            }



            for(int i=0;i<dataSet.get(listPosition).getOptions().size();i++){
                if(dataSet.get(listPosition).getOptions().get(i).getCorrect().equalsIgnoreCase("1")){
                    if(currentLanguage.equals(AppConstants.LANG_EN)){
                        txtAnswer.setText(dataSet.get(listPosition).getOptions().get(i).getEnAnswer());
                    }else{
                        txtAnswer.setText(dataSet.get(listPosition).getOptions().get(i).getAnswer());
                    }

                }
            }

            if(currentLanguage.equals(AppConstants.LANG_EN)){
                txtExplanation.setText(dataSet.get(listPosition).getEnExplanation());
            }else{
                txtExplanation.setText(dataSet.get(listPosition).getExplanation());
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

}
