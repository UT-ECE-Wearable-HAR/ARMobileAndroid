package com.utexas.activityrecognition.api;

import android.content.Context;

import com.utexas.activityrecognition.data.error.RegistrationException;


public interface RecognitionAPI {
    boolean Login(Context context, String username, String password);
    boolean Logout(Context context);
    boolean Register(Context context, String email, String username, String password) throws RegistrationException;
    boolean ConnectImgSocket(Context context);
    boolean GetInferences(Context context);
}
