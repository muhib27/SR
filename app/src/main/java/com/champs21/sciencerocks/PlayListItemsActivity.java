package com.champs21.sciencerocks;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.ImageButton;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
import java.util.LinkedList;
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

    private YouTubePlayer mYoutubePlayer = null;

    private boolean isFirstCardCliceked = false;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private String funnyVideoPlayListId = "";
    private String title = "Videos";
    private AppCompatButton btnWinnerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_play_list_items);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras()!=null){
            funnyVideoPlayListId = getIntent().getExtras().getString(AppConstants.KEY_FUNNY_VIDEOS);
            if(TextUtils.isEmpty(funnyVideoPlayListId)){
                playListId = getIntent().getExtras().getString(AppConstants.ID_PLAY_LIST);
                this.setTitle(title);
            }else {
                playListId = funnyVideoPlayListId;
                this.setTitle(title);
            }

            title = getIntent().getExtras().getString(AppConstants.PLAY_LIST_NAME);
            this.setTitle(title);
        }
        else{
            this.setTitle(getString(R.string.title_activity_play_list_items));
        }

        listItems = new ArrayList<Item>();
        listItems.clear();

        initView();
        initApiCall();
        initAction();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                //Toast.makeText(ResultPageActivity.this, "Successfully posted!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                //Toast.makeText(ResultPageActivity.this, "Something went wrong, try to post later!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        /*if(playListId.equals(AppConstants.PALYLIST_ID_ROCKING_EXP)){
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if(mYoutubePlayer!=null){
                    getSupportActionBar().hide();
                    btnWinnerList.setVisibility(View.GONE);
                }

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                getSupportActionBar().show();
                btnWinnerList.setVisibility(View.VISIBLE);
            }
        }else{
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

                if(mYoutubePlayer!=null){
                    getSupportActionBar().hide();
                    btnWinnerList.setVisibility(View.GONE);
                }

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
                getSupportActionBar().show();
                btnWinnerList.setVisibility(View.GONE);
            }
        }*/

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            if(mYoutubePlayer != null){
                mYoutubePlayer.setFullscreen(true);

                getSupportActionBar().hide();
                btnWinnerList.setVisibility(View.GONE);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            if(mYoutubePlayer != null){
                mYoutubePlayer.setFullscreen(false);

                getSupportActionBar().show();
                if(playListId.equals(AppConstants.PALYLIST_ID_ROCKING_EXP))
                    btnWinnerList.setVisibility(View.VISIBLE);
                else
                    btnWinnerList.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_videos, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) PlayListItemsActivity.this.getSystemService(Context.SEARCH_SERVICE);

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
        }
        else if(menuItem.getItemId() == R.id.action_search){

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void initView(){
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        mSwipyRefreshLayout = (SwipyRefreshLayout)this.findViewById(R.id.swipyrefreshlayout);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);

        myYouTubePlayerFragment = (YouTubePlayerSupportFragment)getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        myYouTubePlayerFragment.getView().setVisibility(View.GONE);
        btnWinnerList = (AppCompatButton)this.findViewById(R.id.btnWinnerList);

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


        if(playListId.equals(AppConstants.PALYLIST_ID_ROCKING_EXP)){
            btnWinnerList.setVisibility(View.VISIBLE);
            btnWinnerList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(PlayListItemsActivity.this, WinnerListRootActivity.class);
                    startActivity(intent);
                }
            });

        }else {
            btnWinnerList.setVisibility(View.GONE);
        }

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

    public class PlayListItemAdapter extends RecyclerView.Adapter<PlayListItemAdapter.MyViewHolder> implements Filterable {

        private List<Item> dataSet;
        private ImageLoader mImageLoader;

        class MyViewHolder extends RecyclerView.ViewHolder {

            NetworkImageView imgViewNetwork;
            TextView txtPlayListTitle;
            TextView txtPlayListDescription;
            LinearLayout layoutDescription;
            CardView cardView;
            ImageButton imgBtnShare;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.imgViewNetwork = (NetworkImageView)itemView.findViewById(R.id.imgViewNetwork);
                this.txtPlayListTitle = (TextView) itemView.findViewById(R.id.txtPlayListTitle);
                this.txtPlayListDescription = (TextView)itemView.findViewById(R.id.txtPlayListDescription);
                this.layoutDescription = (LinearLayout)itemView.findViewById(R.id.layoutDescription);
                this.imgBtnShare = (ImageButton)itemView.findViewById(R.id.imgBtnShare);
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
            ImageButton imgBtnShare = holder.imgBtnShare;
            CardView cardView = holder.cardView;

            mImageLoader.get(dataSet.get(listPosition).getSnippet().getThumbnails().getDefault().getUrl(), ImageLoader.getImageListener(imgViewNetwork,
                    R.drawable.youtube_default, android.R.drawable
                            .ic_dialog_alert));
            imgViewNetwork.setImageUrl(dataSet.get(listPosition).getSnippet().getThumbnails().getDefault().getUrl(), mImageLoader);

            txtPlayListTitle.setText(dataSet.get(listPosition).getSnippet().getTitle());
            /*if(TextUtils.isEmpty(dataSet.get(listPosition).getSnippet().getDescription())){
                layoutDescription.setVisibility(View.GONE);
            }
            else{
                layoutDescription.setVisibility(View.VISIBLE);
                txtPlayListDescription.setText(dataSet.get(listPosition).getSnippet().getDescription());
            }*/

            layoutDescription.setVisibility(View.GONE);

            imgBtnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(getString(R.string.app_name))
                                .setContentUrl(Uri.parse("https://www.youtube.com/watch?v="+dataSet.get(listPosition).getSnippet().getResourceId().getVideoId()))
                                .build();

                        shareDialog.show(linkContent);
                    }
                }
            });

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

        private UserFilter userFilter;

        @Override
        public Filter getFilter() {
            if(userFilter == null)
                userFilter = new UserFilter(this, dataSet);
            return userFilter;
        }

        private  class UserFilter extends Filter {

            private final PlayListItemAdapter adapter;

            private final List<Item> originalList;

            private final List<Item> filteredList;

            private UserFilter(PlayListItemAdapter adapter, List<Item> originalList) {
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

                    for (final Item user : originalList) {

                        if (user.getSnippet().getTitle().toLowerCase().contains(filterPattern.toLowerCase())) {
                        filteredList.add(user);
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
                adapter.dataSet.addAll((ArrayList<Item>) results.values);
                adapter.notifyDataSetChanged();
            }
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
            player.loadVideo(vidId);
            player.play();
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
