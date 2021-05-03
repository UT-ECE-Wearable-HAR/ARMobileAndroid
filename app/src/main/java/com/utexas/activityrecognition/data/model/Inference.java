package com.utexas.activityrecognition.data.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import java.util.Date;

public class Inference implements Parcelable {
    private int id;
    private long startTime;
    private long endTime;
    private byte[] img;
    private int activityId;

    public Inference(int id, Context context){
        this.id = id;
        SharedPreferences sp = context.getSharedPreferences("inferenceInfo", Context.MODE_PRIVATE);
        startTime = sp.getLong("inference" + id + "Start", 0);
        endTime = sp.getLong("inference" + id + "End", 0);
        String imgBytes = sp.getString("inference" + id + "Img", null);
        if(imgBytes != null){
            img = Base64.decode(imgBytes, 0);
        } else {
            img = null;
        }
        activityId = sp.getInt("inference" + id + "ActivityId", 0);
    }

    protected Inference(Parcel in) {
        id = in.readInt();
        startTime = in.readLong();
        endTime = in.readLong();
        img = in.createByteArray();
        activityId = in.readInt();
    }

    public int getActivityId() {
        return activityId;
    }

    public String getStartTimeString(){
        String d = new Date(startTime).toString();
        String[] dateParts = d.split(":");
        return dateParts[0] + ":" + dateParts[1];
    }

    public Bitmap getImgBitmap() {
        return BitmapFactory.decodeByteArray(img, 0, img.length);
    }

    public int getId() {
        return id;
    }

    public static final Creator<Inference> CREATOR = new Creator<Inference>() {
        @Override
        public Inference createFromParcel(Parcel in) {
            return new Inference(in);
        }

        @Override
        public Inference[] newArray(int size) {
            return new Inference[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
        parcel.writeByteArray(img);
        parcel.writeInt(activityId);
    }

    public static void saveInference(Context context, int id, long startTime, long endTime, byte[] img, int activityId, SharedPreferences.Editor spEditor){
        spEditor.putLong("inference" + id + "Start", startTime);
        spEditor.putLong("inference" + id + "End", endTime);
        spEditor.putString("inference" + id + "Img", Base64.encodeToString(img, Base64.DEFAULT));
        spEditor.putInt("inference" + id + "ActivityId", activityId);
    }
}
