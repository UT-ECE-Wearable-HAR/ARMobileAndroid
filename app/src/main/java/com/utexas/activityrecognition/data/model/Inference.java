package com.utexas.activityrecognition.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Inference implements Parcelable {
    String description;
    long timestamp;
    byte[] img;
    public Inference(String description, long timestamp, byte[] img) {
        this.description = description;
        this.timestamp = timestamp;
        this.img = img;
    }

    protected Inference(Parcel in) {
        description = in.readString();
        timestamp = in.readLong();
        img = in.createByteArray();
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
        parcel.writeString(description);
        parcel.writeLong(timestamp);
        parcel.writeByteArray(img);
    }
}
