package com.champs21.sciencerocks;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.networks.CustomVolleyRequestQueue;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.champs21.sciencerocks.youtubemodels.Item;
import com.champs21.sciencerocks.youtubemodels.YoutubeModelBase;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlayListItemsActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener{

    private String playListId = "";

    private RecyclerView recyclerView;
    private PlayListItemAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<Item> listItems;
    private CircularProgressView progressView;
    private SwipyRefreshLayout mSwipyRefreshLayout;
    private String pageToken = "";
    private TextView txtMessage;

    private YouTubePlayerSupportFragment myYouTubePlayerFragment;
    private String vidId = "";

    private static final int RECOVERY_DIALOG_REQUEST = 1;

    YouTubePlayer mYoutubePlayer;

    private boolean isFirstCardCliceked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            playListId = getIntent().getExtras().getString(AppConstants.ID_PLAY_LIST);
            this.setTitle(getIntent().getExtras().getString(AppConstants.ID_PLAY_LIST_ITEM_TITLE));
        }
        else{
            this.setTitle(getString(R.string.title_activity_play_list_items));
        }

        listItems = new ArrayList<Item>();
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
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        mSwipyRefreshLayout = (SwipyRefreshLayout)this.findViewById(R.id.swipyrefreshlayout);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);

        myYouTubePlayerFragment = (YouTubePlayerSupportFragment)getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        myYouTubePlayerFragment.getView().setVisibility(View.GONE);

    }
    private void initAction(){
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.TRANSPARENT)
                        .sizeResId(R.dimen.recyclerViewPlayList_divider)
                        .build());

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.e("MainActivity", "Refresh triggered at " + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                if(!TextUtils.isEmpty(pageToken)){
                    initApiCall();
                }
                else{
                    stopLoader();
                }
            }
        });
    }

    private void initApiCall(){
        if(TextUtils.isEmpty(pageToken)){
            progressView.setVisibility(View.VISIBLE);
            progressView.startAnimation();
        }

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.GET, UrlHelper.getYoutubeUrlPlaylistItems(playListId, pageToken), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                stopLoader();

                YoutubeModelBase mb = YoutubeModelBase.getInstance().setResponse(response);
                pageToken = mb.getPlaylistRoot().getNextPageToken();
                listItems.addAll(mb.getPlaylistRoot().getItems());

                if(listItems.size()<=0){
                    txtMessage.setVisibility(View.VISIBLE);
                }
                else{
                    txtMessage.setVisibility(View.GONE);
                }


                if(adapter==null){
                    adapter = new PlayListItemAdapter(listItems);
                    recyclerView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                stopLoader();
            }
        });

        RequestQueue rq = Volley.newRequestQueue(this, new MultiPartStack());
        rq.add(mpr);

    }

    public class PlayListItemAdapter extends RecyclerView.Adapter<PlayListItemAdapter.MyViewHolder> {

        private List<Item> dataSet;
        private ImageLoader mImageLoader;

        class MyViewHolder extends RecyclerView.ViewHolder {

            NetworkImageView imgViewNetwork;
            TextView txtPlayListTitle;
            TextView txtPlayListDescription;
            LinearLayout layoutDescription;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.imgViewNetwork = (NetworkImageView)itemView.findViewById(R.id.imgViewNetwork);
                this.txtPlayListTitle = (TextView) itemView.findViewById(R.id.txtPlayListTitle);
                this.txtPlayListDescription = (TextView)itemView.findViewById(R.id.txtPlayListDescription);
                this.layoutDescription = (LinearLayout)itemView.findViewById(R.id.layoutDescription);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);
            }
        }

        public PlayListItemAdapter(List<Item> data) {
            this.dataSet = data;
            this.mImageLoader = CustomVolleyRequestQueue.getInstance(PlayListItemsActivity.this).getImageLoader();
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_youtube_play_list_items, parent, false);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            NetworkImageView imgViewNetwork = holder.imgViewNetwork;
            TextView txtPlayListTitle = holder.txtPlayListTitle;
            TextView txtPlayListDescription = holder.txtPlayListDescription;
            LinearLayout layoutDescription = holder.layoutDescription;
            CardView cardView = holder.cardView;

            mImageLoader.get(dataSet.get(listPosition).getSnippet().getThumbnails().getDefault().getUrl(), ImageLoader.getImageListener(imgViewNetwork,
                    R.drawable.youtube_default, android.R.drawable
                            .ic_dialog_alert));
            imgViewNetwork.setImageUrl(dataSet.get(listPosition).getSnippet().getThumbnails().getDefault().getUrl(), mImageLoader);

            txtPlayListTitle.setText(dataSet.get(listPosition).getSnippet().getTitle());
            if(TextUtils.isEmpty(dataSet.get(listPosition).getSnippet().getDescription())){
                layoutDescription.setVisibility(View.GONE);
            }
            else{
                layoutDescription.setVisibility(View.VISIBLE);
                txtPlayListDescription.setText(dataSet.get(listPosition).getSnippet().getDescription());
            }

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(PlayListItemsActivity.this, PlayerActivity.class);
                    //intent.putExtra(AppConstants.ID_VIDEO, dataSet.get(listPosition).getSnippet().getResourceId().getVideoId());
                    //startActivity(intent);
                    Log.e("CARD_CLICKED", "done");
                    vidId = dataSet.get(listPosition).getSnippet().getResourceId().getVideoId();

                    if(isFirstCardCliceked == false){
                        myYouTubePlayerFragment.getView().setVisibility(View.VISIBLE);
                        myYouTubePlayerFragment.initialize(AppConstants.DEV_KEY, PlayListItemsActivity.this);
                        isFirstCardCliceked = true;
                    }else{
                        mYoutubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        mYoutubePlayer.loadVideo(vidId);
                        mYoutubePlayer.play();
                    }

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
                if (PlayListItemsActivity.this != null) {
                    PlayListItemsActivity.this.runOnUiThread(new Runnable() {

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

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    "There was an error initializing the YouTubePlayer (%1$s)",
                    errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(vidId);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            mYoutubePlayer = player;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(AppConstants.DEV_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView)findViewById(R.id.youtube_fragment);
    }
}
