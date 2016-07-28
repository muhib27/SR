package com.champs21.sciencerocks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.champs21.sciencerocks.app.ApplicationSingleton;
import com.champs21.sciencerocks.utils.AppConstants;

public class MeetTheAnchorsRootActivity extends AppCompatActivity {


    private ImageView imgViewMale;
    private ImageView imgViewFemale;
    private TextView txtImgViewMale;
    private TextView txtImgViewFemale;
    private TextView txtDescription;

    private RelativeLayout layoutMaleHolder;
    private RelativeLayout layoutFemaleHolder;

    private AppCompatButton btnQA;
    private LinearLayout layoutRootMale;
    private LinearLayout layoutRootFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_the_anchors_root);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        initView();
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
        layoutMaleHolder = (RelativeLayout)this.findViewById(R.id.layoutMaleHolder);
        layoutFemaleHolder = (RelativeLayout)this.findViewById(R.id.layoutFemaleHolder);

        imgViewMale = (ImageView)this.findViewById(R.id.imgViewMale);
        imgViewFemale = (ImageView)this.findViewById(R.id.imgViewFemale);

        txtImgViewMale = (TextView)this.findViewById(R.id.txtImgViewMale);
        txtImgViewFemale = (TextView)this.findViewById(R.id.txtImgViewFemale);

        txtDescription = (TextView)this.findViewById(R.id.txtDescription);
        btnQA = (AppCompatButton)this.findViewById(R.id.btnQA);

        layoutRootMale = (LinearLayout)this.findViewById(R.id.layoutRootMale);
        layoutRootFemale = (LinearLayout)this.findViewById(R.id.layoutRootFemale);
    }

    private void initAction(){
        layoutMaleHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetTheAnchorsRootActivity.this, MeetTheAnchorsDetailsActivity.class);
                intent.putExtra(AppConstants.SELECTED_ANCHOR, AppConstants.ANCHOR_MALE);
                startActivity(intent);

            }
        });

        layoutRootMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetTheAnchorsRootActivity.this, MeetTheAnchorsDetailsActivity.class);
                intent.putExtra(AppConstants.SELECTED_ANCHOR, AppConstants.ANCHOR_MALE);
                startActivity(intent);
            }
        });

        layoutFemaleHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetTheAnchorsRootActivity.this, MeetTheAnchorsDetailsActivity.class);
                intent.putExtra(AppConstants.SELECTED_ANCHOR, AppConstants.ANCHOR_FEMALE);
                startActivity(intent);
            }
        });

        layoutRootFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MeetTheAnchorsRootActivity.this, MeetTheAnchorsDetailsActivity.class);
                intent.putExtra(AppConstants.SELECTED_ANCHOR, AppConstants.ANCHOR_FEMALE);
                startActivity(intent);
            }
        });



        btnQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ApplicationSingleton.getInstance().isNetworkConnected() == true) {

                    Intent intent = new Intent(MeetTheAnchorsRootActivity.this, QaRootActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MeetTheAnchorsRootActivity.this, R.string.toast_no_internet, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}
