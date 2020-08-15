package com.example.liverpooltest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SigletonVolley {
    private static SigletonVolley singleton;
    private RequestQueue requestQueue;
    private static Context context;

    private SigletonVolley(Context context) {
        SigletonVolley.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized SigletonVolley getInstance(Context context) {
        if (singleton == null) {
            singleton = new SigletonVolley(context);
        }
        return singleton;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public  void addToRequestQueue(Request req) {
        getRequestQueue().add(req);
    }

    public void clear_cachep(){
        getRequestQueue().getCache().clear();
    }

    public  void clear_requests() {
        getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

}

