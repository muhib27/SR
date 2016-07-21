package com.champs21.sciencerocks.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.champs21.sciencerocks.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by BLACK HAT on 17-Apr-16.
 */
public class ApplicationSingleton extends MultiDexApplication {

    private static ApplicationSingleton sInstance;
    private SharedPreferences mPref;

    public static ApplicationSingleton getInstance() {
        return sInstance;
    }

    @Override protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();

        //CookieManager cookieManager = new CookieManager(new PersistentCookieStore(this), CookiePolicy.ACCEPT_ORIGINAL_SERVER);
        //CookieHandler.setDefault(cookieManager);

    }

    private void initializeInstance() {

        // Do your application wise initialization task
        // set application wise preference
        mPref = this.getApplicationContext().getSharedPreferences("science_rocks_pref_key", MODE_PRIVATE);
    }

    public void savePrefString(String key, String value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public String getPrefString(String key){
        return mPref.getString(key, "");
    }

    public void savePrefBoolean(String key, boolean value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public boolean getPrefBoolean(String key){
        return mPref.getBoolean(key, false);
    }


    public void printUrlParams(Map params){
        Iterator myVeryOwnIterator = params.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            String key=(String)myVeryOwnIterator.next();
            String value=(String)params.get(key);
            Log.e("URL_PARAMS", "key: " + key);
            Log.e("URL_PARAMS", "value: "+value);
            Log.e("URL_PARAMS", "---------------------------------------------------------");
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public String printHashKey(Context pContext) {
        String hashKey = "";
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                hashKey = new String(Base64.encode(md.digest(), 0));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("KEY_HASH", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("KEY_HASH", "printHashKey()", e);
        }
        return  hashKey;
    }

    public void requestAdMob(Activity activity){
        AdView mAdView = (AdView) activity.findViewById(R.id.adView);

        if(isNetworkConnected()){
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }
        else{
            mAdView.setVisibility(View.GONE);
        }

    }

    public Realm getRealm(){
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();

        return realm;
    }

}