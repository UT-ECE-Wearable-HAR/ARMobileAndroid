package com.utexas.activityrecognition.api.impl;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.api.RecognitionAPI;
import com.utexas.activityrecognition.data.RecognitionCookieStore;
import com.utexas.activityrecognition.data.error.RegistrationException;
import com.utexas.activityrecognition.data.model.Inference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RecogitionAPIImpl implements RecognitionAPI {
    final static String TAG = RecogitionAPIImpl.class.getCanonicalName();
    final static int TIMEOUT_MS = 3000;
    static RecogitionAPIImpl  Instance = new RecogitionAPIImpl();

    public static RecogitionAPIImpl getInstance() {
        return Instance;
    }

    @Override
    public boolean login(Context context, String username, String password) {
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
    public boolean logout(Context context) {
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
    public boolean register(Context context, String email, String username, String password) throws RegistrationException {
        CallerThread callerThread = new CallerThread(context);
        callerThread.setResponseBody("email=" + email + "&username=" + username + "&password=" + password);
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_register));
        try {
            callerThread.start();
            callerThread.join();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        if(callerThread.getResult() && callerThread.responseBody.equals("Username already in use")) {
            throw new RegistrationException(R.string.username_taken);
        }
        else if(callerThread.getResult() && callerThread.responseBody.equals("Email already in use")) {
            throw new RegistrationException(R.string.email_taken);
        }
        else {
            return (callerThread.getResult() && callerThread.responseBody.equals("Registered"));
        }
    }

    @Override
    public boolean connectImgSocket(Context context) {
        CallerThread callerThread = new CallerThread(context);
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_connect));
        try {
            callerThread.start();
            callerThread.join();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
        return (callerThread.getResult() && callerThread.responseBody.equals("Connected"));
    }

    @Override
    public JSONObject getSession(Context context) throws JSONException {
        //Just demo data for now
        JSONObject toRet = new JSONObject("{\n" +
                "    \"activities\": [\n" +
                "        {\n" +
                "            \"start\": 1,\n" +
                "            \"end\": 2,\n" +
                "            \"id\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"start\": 3,\n" +
                "            \"end\": 4,\n" +
                "            \"id\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"start\": 5,\n" +
                "            \"end\": 6,\n" +
                "            \"id\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"start\": 7,\n" +
                "            \"end\": 8,\n" +
                "            \"id\": 2\n" +
                "        },\n" +
                "        {\n" +
                "            \"start\": 30,\n" +
                "            \"end\": 33,\n" +
                "            \"id\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}");
        return toRet;
    }

    @Override
    public ArrayList<Inference> getInferences(Context context, int sessionId) {
        return null;
    }

    @Override
    public byte[][] getImgs(Context context, int[] imgIds) throws JSONException {
        CallerThread callerThread = new CallerThread(context);
        JSONArray arr = new JSONArray();
        for (int id: imgIds) {
            arr.put(id);
        }
        JSONObject body = new JSONObject();
        body.put("imgIds", arr);
        callerThread.setResponseBody(body.toString());
        callerThread.setURL(context.getResources().getString(R.string.base_url)
            + context.getResources().getString(R.string.api_getimgs));
        try {
            callerThread.start();
            callerThread.join();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return new byte[0][0];
        }
        if(callerThread.getResult()){
            String responseString = callerThread.responseBody.replaceAll("\n", "\\n");
            JSONObject response = new JSONObject(responseString);
            JSONArray imgs = response.getJSONArray("imgs");
            byte[][] toRet = new byte[imgIds.length][224*224*3];
            for(int i = 0; i < toRet.length; i++){
                toRet[i] = Base64.decode(imgs.getString(i), 0);
            }
            return toRet;
        }
        return new byte[0][0];
    }

    @Override
    public long[][] getTimestamps(Context context, JSONArray activities) throws JSONException {
        CallerThread callerThread = new CallerThread(context);
        JSONObject body = new JSONObject();
        JSONArray frameIds = new JSONArray();
        for(int i = 0; i < activities.length(); i++){
            JSONObject frame = new JSONObject();
            frame.put("start", activities.getJSONObject(i).getInt("start"));
            frame.put("end", activities.getJSONObject(i).getInt("end"));
            frameIds.put(frame);
        }
        body.put("frameIds", frameIds);
        callerThread.setResponseBody(body.toString());
        callerThread.setURL(context.getResources().getString(R.string.base_url)
                + context.getResources().getString(R.string.api_gettimestamps));
        try{
            callerThread.start();
            callerThread.join();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return new long[0][0];
        }
        if (callerThread.getResult()){
            String responseString = callerThread.responseBody.replaceAll("\n", "\\n");
            JSONObject response = new JSONObject(responseString);
            JSONArray timestamps = response.getJSONArray("timestamps");
            long[][] toRet = new long[frameIds.length()][2];
            for(int i = 0; i < toRet.length; i++){
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
                    formatter.setTimeZone(TimeZone.getDefault());

                    toRet[i][0] = formatter.parse(timestamps.getJSONObject(i).getString("start")).getTime();
                    toRet[i][1] = formatter.parse(timestamps.getJSONObject(i).getString("end")).getTime();
                } catch (ParseException e){
                    toRet[i][0] = 0;
                    toRet[i][1] = 0;
                }
            }
            return toRet;
        }
        return new long[0][0];
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
                httpURLConnection.setConnectTimeout(TIMEOUT_MS);
                String cookies = "";
                for(HttpCookie cookie:cookieStore.get(new URI(context.getResources().getString(R.string.base_url))))
                    cookies += cookie + "; ";
                httpURLConnection.setRequestProperty("Cookie", cookies);
                DataOutputStream output = new DataOutputStream(httpURLConnection.getOutputStream());
                if(responseBody != null) {
                    output.writeBytes(responseBody);
                }
                output.flush();
                output.close();
                int status = httpURLConnection.getResponseCode();
                int contentLength = httpURLConnection.getContentLength();
                if (status != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Invalid status " + status);
                    success = false;
                }
                List<String> cookielist = httpURLConnection.getHeaderFields().get("set-cookie");
                if(cookielist != null)
                    for(String cookieDetail : cookielist)
                        cookieStore.add(new URI(context.getResources().getString(R.string.base_url)),HttpCookie.parse(cookieDetail).get(0));
                byte[] readBytes = new byte[10*1024*1024];
                int totalLen = 0;
                while (contentLength > totalLen){
                    int len = (new DataInputStream(httpURLConnection.getInputStream())).read(readBytes, totalLen, readBytes.length - totalLen);
                    totalLen += len;
                }
                String responseBody = new String(readBytes, 0, totalLen);
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

