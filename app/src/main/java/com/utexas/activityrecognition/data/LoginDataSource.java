package com.utexas.activityrecognition.data;

import android.content.Context;

import com.utexas.activityrecognition.R;
import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;
import com.utexas.activityrecognition.data.error.RegistrationException;
import com.utexas.activityrecognition.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(Context context, String username, String password) {
        if(RecogitionAPIImpl.getInstance().login(context, username, password)) {
            return new Result.Success<>(new LoggedInUser(username));
        } else {
            return new Result.Error(new IOException("Error logging in"));
        }
    }

    public Result<LoggedInUser> register(Context context, String email, String username, String password) {
        try {
            if (RecogitionAPIImpl.getInstance().register(context, email, username, password)) {
                return new Result.Success<>(new LoggedInUser(username));
            } else {
                return new Result.Error(new RegistrationException(R.string.register_failed));
            }
        } catch (RegistrationException e) {
            return new Result.Error(e);
        }
    }

    public void logout(Context context) {
        if(RecogitionAPIImpl.getInstance().logout(context)) {
            return;
        } else {
            return;
        }
    }
}