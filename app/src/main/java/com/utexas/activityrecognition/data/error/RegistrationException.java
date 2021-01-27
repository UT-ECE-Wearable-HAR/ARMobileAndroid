package com.utexas.activityrecognition.data.error;

import androidx.annotation.Nullable;

public class RegistrationException extends  Exception {
    int messageId;
    public RegistrationException(int messageid) {
        this.messageId = messageid;
    }

    public int getMessageId() {
        return messageId;
    }
}
