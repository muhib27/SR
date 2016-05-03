package com.champs21.sciencerocks.networks;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by BLACK HAT on 29-Feb-16.
 */
public class VolleyHelper {
    private static VolleyHelper INSTANCE;
    private RequestQueue requestQueue;
    private Context context;

    private VolleyHelper(Context context){
        this.context = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized VolleyHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new VolleyHelper(context);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public RequestQueue getRequestQueue(MultiPartStack stack){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext(), stack);
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, MultiPartStack stack) {
        getRequestQueue(stack).add(req);
    }
}
