package com.champs21.sciencerocks;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.Dailydose;
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

public class DailyDozeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DailyDozeAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Dailydose> listItems;
    private CircularProgressView progressView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private TextView txtMessage;
    private boolean hasNext;
    private int pageNumber = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_doze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems = new ArrayList<Dailydose>();
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
        params.put("page_size", "10");

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_DAILY_DOZE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                stopLoader();

                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){

                    hasNext = mb.getData().getHasNext();

                    listItems.addAll(mb.getData().getDailydose());

                    if(listItems.size()<=0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    if(adapter==null){
                        adapter = new DailyDozeAdapter(listItems);
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

    private void lodWebViewData(WebView webView, String data){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);
    }

    public class DailyDozeAdapter extends RecyclerView.Adapter<DailyDozeAdapter.MyViewHolder> {

        private List<Dailydose> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            WebView webView;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.webView = (WebView) itemView.findViewById(R.id.webView);
                this.webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public DailyDozeAdapter(List<Dailydose> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_dailydoze_layout, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            WebView webView = holder.webView;
            CardView cardView = holder.cardView;

            lodWebViewData(webView, dataSet.get(listPosition).getContent());


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

    private void stopLoader(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                if (DailyDozeActivity.this != null) {
                    DailyDozeActivity.this.runOnUiThread(new Runnable() {

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

}
