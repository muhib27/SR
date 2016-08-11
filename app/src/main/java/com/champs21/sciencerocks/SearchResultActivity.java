package com.champs21.sciencerocks;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.models.SearchModel;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity {

    private String searchString = "";

    private RecyclerView recyclerView;
    private SearchAdapter adapter;
    private LinearLayoutManager layoutManager;
    private CircularProgressView progressView;
    private TextView txtMessage;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private List<SearchModel> listItems;
    private Gson gson;
    private String pageToken = "";
    private String pageToken2 = "";
    private String currentLanguage = AppConstants.LANG_BN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gson = new Gson();
        if(getIntent().getExtras() != null){
            searchString = getIntent().getExtras().getString(AppConstants.SEARCH_STRING);
            setTitle(getString(R.string.search_for)+"\""+searchString+"\"");
        }

        listItems = new ArrayList<SearchModel>();
        Log.e("SEARCH_RESULT", "is: "+searchString);

        initView();
        initAction();
        initApiCall();


        if(TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER))){
            currentLanguage = AppConstants.LANG_BN;
            ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_BN);
        }else{
            currentLanguage = ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER);
        }

    }

    private void initView(){
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        progressView.setVisibility(View.GONE);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
    }

    private void initAction(){

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.recyclerViewQaList_divider_search)
                        .build());

    }

    private void initApiCall(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();


        MultiPartStringRequest mprTopicDoze = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_SEARCH), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);


                ModelBase mb = ModelBase.getInstance().setResponse(response);
                if(mb.getStatus().getCode() == 200){

                    JSONObject object = response;
                    JsonParser jsonParser = new JsonParser();
                    JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());

                    JsonArray arrayDailyDoze = gsonObject.get("data").getAsJsonObject().get("dailydose").getAsJsonArray();
                    for(int i = 0;i<arrayDailyDoze.size();i++){
                        listItems.add(gson.fromJson(arrayDailyDoze.get(i).toString(), SearchModel.class));
                    }


                    JsonArray arrayTopcis = gsonObject.get("data").getAsJsonObject().get("topics").getAsJsonArray();
                    for(int i = 0;i<arrayTopcis.size();i++){
                        listItems.add(gson.fromJson(arrayTopcis.get(i).toString(), SearchModel.class));
                    }

                    if(adapter == null){
                        adapter = new SearchAdapter(listItems);
                        recyclerView.setAdapter(adapter);
                    }
                    if(listItems.size()<=0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    adapter.notifyDataSetChanged();

                }

                Log.e("LIST_SIZE", "is: "+listItems.size());



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public Map<String, String> getStringUploads() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("term", searchString);
                return params;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mprTopicDoze);


        initApiYoutubeRocking();
        initApiYoutubeWow();

    }

    private void initApiYoutubeRocking(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        String str = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&pageToken="+pageToken+"&playlistId="+AppConstants.PALYLIST_ID_ROCKING_EXP+"&key=AIzaSyCMnZ5p2UpbEGnctTX6DJdi-k9ELH22r0I";
        MultiPartStringRequest mprVideo = new MultiPartStringRequest(Request.Method.GET, str, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                JSONObject object = response;
                JsonParser jsonParser = new JsonParser();
                JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());

                if(gsonObject.get("nextPageToken")!=null)
                    pageToken = gsonObject.get("nextPageToken").getAsString();
                else
                    pageToken = "";

                JsonArray arrayitems = gsonObject.get("items").getAsJsonArray();

                for(int i=0;i<arrayitems.size();i++){

                    String title = arrayitems.get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString();
                    String vidId = arrayitems.get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString();
                    SearchModel sm = new SearchModel();
                    sm.setTitle(title);
                    sm.setVideoId(vidId);
                    sm.setFromYoutube(true);

                    if(sm.getTitle().toLowerCase().contains(searchString.toLowerCase())){
                        listItems.add(sm);
                    }
                }

                if(adapter == null){
                    adapter = new SearchAdapter(listItems);
                    recyclerView.setAdapter(adapter);
                }
                if(listItems.size()<=0){
                    txtMessage.setVisibility(View.VISIBLE);
                }else{
                    txtMessage.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();


                if(!TextUtils.isEmpty(pageToken)){
                    initApiYoutubeRocking();
                }

                Log.e("LIST_SIZE", "is: "+listItems.size());



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue rq1 = Volley.newRequestQueue(this, new MultiPartStack());
        rq1.add(mprVideo);



    }

    private void initApiYoutubeWow(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        String str2 = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet&maxResults=20&pageToken="+pageToken2+"&playlistId="+AppConstants.PALYLIST_ID_FUNNY_VIDEOS+"&key=AIzaSyCMnZ5p2UpbEGnctTX6DJdi-k9ELH22r0I";
        MultiPartStringRequest mprVideo2 = new MultiPartStringRequest(Request.Method.GET, str2, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);


                JSONObject object = response;
                JsonParser jsonParser = new JsonParser();
                JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());

                if(gsonObject.get("nextPageToken")!=null)
                    pageToken2 = gsonObject.get("nextPageToken").getAsString();
                else
                    pageToken = "";

                JsonArray arrayitems = gsonObject.get("items").getAsJsonArray();

                for(int i=0;i<arrayitems.size();i++){

                    String title = arrayitems.get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString();
                    String vidId = arrayitems.get(i).getAsJsonObject().get("snippet").getAsJsonObject().get("resourceId").getAsJsonObject().get("videoId").getAsString();
                    SearchModel sm = new SearchModel();
                    sm.setTitle(title);
                    sm.setVideoId(vidId);
                    sm.setFromYoutube(true);

                    if(sm.getTitle().toLowerCase().contains(searchString.toLowerCase())){
                        listItems.add(sm);
                    }
                }

                if(adapter == null){
                    adapter = new SearchAdapter(listItems);
                    recyclerView.setAdapter(adapter);
                }
                if(listItems.size()<=0){
                    txtMessage.setVisibility(View.VISIBLE);
                }else{
                    txtMessage.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();


                if(!TextUtils.isEmpty(pageToken2)){
                    initApiYoutubeWow();
                }

                Log.e("LIST_SIZE", "is: "+listItems.size());



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue rq2 = Volley.newRequestQueue(this, new MultiPartStack());
        rq2.add(mprVideo2);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

        private List<SearchModel> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView imgViewNetwork;
            TextView txtTitle;
            TextView txtCategory;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.imgViewNetwork = (ImageView)itemView.findViewById(R.id.imgViewNetwork);
                this.txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                this.txtCategory = (TextView)itemView.findViewById(R.id.txtCategory);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public SearchAdapter(List<SearchModel> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_search_items, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            ImageView imgViewNetwork = holder.imgViewNetwork;
            TextView txtTitle = holder.txtTitle;
            TextView txtCategory = holder.txtCategory;
            CardView cardView = holder.cardView;

            if(dataSet.get(listPosition).isFromYoutube()){

                if(!TextUtils.isEmpty(dataSet.get(listPosition).getTitle())){
                    txtTitle.setText(dataSet.get(listPosition).getTitle());
                    imgViewNetwork.setImageResource(R.drawable.grid_1);
                    txtCategory.setText("Toutube Video");
                }

            }else {
                if(!TextUtils.isEmpty(dataSet.get(listPosition).getTitle())){
                    txtTitle.setText(dataSet.get(listPosition).getTitle());
                    imgViewNetwork.setImageResource(R.drawable.grid_4);
                    txtCategory.setText("Daily Doze");
                }else{
                    txtTitle.setText("Daily Doze");
                    txtCategory.setText("Daily Doze");
                    imgViewNetwork.setImageResource(R.drawable.grid_4);
                }

                if(currentLanguage.equals(AppConstants.LANG_EN)){

                    if(!TextUtils.isEmpty(dataSet.get(listPosition).getEnName())){
                        txtTitle.setText(dataSet.get(listPosition).getEnName());
                        imgViewNetwork.setImageResource(R.drawable.grid_3);
                        txtCategory.setText("Quiz");
                    }
                }else{
                    if(!TextUtils.isEmpty(dataSet.get(listPosition).getName())){
                        txtTitle.setText(dataSet.get(listPosition).getName());
                        imgViewNetwork.setImageResource(R.drawable.grid_3);
                        txtCategory.setText("Quiz");
                    }
                }


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
