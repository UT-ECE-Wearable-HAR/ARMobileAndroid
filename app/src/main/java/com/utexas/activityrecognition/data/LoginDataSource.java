package com.utexas.activityrecognition.data;

import android.content.Context;

import com.utexas.activityrecognition.api.impl.RecogitionAPIImpl;
import com.utexas.activityrecognition.data.model.LoggedInUser;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    public Result<LoggedInUser> login(Context context, String username, String password) {
        if(RecogitionAPIImpl.Instance().Login(context, username, password)) {
            return new Result.Success<>(new LoggedInUser(username));
        } else {
            return new Result.Error(new IOException("Error logging in"));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}