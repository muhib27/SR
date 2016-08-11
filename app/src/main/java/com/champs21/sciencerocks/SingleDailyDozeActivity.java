package com.champs21.sciencerocks;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.champs21.sciencerocks.models.Dailydose;
import com.champs21.sciencerocks.networks.MultiPartStack;
import com.champs21.sciencerocks.networks.MultiPartStringRequest;
import com.champs21.sciencerocks.utils.AppConstants;
import com.champs21.sciencerocks.utils.UrlHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleDailyDozeActivity extends AppCompatActivity {

    private CircularProgressView progressView;
    private TextView txtMessage;
    private List<Dailydose> listItems;
    private WebView webView;
    private int id = 0;
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private Dailydose dailydose = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_single_daily_doze);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(getIntent().getExtras() != null){
            id = Integer.parseInt(getIntent().getExtras().getString(AppConstants.SEARCH_DATA_TO_PAGE).trim());
            Log.e("IDIDIDI", "is: "+id);
        }

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


        initView();
        initApiCall();

    }

    private void initView(){
        webView = (WebView)this.findViewById(R.id.webView);
        progressView = (CircularProgressView)this.findViewById(R.id.progressView);
        progressView.setVisibility(View.GONE);
        txtMessage = (TextView)this.findViewById(R.id.txtMessage);
    }



    private void lodWebViewData(WebView webView, String data){
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_doze, menu);

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
        else if(menuItem.getItemId() == R.id.action_share){

            if(dailydose!=null){
                if (ShareDialog.canShow(ShareLinkContent.class)) {

                    String str = "http://api.champs21.com/api/sciencerocks/sharedaildose?id="+dailydose.getId();
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(getString(R.string.app_name))
                            .setContentUrl(Uri.parse(str))
                            .build();

                    shareDialog.show(linkContent);
                }
            }


        }

        else if(menuItem.getItemId() == R.id.action_download){
            if(dailydose!=null){
                String str = "http://api.champs21.com/api/sciencerocks/Download?id="+dailydose.getId();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(str)));
            }

        }

        return super.onOptionsItemSelected(menuItem);
    }


    private void initApiCall(){

        progressView.setVisibility(View.VISIBLE);
        progressView.startAnimation();

        final Map<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(id));

        MultiPartStringRequest mpr = new MultiPartStringRequest(Request.Method.POST, UrlHelper.newUrl(UrlHelper.URL_SINGLE_DAILY_DOZE), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("*** RESPONSE ***", "is: "+response.toString());
                if(progressView!=null)
                    progressView.setVisibility(View.GONE);

                JSONObject object = response;
                JsonParser jsonParser = new JsonParser();
                JsonObject gsonObject = (JsonObject)jsonParser.parse(object.toString());


                JsonObject jsonObject = gsonObject.get("data").getAsJsonObject().get("dailydose").getAsJsonObject();
                dailydose = new Gson().fromJson(jsonObject.toString(), Dailydose.class);

                if(dailydose == null){
                    txtMessage.setVisibility(View.VISIBLE);
                }
                else{
                    txtMessage.setVisibility(View.GONE);
                }

                lodWebViewData(webView, dailydose.getContent());



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

}
