package com.champs21.sciencerocks;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.Episode;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinnerListRootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private WinnerListAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Episode> listItems;
    private CircularProgressView progressView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView txtMessage;
    private boolean hasNext;
    private int pageNumber = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner_list_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listItems = new ArrayList<Episode>();
        listItems.clear();

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
        progressView.setVisibility(View.GONE);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        mSwipyRefreshLayout = (SwipyRefreshLayout)this.findViewById(R.id.swipyrefreshlayout);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
    }

    private void initAction(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if(hasNext == true){
                    pageNumber++;
                    initApiCall();
                }
                else{
                    stopLoader();
                }
            }
        });
    }


    private void initApiCall(){

        if(listItems.size()<=0){
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
        }

        final Map<String, String> params = new HashMap<String, String>();
        params.put("page_number", String.valueOf(pageNumber));
        params.put("page_size", "2");


        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_GET_EPISODE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                stopLoader();

                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){

                    hasNext = mb.getData().getHasNext();

                    listItems.addAll(mb.getData().getEpisodes());

                    if(listItems.size()<=0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    if(adapter==null){
                        adapter = new WinnerListAdapter(listItems);
                        recyclerView.setAdapter(adapter);
                    }

                    adapter.notifyDataSetChanged();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);
                stopLoader();
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

    public class WinnerListAdapter extends RecyclerView.Adapter<WinnerListAdapter.MyViewHolder> {

        private List<Episode> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtEpisodeTitle;
            TextView txtDate;

            TextView txtWinnerName1;
            TextView textWinnerJob1;
            TextView txtWinnerDistrict1;

            TextView txtWinnerName2;
            TextView textWinnerJob2;
            TextView txtWinnerDistrict2;

            TextView txtWinnerName3;
            TextView textWinnerJob3;
            TextView txtWinnerDistrict3;

            AppCompatButton btnRowWinnerList;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtEpisodeTitle = (TextView) itemView.findViewById(R.id.txtEpisodeTitle);
                this.txtDate = (TextView)itemView.findViewById(R.id.txtDate);

                this.txtWinnerName1 = (TextView)itemView.findViewById(R.id.txtWinnerName1);
                this.textWinnerJob1 = (TextView)itemView.findViewById(R.id.textWinnerJob1);
                this.txtWinnerDistrict1 = (TextView)itemView.findViewById(R.id.txtWinnerDistrict1);

                this.txtWinnerName2 = (TextView)itemView.findViewById(R.id.txtWinnerName2);
                this.textWinnerJob2 = (TextView)itemView.findViewById(R.id.textWinnerJob2);
                this.txtWinnerDistrict2 = (TextView)itemView.findViewById(R.id.txtWinnerDistrict2);

                this.txtWinnerName3 = (TextView)itemView.findViewById(R.id.txtWinnerName3);
                this.textWinnerJob3 = (TextView)itemView.findViewById(R.id.textWinnerJob3);
                this.txtWinnerDistrict3 = (TextView)itemView.findViewById(R.id.txtWinnerDistrict3);


                this.btnRowWinnerList = (AppCompatButton)itemView.findViewById(R.id.btnRowWinnerList);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public WinnerListAdapter(List<Episode> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_winner_list_layout, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtEpisodeTitle = holder.txtEpisodeTitle;
            TextView txtDate = holder.txtDate;

            TextView txtWinnerName1 = holder.txtWinnerName1;
            TextView textWinnerJob1 = holder.textWinnerJob1;
            TextView txtWinnerDistrict1 = holder.txtWinnerDistrict1;

            TextView txtWinnerName2 = holder.txtWinnerName2;
            TextView textWinnerJob2 = holder.textWinnerJob2;
            TextView txtWinnerDistrict2 = holder.txtWinnerDistrict2;

            TextView txtWinnerName3 = holder.txtWinnerName3;
            TextView textWinnerJob3 = holder.textWinnerJob3;
            TextView txtWinnerDistrict3 = holder.txtWinnerDistrict3;


            AppCompatButton btnRowWinnerList = holder.btnRowWinnerList;
            CardView cardView = holder.cardView;


            txtEpisodeTitle.setText(listItems.get(listPosition).getName());
            txtDate.setText(listItems.get(listPosition).getDate());

            txtWinnerName1.setText(listItems.get(listPosition).getWinner1());
            textWinnerJob1.setText(listItems.get(listPosition).getWinner1Occupation());
            txtWinnerDistrict1.setText(listItems.get(listPosition).getWinner1District());

            txtWinnerName2.setText(listItems.get(listPosition).getWinner2());
            textWinnerJob2.setText(listItems.get(listPosition).getWinner2Occupation());
            txtWinnerDistrict2.setText(listItems.get(listPosition).getWinner2District());

            txtWinnerName3.setText(listItems.get(listPosition).getWinner3());
            textWinnerJob3.setText(listItems.get(listPosition).getWinner3Occupation());
            txtWinnerDistrict3.setText(listItems.get(listPosition).getWinner3District());



            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            btnRowWinnerList.setOnClickListener(new View.OnClickListener() {
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

    private void stopLoader(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                if (WinnerListRootActivity.this != null) {
                    WinnerListRootActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mSwipyRefreshLayout != null)
                                mSwipyRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            }
        }, 500);
    }

    private void showDialog(Episode episode){
        if(!isFinishing()){

            final MaterialDialog dialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_title_quiz)
                    .customView(R.layout.layout_dialog_explanation_next, false)
                    .cancelable(false)
                    .show();

            View dialogView = dialog.getCustomView();


        }
    }

}
