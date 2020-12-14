package com.utexas.activityrecognition.api.impl;

import android.content.Context;
import android.util.Log;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.api.RecognitionAPI;
import com.utexas.activityrecognition.data.RecognitionCookieStore;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class RecogitionAPIImpl implements RecognitionAPI {
    final static String TAG = RecogitionAPIImpl.class.getCanonicalName();
    static RecogitionAPIImpl  Instance = new RecogitionAPIImpl();

    public static RecogitionAPIImpl Instance () {
        return Instance;
    }

    @Override
    public boolean Login(Context context, String username, String password) {
        CallerThread callerThread = new CallerThread(context);
        callerThread.setResponseBody("username=" + username + "&password=" + password);
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_login));
        try {
            callerThread.start();
            callerThread.join();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return (callerThread.getResult() && callerThread.responseBody.equals("Logged in"));
    }

    @Override
    public boolean Logout(Context context) {
        CallerThread callerThread = new CallerThread(context);
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_logout));
        try {
            callerThread.start();
            callerThread.join();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return (callerThread.getResult() && callerThread.responseBody.equals("Logged Out"));
    }

    @Override
    public boolean Register(Context context, String username, String password) {
        CallerThread callerThread = new CallerThread(context);
        callerThread.setResponseBody("username=" + username + "&password=" + password);
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_register));
        try {
            callerThread.start();
            callerThread.join();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return (callerThread.getResult() && callerThread.responseBody.equals("Registered"));
    }

    @Override
    public boolean ConnectImgSocket(Context context) {
        return false;
    }

    @Override
    public boolean GetInferences(Context context) {
        return false;
    }

    private class CallerThread extends Thread {
        String url;
        String responseBody;
        boolean success;
        CookieStore cookieStore;
        Context context;

        public CallerThread(Context context) {
            cookieStore = new RecognitionCookieStore(context);
            this.context = context;
        }

        @Override
        public void run() {
            success = true;
            try {
                URL connectUrl = new URL(url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) connectUrl.openConnection();
                httpURLConnection.setInstanceFollowRedirects(false);
                HttpURLConnection.setFollowRedirects(false);
                httpURLConnection.setRequestMethod("POST");
                String cookies="";
                for(HttpCookie cookie:cookieStore.get(new URI(context.getResources().getString(R.string.base_url))))
                    cookies+=cookie+"; ";
                httpURLConnection.setRequestProperty("Cookie",cookies);
                DataOutputStream output = new DataOutputStream(httpURLConnection.getOutputStream());
                output.writeBytes(responseBody);
                output.flush();
                output.close();
                int status = httpURLConnection.getResponseCode();
                Log.e(TAG, String.valueOf(status));
                if (status != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Invalid status " + status);
                    success = false;
                }
                List<String> cookielist = httpURLConnection.getHeaderFields().get("set-cookie");
                for(String item: cookielist) {
                    Log.e(TAG, item);
                }
                if(cookielist != null)
                    for(String cookieDetail : cookielist)
                        cookieStore.add(new URI(context.getResources().getString(R.string.base_url)),HttpCookie.parse(cookieDetail).get(0));
                byte[] b = new byte[1024*1024];
                int len = (new DataInputStream(httpURLConnection.getInputStream())).read(b);
                String responseBody = new String(b, 0, len);
                this.responseBody = responseBody;
                httpURLConnection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                success = false;
            }
        }

        public boolean getResult() {
            return success;
        }

        public String getResponseBody() {
            return responseBody;
        }

        public void setResponseBody(String responseBody) {
            this.responseBody = responseBody;
        }

        public void setURL(String url) {
            this.url = url;
        }
    }
}
