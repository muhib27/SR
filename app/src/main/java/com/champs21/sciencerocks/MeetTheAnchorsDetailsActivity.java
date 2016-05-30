package com.champs21.sciencerocks;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.ProportionalImageView;

public class MeetTheAnchorsDetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private ProportionalImageView imgProfile;
    //private TextView txtName;
    private TextView txtAge;
    //private TextView txtBioDescription;

    private String selectedAnchor = "";
    private TextView txtJobTitle;
    private TextView txtBioDetails;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_the_anchors_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.mta_anchor));


        /*CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mCustomPagerAdapter);*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().getExtras() != null){
            selectedAnchor = getIntent().getExtras().getString(AppConstants.SELECTED_ANCHOR);

        }

        initView();
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
        imgProfile = (ProportionalImageView)this.findViewById(R.id.imgProfile);
        txtAge = (TextView)this.findViewById(R.id.txtAge);
        txtJobTitle = (TextView)this.findViewById(R.id.txtJobTitle);
        txtBioDetails = (TextView)this.findViewById(R.id.txtBioDetails);
    }

    private void initAction(){

        if(selectedAnchor.equalsIgnoreCase(AppConstants.ANCHOR_MALE)){
            collapsingToolbar.setTitle(getString(R.string.mta_male_anchor_name));
            imgProfile.setImageResource(R.drawable.male_anch);
            txtAge.setText(R.string.mta_male_anchor_age);
            txtJobTitle.setText(R.string.mta_male_anchor_job_title);
            txtBioDetails.setText(R.string.mta_bio_male_details);
        }
        else if(selectedAnchor.equalsIgnoreCase(AppConstants.ANCHOR_FEMALE)){
            collapsingToolbar.setTitle(getString(R.string.mta_female_anchor_name));
            imgProfile.setImageResource(R.drawable.female_anch);
            txtAge.setText(R.string.mta_female_anchor_age);
            txtJobTitle.setText(R.string.mta_female_anchor_job_title);
            txtBioDetails.setText(R.string.mta_bio_female_details);
        }

    }

    /*int[] mResources = {
            R.drawable.male_anchor,
            R.drawable.male_anchor,
            R.drawable.male_anchor,
            R.drawable.male_anchor,
            R.drawable.male_anchor,
            R.drawable.male_anchor
    };

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.anchor_pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }*/

}
