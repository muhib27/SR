package com.champs21.sciencerocks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.models.Topic;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopicRootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CircularProgressView progressView;
    private TextView txtMessage;
    private TopicAdapter adapter;
    private List<Topic> listTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listTopic = new ArrayList<Topic>();

        initView();
        initApiCall();
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
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
    }

    private void initAction(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.recyclerView_divider)
                        .build());
    }

    private void initApiCall(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();
        listTopic.clear();


        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.GET, UrlHelper.baseUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                ModelBase mb = ModelBase.getInstance().setResponse(response);

                if(mb.getStatus().getCode() == 200){

                    listTopic.addAll(mb.getData().getTopics());
                    if(adapter == null){
                        adapter = new TopicAdapter(listTopic);
                        recyclerView.setAdapter(adapter);
                    }

                    if(listTopic.size() <= 0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);
            }
        });

        RequestQueue rq = Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mpr);

    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> {

        private List<Topic> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtTopicTitle;
            TextView txtTopicDetails;
            CardView cardView;
            LinearLayout layoutDescriptionHolder;
            LinearLayout layoutTopProceedHolder;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtTopicTitle = (TextView) itemView.findViewById(R.id.txtTopicTitle);
                this.txtTopicDetails = (TextView)itemView.findViewById(R.id.txtTopicDetails);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
                this.layoutDescriptionHolder = (LinearLayout)itemView.findViewById(R.id.layoutDescriptionHolder);
                this.layoutTopProceedHolder = (LinearLayout)itemView.findViewById(R.id.layoutTopProceedHolder);
            }
        }

        public TopicAdapter(List<Topic> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_topic_layout2, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtTopicTitle = holder.txtTopicTitle;
            TextView txtTopicDetails = holder.txtTopicDetails;
            CardView cardView = holder.cardView;
            LinearLayout layoutDescriptionHolder = holder.layoutDescriptionHolder;
            LinearLayout layoutTopProceedHolder = holder.layoutTopProceedHolder;


            if(TextUtils.isEmpty(dataSet.get(listPosition).getDetails())){
                layoutDescriptionHolder.setVisibility(View.GONE);
                layoutTopProceedHolder.setVisibility(View.VISIBLE);
            }
            else {
                layoutDescriptionHolder.setVisibility(View.VISIBLE);
                layoutTopProceedHolder.setVisibility(View.GONE);
            }

            txtTopicTitle.setText(dataSet.get(listPosition).getName());
            txtTopicDetails.setText(dataSet.get(listPosition).getDetails());


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TopicRootActivity.this, LevelRootActivity.class);
                    intent.putExtra(AppConstants.QUIZ_TOPIC_ID, dataSet.get(listPosition).getId());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

}
