package com.champs21.sciencerocks;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.models.Topic;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.realm.RealmTopic;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.realm.Realm;

public class TopicRootActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private CircularProgressView progressView;
    private TextView txtMessage;
    private TopicAdapter adapter;
    private List<Topic> listTopic;
    private boolean isNew = true;
    private static final int REQUEST_FROM_QUIZ_PAGE = 457;

    private String currentLanguage = AppConstants.LANG_BN;
    private boolean isFlagIconClickable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listTopic = new ArrayList<Topic>();

        if(TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER))){
            currentLanguage = AppConstants.LANG_BN;
            ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_BN);
        }else{
            currentLanguage = ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER);
        }

        initView();
        initApiCall();
        initAction();



        ApplicationSingleton.getInstance().requestAdMob(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lang, menu);

        if(currentLanguage.equals(AppConstants.LANG_BN)){
            menu.getItem(0).setIcon(R.drawable.icon_en_lang);
        }else{
            menu.getItem(0).setIcon(R.drawable.icon_bn_lang);
        }

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) TopicRootActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

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
        }else if(menuItem.getItemId() == R.id.action_lang){

            if(isFlagIconClickable == true){
                if(currentLanguage.equals(AppConstants.LANG_BN)){
                    ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_EN);
                    menuItem.setIcon(R.drawable.icon_bn_lang);
                }else{
                    ApplicationSingleton.getInstance().savePrefString(AppConstants.LANG_IDENTIFIER, AppConstants.LANG_BN);
                    menuItem.setIcon(R.drawable.icon_en_lang);
                }

                currentLanguage = ApplicationSingleton.getInstance().getPrefString(AppConstants.LANG_IDENTIFIER);
                if(adapter!=null)
                    adapter.refresh();
            }

        }
        else if(menuItem.getItemId() == R.id.action_search){

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

                    isFlagIconClickable = true;

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


    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> implements Filterable {

        private List<Topic> dataSet;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtTopicTitle;
            TextView txtTopicDetails;
            CardView cardView;
            LinearLayout layoutDescriptionHolder;
            LinearLayout layoutTopProceedHolder;
            ImageView imgNew;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtTopicTitle = (TextView) itemView.findViewById(R.id.txtTopicTitle);
                this.txtTopicDetails = (TextView)itemView.findViewById(R.id.txtTopicDetails);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
                this.layoutDescriptionHolder = (LinearLayout)itemView.findViewById(R.id.layoutDescriptionHolder);
                this.layoutTopProceedHolder = (LinearLayout)itemView.findViewById(R.id.layoutTopProceedHolder);
                this.imgNew = (ImageView)itemView.findViewById(R.id.imgNew);
            }
        }

        public TopicAdapter(List<Topic> data) {
            this.dataSet = data;
        }

        public void refresh(){
            notifyDataSetChanged();
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
            ImageView imgNew = holder.imgNew;


            /*if(TextUtils.isEmpty(dataSet.get(listPosition).getDetails())){
                layoutDescriptionHolder.setVisibility(View.GONE);
                layoutTopProceedHolder.setVisibility(View.VISIBLE);
            }
            else {
                layoutDescriptionHolder.setVisibility(View.VISIBLE);
                layoutTopProceedHolder.setVisibility(View.GONE);
            }*/

            layoutDescriptionHolder.setVisibility(View.GONE);
            layoutTopProceedHolder.setVisibility(View.VISIBLE);

            if(currentLanguage.equals(AppConstants.LANG_EN)){
                txtTopicTitle.setText(dataSet.get(listPosition).getEnName());
            }else{
                txtTopicTitle.setText(dataSet.get(listPosition).getName());
            }

            txtTopicDetails.setText(dataSet.get(listPosition).getDetails());


            Realm realm = ApplicationSingleton.getInstance().getRealm();
            realm.beginTransaction();

            RealmTopic realmTopic = null;
            realmTopic = realm.where(RealmTopic.class).equalTo("id", dataSet.get(listPosition).getId()).findFirst();
            if(realmTopic == null){
                realmTopic = realm.createObject(RealmTopic.class);
                realmTopic.setId(dataSet.get(listPosition).getId());
                realmTopic.setNew(true);
            }

            if(realmTopic.isNew()){
                imgNew.setVisibility(View.VISIBLE);
            }else{
                imgNew.setVisibility(View.INVISIBLE);
            }

            realm.commitTransaction();

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(TopicRootActivity.this, LevelRootActivity.class);
                    intent.putExtra(AppConstants.QUIZ_TOPIC_ID, dataSet.get(listPosition).getId());
                    if(currentLanguage.equals(AppConstants.LANG_EN)){
                        intent.putExtra(AppConstants.QUIZ_LEVEL_NAME, dataSet.get(listPosition).getEnName());
                    }else{
                        intent.putExtra(AppConstants.QUIZ_LEVEL_NAME, dataSet.get(listPosition).getName());
                    }

                    startActivityForResult(intent, REQUEST_FROM_QUIZ_PAGE);
                }
            });


        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        private UserFilter userFilter;

        @Override
        public Filter getFilter() {
            if(userFilter == null)
                userFilter = new UserFilter(this, dataSet);
            return userFilter;
        }

        private  class UserFilter extends Filter {

            private final TopicAdapter adapter;

            private final List<Topic> originalList;

            private final List<Topic> filteredList;

            private UserFilter(TopicAdapter adapter, List<Topic> originalList) {
                super();
                this.adapter = adapter;
                this.originalList = new LinkedList<>(originalList);
                this.filteredList = new ArrayList<>();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                final FilterResults results = new FilterResults();

                if (constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    final String filterPattern = constraint.toString().toLowerCase().trim();

                    for (final Topic user : originalList) {

                        if(currentLanguage.equals(AppConstants.LANG_EN)){

                            if (user.getEnName().toLowerCase().contains(filterPattern.toLowerCase())) {
                                filteredList.add(user);
                            }
                        }else{
                            if (user.getName().toLowerCase().contains(filterPattern.toLowerCase())) {
                                filteredList.add(user);
                            }
                        }

                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                adapter.dataSet.clear();
                adapter.dataSet.addAll((ArrayList<Topic>) results.values);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FROM_QUIZ_PAGE){
            //initApiCall();
            Intent intent = getIntent();
            finish();
            startActivity(intent);

        }
    }
}
