package com.utexas.activityrecognition.api;

import android.content.Context;

import com.utexas.activityrecognition.data.error.RegistrationException;
import com.utexas.activityrecognition.data.model.Inference;
import com.utexas.activityrecognition.data.model.Session;

import java.util.ArrayList;


public interface RecognitionAPI {
    boolean login(Context context, String username, String password);
    boolean logout(Context context);
    boolean register(Context context, String email, String username, String password) throws RegistrationException;
    boolean connectImgSocket(Context context);
    ArrayList<Session> getSessions(Context context);
    ArrayList<Inference> getInferences(Context context, int sessionId);

}
