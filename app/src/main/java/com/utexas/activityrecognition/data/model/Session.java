package com.utexas.activityrecognition.data.model;

import java.util.Date;

public class Session {
    int id;
    Date timestamp;
    byte[] img;
    public Session(int id, Date timestamp, byte[] img) {
        this.id = id;
        this.timestamp = timestamp;
        this.img = img;
    }
}
