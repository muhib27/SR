package com.champs21.sciencerocks;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.Dailydose;
import com.champs21.sciencerocks.models.ModelBase;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.CustomWebView;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.github.rahatarmanahmed.cpv.CircularProgressView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyDozeNewActivity extends AppCompatActivity {


    private CircularProgressView progressView;
    private TextView txtMessage;
    private List<Dailydose> listItems;
    private boolean hasNext;
    private int pageNumber = 1;
    private CustomWebView webView;
    private int currentPosition = 0;

    private LinearLayout layoutPreviousHolder;
    private LinearLayout layoutNextHolder;
    private ImageButton btnPrevious;
    private ImageButton btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_doze_new);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listItems = new ArrayList<Dailydose>();
        listItems.clear();

        initView();
        initApiCall();
        //initAction();

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
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);

        webView = (CustomWebView)this.findViewById(R.id.webView);
        webView.setGestureDetector(new GestureDetector(this, new CustomeGestureDetector()));



        layoutPreviousHolder = (LinearLayout)this.findViewById(R.id.layoutPreviousHolder);
        layoutNextHolder = (LinearLayout)this.findViewById(R.id.layoutNextHolder);
        btnPrevious = (ImageButton)this.findViewById(R.id.btnPrevious);
        btnNext = (ImageButton)this.findViewById(R.id.btnNext);

        layoutPreviousHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPrevious.setSelected(true);

                if(currentPosition > 0){
                    currentPosition--;
                    initAction();
                }else {
                    Toast.makeText(DailyDozeNewActivity.this, R.string.daily_doze_new_activity_no_previous_data, Toast.LENGTH_SHORT).show();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnPrevious.setSelected(false);
                    }
                }, 100);
            }
        });

        layoutNextHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnNext.setSelected(true);

                if(currentPosition == listItems.size()){
                    initAction();
                }
                else if(currentPosition < listItems.size()){
                    currentPosition++;
                    initAction();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnNext.setSelected(false);
                    }
                }, 100);
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition > 0){
                    currentPosition--;
                    initAction();
                }else {
                    Toast.makeText(DailyDozeNewActivity.this, R.string.daily_doze_new_activity_no_next_data, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentPosition == listItems.size()){
                    initAction();
                }
                else if(currentPosition < listItems.size()){
                    currentPosition++;
                    initAction();
                }
            }
        });

    }


    private void initAction(){

        //currentPosition++;
        Log.e("CURR_POS", "is: "+currentPosition);
        Log.e("LIST_SIZE", "is: "+listItems.size());
        if(currentPosition == listItems.size() && hasNext == true){
            pageNumber++;
            initApiCall();
        }
        else if (currentPosition == listItems.size() && hasNext == false){
            currentPosition--;
            Toast.makeText(DailyDozeNewActivity.this, R.string.daily_doze_new_activity_no_next_data, Toast.LENGTH_SHORT).show();
            //lodWebViewData(webView, listItems.get(currentPosition).getContent());
        }
        else{
            lodWebViewData(webView, listItems.get(currentPosition).getContent());
        }



    }

    private void lodWebViewData(WebView webView, String data){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

    }


    private void initApiCall(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("page_number", String.valueOf(pageNumber));
        params.put("page_size", "2");

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_DAILY_DOZE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);



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

                    initAction();

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


    private class CustomeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e1 == null || e2 == null) return false;
            if(e1.getPointerCount() > 1 || e2.getPointerCount() > 1) return false;
            else {
                try { // right to left swipe .. go to next page
                    if(e1.getX() - e2.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        if(currentPosition == listItems.size()){
                            initAction();
                        }
                        else if(currentPosition < listItems.size()){
                            currentPosition++;
                            initAction();
                        }
                        return true;
                    } //left to right swipe .. go to prev page
                    else if (e2.getX() - e1.getX() > 100 && Math.abs(velocityX) > 800) {
                        //do your stuff
                        if(currentPosition > 0){
                            currentPosition--;
                            initAction();
                        }else {
                            Toast.makeText(DailyDozeNewActivity.this, R.string.daily_doze_new_activity_no_next_data, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    } //bottom to top, go to next document
                    else if(e1.getY() - e2.getY() > 100 && Math.abs(velocityY) > 800
                            && webView.getScrollY() >= webView.getScale() * (webView.getContentHeight() - webView.getHeight())) {
                        //do your stuff
                        return true;
                    } //top to bottom, go to prev document
                    else if (e2.getY() - e1.getY() > 100 && Math.abs(velocityY) > 800 ) {
                        //do your stuff
                        return true;
                    }
                } catch (Exception e) { // nothing
                }
                return false;
            }
        }
    }



}
