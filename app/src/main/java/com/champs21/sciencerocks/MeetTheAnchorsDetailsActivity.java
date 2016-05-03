package com.champs21.sciencerocks;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.champs21.sciencerocks.utils.AppConstants;

public class MeetTheAnchorsDetailsActivity extends AppCompatActivity {

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imgProfile;
    private TextView txtName;
    private TextView txtAge;
    private TextView txtBioDescription;

    private String selectedAnchor = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet_the_anchors_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Anchor");


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
        imgProfile = (ImageView)this.findViewById(R.id.imgProfile);

        txtName = (TextView)this.findViewById(R.id.txtName);
        txtAge = (TextView)this.findViewById(R.id.txtAge);

        txtBioDescription = (TextView)this.findViewById(R.id.txtBioDescription);
    }

    private void initAction(){

        if(selectedAnchor.equalsIgnoreCase(AppConstants.ANCHOR_MALE)){
            collapsingToolbar.setTitle("Leonardo Dicaprio");
            imgProfile.setImageResource(R.drawable.male_anchor);
            txtName.setText("Leonardo Dicaprio");
            txtAge.setText("28 Years");
        }
        else if(selectedAnchor.equalsIgnoreCase(AppConstants.ANCHOR_FEMALE)){
            collapsingToolbar.setTitle("Anne Hathaway");
            imgProfile.setImageResource(R.drawable.female_anchor);
            txtName.setText("Anne Hathaway");
            txtAge.setText("25 Years");
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
