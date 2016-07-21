package com.champs21.sciencerocks;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.Level;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.realm.RealmLevel;
import com.champs21.sciencerocks.realm.RealmTopic;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

public class LevelRootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CircularProgressView progressView;
    private TextView txtMessage;
    private LevelAdapter adapter;
    private List<Level> listLevels;
    private String topicId = "";
    private String topicName = "Level";
    private static final int REQUEST_FROM_QUIZ_PAGE = 450;
    private int countIsNowTopic = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            topicId = getIntent().getExtras().getString(AppConstants.QUIZ_TOPIC_ID);
            topicName = getIntent().getExtras().getString(AppConstants.QUIZ_LEVEL_NAME);
        }

        setTitle(topicName);
        ApplicationSingleton.getInstance().savePrefString(AppConstants.QUIZ_PLAY_TITLE, topicName);

        listLevels = new ArrayList<Level>();

        initView();
        initApiCall();
        initAction();

        ApplicationSingleton.getInstance().requestAdMob(this);

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
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
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
        listLevels.clear();


        final Map<String,String> params = new HashMap<String,String>();
        params.put("topic_id", topicId);


        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_GET_LEVEL), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                ModelBase mb = ModelBase.getInstance().setResponse(response);

                if(mb.getStatus().getCode() == 200){

                    listLevels.addAll(mb.getData().getLevel());
                    if(adapter == null){
                        adapter = new LevelAdapter(listLevels);
                        recyclerView.setAdapter(adapter);
                    }

                    if(listLevels.size() <= 0){
                        txtMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtMessage.setVisibility(View.GONE);
                    }

                    Realm realm = ApplicationSingleton.getInstance().getRealm();
                    realm.beginTransaction();

                    RealmTopic realmTopic = null;
                    realmTopic = realm.where(RealmTopic.class).findFirst();
                    if(realmTopic == null){
                        realmTopic = realm.createObject(RealmTopic.class);
                    }

                    RealmList<RealmLevel> listRealmLevel = new RealmList<RealmLevel>();

                    for(int i=0;i<listLevels.size();i++){
                        RealmLevel rl = realm.createObject(RealmLevel.class);

                        rl.setId(listLevels.get(i).getId());
                        rl.setNew(true);
                        rl.setVisitedQuiz(false);

                        listRealmLevel.add(rl);
                        realmTopic.setListLevels(listRealmLevel);
                        realm.copyToRealmOrUpdate(realmTopic);
                    }

                    realm.commitTransaction();

                    adapter.notifyDataSetChanged();

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


    public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.MyViewHolder> {

        private List<Level> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtLevelName;
            TextView txtLevelDetails;
            CardView cardView;
            LinearLayout layoutDescriptionHolder;
            LinearLayout layoutTopProceedHolder;
            ImageView imgNew;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtLevelName = (TextView) itemView.findViewById(R.id.txtLevelName);
                this.txtLevelDetails = (TextView)itemView.findViewById(R.id.txtLevelDetails);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
                this.layoutDescriptionHolder = (LinearLayout)itemView.findViewById(R.id.layoutDescriptionHolder);
                this.layoutTopProceedHolder = (LinearLayout)itemView.findViewById(R.id.layoutTopProceedHolder);
                this.imgNew = (ImageView)itemView.findViewById(R.id.imgNew);
            }
        }

        public LevelAdapter(List<Level> data) {
            this.dataSet = data;
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_level_layout2, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtLevelName = holder.txtLevelName;
            TextView txtLevelDetails = holder.txtLevelDetails;
            CardView cardView = holder.cardView;
            LinearLayout layoutDescriptionHolder = holder.layoutDescriptionHolder;
            LinearLayout layoutTopProceedHolder = holder.layoutTopProceedHolder;
            ImageView imgNew =  holder.imgNew;

            if(TextUtils.isEmpty(dataSet.get(listPosition).getDetails())){
                layoutDescriptionHolder.setVisibility(View.GONE);
                layoutTopProceedHolder.setVisibility(View.VISIBLE);
            }
            else {
                layoutDescriptionHolder.setVisibility(View.VISIBLE);
                layoutTopProceedHolder.setVisibility(View.GONE);
            }

            txtLevelName.setText(dataSet.get(listPosition).getName());
            txtLevelDetails.setText(dataSet.get(listPosition).getDetails());

            Realm realm = ApplicationSingleton.getInstance().getRealm();
            realm.beginTransaction();
            RealmLevel realmLevel = realm.where(RealmLevel.class).equalTo("id", dataSet.get(listPosition).getId()+topicName).findFirst();

            if(realmLevel!=null){
                imgNew.setVisibility(View.INVISIBLE);
                countIsNowTopic++;
            }else{
                imgNew.setVisibility(View.VISIBLE);
            }

            RealmTopic realmTopic = realm.where(RealmTopic.class).findFirst();
            if(countIsNowTopic == dataSet.size()-1){

                realmTopic.setId(dataSet.get(listPosition).getId()+topicName);
            }else {
                realmTopic.setId("");
            }


            realm.commitTransaction();


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LevelRootActivity.this, QuizActivity.class);
                    intent.putExtra(AppConstants.QUIZ_LEVEL_ID, dataSet.get(listPosition).getId());
                    intent.putExtra(AppConstants.QUIZ_LEVEL_NAME, topicName);
                    startActivityForResult(intent, REQUEST_FROM_QUIZ_PAGE);
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FROM_QUIZ_PAGE){
            initApiCall();
        }
    }
}
