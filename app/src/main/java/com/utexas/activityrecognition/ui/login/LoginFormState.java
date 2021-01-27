package com.utexas.activityrecognition.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer emailError, @Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.emailError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }
    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}