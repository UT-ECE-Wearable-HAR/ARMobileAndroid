package com.utexas.activityrecognition.data.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Session {

    private int id;
    private Date startTime;
    private Date endTime;
    private byte[] img;
    private Inference[] inferences;
    public Session(int id, Context context) {
        this.id = id;
        SharedPreferences sp = context.getSharedPreferences("inferenceInfo", Context.MODE_PRIVATE);
        startTime = new Date(sp.getLong("session" + id + "Start", 0));
        endTime = new Date(sp.getLong("session" + id + "End", 0));
        String imgBytes = sp.getString("session" + id + "Img", null);
        if(imgBytes != null){
            img = Base64.decode(imgBytes, 0);
        } else {
            img = null;
        }
        int numInferences = sp.getInt("session" + id + "InferenceCount", 0);
        inferences = new Inference[numInferences];
        for (int i = 0; i < numInferences; i++){
            int inferenceId = sp.getInt("session" + id + "Inference" + i + "Id", 0);
            inferences[i] = new Inference(inferenceId, context);
        }
    }

    public int getId(){
        return id;
    }

    public byte[] getImg(){
        return img;
    }

    public Bitmap getImgBitmap(){
        return BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    public Inference[] getInferences(){
        return inferences;
    }

    public String getStartTimeString(){
        String d = startTime.toString();
        String[] dateParts = d.split(":");
        return dateParts[0] + ":" + dateParts[1];
    }

    public static Session[] getAllSessions(Context context){
        SharedPreferences sp = context.getSharedPreferences("inferenceInfo", Context.MODE_PRIVATE);
        int numSessions = sp.getInt("currLargestSessionID", -1) + 1;
        Session[] allSessions = new Session[numSessions];
        for(int i = 0; i < numSessions; i++){
            allSessions[i] = new Session(numSessions - i - 1, context);
        }
        return allSessions;
    }

    public static void saveSession(Context context, JSONObject jsonObject) throws JSONException {
        SharedPreferences sp = context.getSharedPreferences("inferenceInfo", Context.MODE_PRIVATE);
        int id = sp.getInt("currLargestSessionID", -1) + 1;
        //TODO call apis on a separate thread
        long[][] timestamps = RecogitionAPIImpl.getInstance().getTimestamps(context, jsonObject.getJSONArray("activities"));
        int numInferences = timestamps.length;

        int[] inferenceImgIds = new int[numInferences];
        JSONArray startEndIds = jsonObject.getJSONArray("activities");
        for (int i = 0; i < numInferences; i++){
            int startId = startEndIds.getJSONObject(i).getInt("start");
            int endId = startEndIds.getJSONObject(i).getInt("end");
            int middleId = (startId + endId)/2;
            inferenceImgIds[i] = middleId;
        }
        SharedPreferences.Editor spEditor = sp.edit();
        byte[][] inferenceImgs = RecogitionAPIImpl.getInstance().getImgs(context, inferenceImgIds);
        int inferenceId = sp.getInt("currLargestInferenceID", -1);
        for(int i = 0; i < numInferences; i++){
            Inference.saveInference(context, ++inferenceId, timestamps[i][0], timestamps[i][1]
                    , inferenceImgs[i], startEndIds.getJSONObject(i).getInt("id"), spEditor);
            spEditor.putInt("session" + id + "Inference" + i + "Id", inferenceId);
        }

        spEditor.putLong("session" + id + "Start", timestamps[0][0]);
        spEditor.putLong("session" + id + "End", timestamps[timestamps.length - 1][1]);
        spEditor.putString("session" + id + "Img", Base64.encodeToString(inferenceImgs[0], Base64.DEFAULT));
        spEditor.putInt("session" + id + "InferenceCount", timestamps.length);
        spEditor.putInt("currLargestSessionID", id);
        spEditor.putInt("currLargestInferenceID", inferenceId);
        spEditor.apply();
    }
}
