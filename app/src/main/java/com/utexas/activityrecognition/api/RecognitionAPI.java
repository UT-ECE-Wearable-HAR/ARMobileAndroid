package com.utexas.activityrecognition.api;

import android.content.Context;

import com.utexas.activityrecognition.data.error.RegistrationException;
import com.utexas.activityrecognition.data.model.Inference;
import com.utexas.activityrecognition.data.model.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;


public interface RecognitionAPI {
    boolean login(Context context, String username, String password);
    boolean logout(Context context);
    boolean register(Context context, String email, String username, String password) throws RegistrationException;
    boolean connectImgSocket(Context context);
    JSONObject getSession(Context context) throws JSONException;
    ArrayList<Inference> getInferences(Context context, int sessionId);
    byte[][] getImgs(Context context, int[] imgIds) throws JSONException;
    long[][] getTimestamps(Context context, JSONArray activities) throws JSONException;

}
