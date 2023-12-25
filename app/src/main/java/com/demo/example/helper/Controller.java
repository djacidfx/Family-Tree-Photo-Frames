package com.demo.example.helper;

import android.app.Application;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class Controller extends Application {
    private static final String PROPERTY_ID = "UA-104331859-1";
    public static final String TAG = "Controller";
    private static Controller mInstance;
    private ImageLoader mImageLoader;
    private RequestQueue mRequestQueue;

    
    public enum TrackerName {
        APP_TRACKER,
        GLOBAL_TRACKER,
        ECOMMERCE_TRACKER
    }



    @Override 
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        MultiDex.install(this);
    }

    public static synchronized Controller getInstance() {
        Controller controller;
        synchronized (Controller.class) {
            controller = mInstance;
        }
        return controller;
    }

    public RequestQueue getRequestQueue() {
        if (this.mRequestQueue == null) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return this.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String str) {
        if (TextUtils.isEmpty(str)) {
            str = TAG;
        }
        request.setTag(str);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object obj) {
        RequestQueue requestQueue = this.mRequestQueue;
        if (requestQueue != null) {
            requestQueue.cancelAll(obj);
        }
    }
}
