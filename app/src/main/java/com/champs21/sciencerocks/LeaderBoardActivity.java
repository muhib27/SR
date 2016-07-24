package com.champs21.sciencerocks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.LeaderList;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.CustomVolleyRequestQueue;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LeaderBoardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LeaderBoardAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<LeaderList> listItems;
    private CircularProgressView progressView;
    private TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems = new ArrayList<LeaderList>();
        listItems.clear();

        initView();
        initApiCall();
        initAction();

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
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
    }

    private void initAction(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void initApiCall(){

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.GET, UrlHelper.newUrl(UrlHelper.URL_GET_LEADER_BOARD), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ****", "is: "+response);
                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){
                    listItems.addAll(mb.getData().getLeaderList());
                }

                if(listItems.size()<=0){
                    txtMessage.setVisibility(View.VISIBLE);
                }
                else{
                    txtMessage.setVisibility(View.GONE);
                }

                if(adapter==null){
                    adapter = new LeaderBoardAdapter(listItems);
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue rq = Volley.newRequestQueue(LeaderBoardActivity.this, new MultiPartStack());
        rq.add(mpr);
    }

    public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.MyViewHolder> {

        private List<LeaderList> dataSet;
        private ImageLoader mImageLoader;

        class MyViewHolder extends RecyclerView.ViewHolder {

            NetworkImageView imgViewNetwork;
            TextView txtPosition;
            TextView txtName;
            TextView txtEmail;
            TextView txtScore;
            TextView txtTime;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.imgViewNetwork = (NetworkImageView)itemView.findViewById(R.id.imgViewNetwork);
                this.txtPosition = (TextView) itemView.findViewById(R.id.txtPosition);
                this.txtName = (TextView)itemView.findViewById(R.id.txtName);
                this.txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
                this.txtScore = (TextView) itemView.findViewById(R.id.txtScore);
                this.txtTime = (TextView) itemView.findViewById(R.id.txtTime);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public LeaderBoardAdapter(List<LeaderList> data) {
            this.dataSet = data;
            this.mImageLoader = CustomVolleyRequestQueue.getInstance(LeaderBoardActivity.this).getImageLoader();

        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_leaderboard_list, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            NetworkImageView imgViewNetwork = holder.imgViewNetwork;
            TextView txtPosition = holder.txtPosition;
            TextView txtName = holder.txtName;
            TextView txtEmail = holder.txtEmail;
            TextView txtScore = holder.txtScore;
            TextView txtTime = holder.txtTime;
            CardView cardView = holder.cardView;

            mImageLoader.get("", ImageLoader.getImageListener(imgViewNetwork, R.drawable.avatar, R.drawable.avatar));
            imgViewNetwork.setImageUrl("", mImageLoader);

            int pos = listPosition;
            txtPosition.setText(String.valueOf(pos+1));
            txtName.setText(dataSet.get(listPosition).getName());
            txtEmail.setText(dataSet.get(listPosition).getEmail());
            txtScore.setText(dataSet.get(listPosition).getScore());

            Long time = Long.parseLong(dataSet.get(listPosition).getTime());
            txtTime.setText(getDurationBreakdown(time*1000));


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

    private String getDurationBreakdown(long millis)
    {
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

        String output = String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds);

        //return(sb.toString());
        return output;
    }
}
