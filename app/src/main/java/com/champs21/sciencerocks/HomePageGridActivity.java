package com.champs21.sciencerocks;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.app.MySuggestionProvider;
import com.champs21.sciencerocks.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

public class HomePageGridActivity extends AppCompatActivity {//implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUST_TOPIC_ACTIVITY = 10;
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private List<String> listData;
    private GridLayoutManager layoutManager;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private boolean isPermissionGranted = false;
    private ImageView imgMainLogo;

    private ScrollView scrollView;
    //private NavigationView navigationView;
    private boolean isSoundOff = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*boolean theme = ApplicationSingleton.getInstance().getPrefBoolean("pref_skin1");
        if(theme){
            this.setTheme(R.style.AppTheme_Skin1);
        }
        else {
            this.setTheme(R.style.AppTheme);
        }*/

        setContentView(R.layout.activity_drawer_home_page_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/



        listData = new ArrayList<String>();
        //listData.add(getString(R.string.home_page_grid_daily_doze));
        //listData.add(getString(R.string.home_page_grid_episodes));
        //listData.add(getString(R.string.home_page_grid_quiz));
        //listData.add(getString(R.string.home_page_grid_mta));
        listData.add(getString(R.string.home_page_grid_rocking_experiments));
        listData.add(getString(R.string.home_page_grid_funny_videos));
        listData.add(getString(R.string.home_page_grid_quiz));
        listData.add(getString(R.string.home_page_grid_daily_doze));


        initView();
        initAction();

        /*if(ApplicationSingleton.getInstance().getPrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE) == true){
            navigationView.getMenu().findItem(R.id.nav_music).setIcon(R.drawable.ic_volume_off_black_24dp);
            isSoundOff = true;
            //navigationView.getMenu().getItem(0).setChecked(true);
        }else {
            isSoundOff = false;
            navigationView.getMenu().findItem(R.id.nav_music).setIcon(R.drawable.ic_volume_up_black_24dp);
            //navigationView.getMenu().getItem(0).setChecked(false);
        }

        View headerLayout = navigationView.getHeaderView(0);
        TextView txtUserName = (TextView)headerLayout.findViewById(R.id.txtUserName);
        if(!TextUtils.isEmpty(ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME))){
            txtUserName.setText(ApplicationSingleton.getInstance().getPrefString(AppConstants.GOOGLE_AUTH_DISPLAY_NAME));
        }*/

        Log.e("KEY_HASH", ApplicationSingleton.getInstance().printHashKey(getApplicationContext()));


        //ApplicationSingleton.getInstance().requestAdMob(this);

        Intent intent  = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);

            Intent intent1 = new Intent(this, SearchResultActivity.class);
            intent1.putExtra(AppConstants.SEARCH_STRING, query);
            startActivity(intent1);

            finish();

        }
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_home_page_grid, menu);

       MenuItem searchItem = menu.findItem(R.id.action_search);
       SearchManager searchManager = (SearchManager) HomePageGridActivity.this.getSystemService(Context.SEARCH_SERVICE);

       SearchView searchView = null;
       if (searchItem != null) {
           searchView = (SearchView) searchItem.getActionView();
       }
       if (searchView != null) {
           searchView.setSearchableInfo(searchManager.getSearchableInfo(HomePageGridActivity.this.getComponentName()));
       }


       return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
            if(imgMainLogo.getVisibility() == View.VISIBLE){
                imgMainLogo.setVisibility(View.GONE);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
            if(imgMainLogo.getVisibility() != View.VISIBLE){
                imgMainLogo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        else if(menuItem.getItemId() == R.id.action_about_sr){
            Intent intent = new Intent(HomePageGridActivity.this, AboutScienceRocksActivity.class);
            startActivity(intent);
        }
        else if(menuItem.getItemId() == R.id.action_banner_page){
            showBannerPopup();
        }
        else if(menuItem.getItemId() == R.id.action_search){

        }
        /*else if (menuItem.getItemId() == R.id.action_skin_choose) {
            Intent intent = new Intent(HomePageGridActivity.this, AppPreferences.class);
            startActivity(intent);
        }*/
        /*else if(menuItem.getItemId() == R.id.action_banner_page) {

        }*/

        return super.onOptionsItemSelected(menuItem);
    }


    private void showBannerPopup(){


        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .customView(R.layout.popup_layout_banner, false)
                .build();
        View dialogView = dialog.getCustomView();

        Button btnClose = (Button) dialogView.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });


        dialog.show();
    }



    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_music) {
            // Handle the camera action
            isSoundOff = !isSoundOff;

            if(isSoundOff){
                navigationView.getMenu().findItem(R.id.nav_music).setIcon(R.drawable.ic_volume_off_black_24dp);
                ApplicationSingleton.getInstance().savePrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE, true);
                //navigationView.getMenu().getItem(0).setChecked(true);
            }else{
                navigationView.getMenu().findItem(R.id.nav_music).setIcon(R.drawable.ic_volume_up_black_24dp);
                ApplicationSingleton.getInstance().savePrefBoolean(AppConstants.QUIZ_MUSIC_TOGGLE, false);
                //navigationView.getMenu().getItem(0).setChecked(false);
            }

        }else if(id == R.id.nav_about){
            Intent intent = new Intent(HomePageGridActivity.this, AboutScienceRocksActivity.class);
            startActivity(intent);
        }

        *//*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*//*

        return true;
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initView(){
        recyclerView = (RecyclerView)this.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        imgMainLogo = (ImageView)this.findViewById(R.id.imgMainLogo);

        scrollView = (ScrollView)this.findViewById(R.id.scrollView);

    }
    private void initAction(){
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new CustomAdapter(listData);
        recyclerView.setAdapter(adapter);

        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        private List<String> dataSet;
        private int lastPosition = -1;

        private AnimatorSet mSetRightOut;
        private AnimatorSet mSetLeftIn;
        private AnimatorSet setRightOut;
        private AnimatorSet setLeftIn;
        private boolean isBackVisible = false;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtTitle;
            LinearLayout layoutTop;
            CardView cardView;


            public MyViewHolder(View itemView) {
                super(itemView);
                this.txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
                this.layoutTop = (LinearLayout)itemView.findViewById(R.id.layoutTop);
                this.cardView = (CardView)itemView.findViewById(R.id.cardView);

            }
        }

        public CustomAdapter(List<String> data) {
            this.dataSet = data;
            this.setRightOut = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flight_right_out);
            this.setLeftIn = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flight_left_in);
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {

            MyViewHolder myViewHolder = null;
            if(viewType==0){
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_homepage_grid_daily_doze, parent, false);

                myViewHolder = new MyViewHolder(view);

            }
            else{
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_homepage_grid, parent, false);

                myViewHolder = new MyViewHolder(view);
            }
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView txtTitle = holder.txtTitle;
            LinearLayout layoutTop = holder.layoutTop;
            CardView cardView = holder.cardView;

            txtTitle.setText(dataSet.get(listPosition));

            switch (listPosition){
                case 0:
                    layoutTop.setBackgroundResource(R.drawable.grid_1);
                    break;
                case 1:
                    layoutTop.setBackgroundResource(R.drawable.grid_2);
                    break;
                case 2:
                    layoutTop.setBackgroundResource(R.drawable.grid_3);
                    break;
                case 3:
                    layoutTop.setBackgroundResource(R.drawable.grid_4);
                    break;
            }


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Handler h = new Handler();
                    final View v = view;
                    v.setEnabled(false);
                    //quiz
                    if(listPosition == 0){

                        if(ApplicationSingleton.getInstance().isNetworkConnected() == true) {
                            //Intent intent = new Intent(HomePageGridActivity.this, DailyDozeActivity.class);

                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(HomePageGridActivity.this, PlayListItemsActivity.class);
                                    intent.putExtra(AppConstants.KEY_FUNNY_VIDEOS, AppConstants.PALYLIST_ID_ROCKING_EXP);
                                    intent.putExtra(AppConstants.PLAY_LIST_NAME, getString(R.string.title_activity_play_list));
                                    startActivity(intent);
                                }
                            }, 500);


                        }
                        else {
                            Toast.makeText(HomePageGridActivity.this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                        }

                    }
                    //episodes
                    else if(listPosition == 1){
                        if(ApplicationSingleton.getInstance().isNetworkConnected() == true) {

                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(HomePageGridActivity.this, PlayListItemsActivity.class);
                                    intent.putExtra(AppConstants.KEY_FUNNY_VIDEOS, AppConstants.PALYLIST_ID_FUNNY_VIDEOS);
                                    intent.putExtra(AppConstants.PLAY_LIST_NAME, getString(R.string.home_page_grid_funny_videos));
                                    startActivity(intent);
                                    v.setEnabled(true);
                                }
                            }, 500);


                        }
                        else {
                            Toast.makeText(HomePageGridActivity.this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                        }
                    }

                    //daily doze
                    else if(listPosition == 2){

                        if(ApplicationSingleton.getInstance().isNetworkConnected() == true){

                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(HomePageGridActivity.this, TopicRootActivity.class);
                                    startActivityForResult(intent, REQUST_TOPIC_ACTIVITY);
                                    v.setEnabled(true);
                                }
                            }, 500);

                        }
                        else {
                            Toast.makeText(HomePageGridActivity.this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                        }



                    }
                    //meet the anchors
                    else if(listPosition == 3){

                        if(ApplicationSingleton.getInstance().isNetworkConnected() == true) {
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(HomePageGridActivity.this, DailyDozeNewActivity.class);
                                    startActivity(intent);
                                    v.setEnabled(true);
                                }
                            }, 500);
                        }
                        else {
                            Toast.makeText(HomePageGridActivity.this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                        }


                    }

                    showFlipAnimation(view);

                }
            });

            //setAnimation(holder.cardView, listPosition);
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }


        private void setAnimation(View viewToAnimate, int position) {
            if (position > lastPosition) {
                Animation animation = AnimationUtils.loadAnimation(HomePageGridActivity.this, android.R.anim.slide_in_left);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        private void showFlipAnimation(View view){
            if(!isBackVisible){
                setRightOut.setTarget(view);
                setLeftIn.setTarget(view);
                setRightOut.start();
                setLeftIn.start();
                isBackVisible = true;
            }
            else{
                setRightOut.setTarget(view);
                setLeftIn.setTarget(view);
                setRightOut.start();
                setLeftIn.start();
                isBackVisible = false;
            }
            view.setEnabled(true);
        }
    }

    private  boolean checkAndRequestPermissions() {
        int permissionInternet = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionAccessNetState = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionInternet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (permissionAccessNetState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUST_TOPIC_ACTIVITY) {
            finish();
            startActivity(getIntent());
        }
    }

}
