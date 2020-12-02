package com.utexas.activityrecognition.api;

import android.content.Context;


public interface RecognitionAPI {
    boolean Login(Context context, String username, String password);
    boolean Logout(Context context);
    boolean Register(Context context, String username, String password);
    boolean ConnectImgSocket(Context context);
    boolean GetInferences(Context context);
}
