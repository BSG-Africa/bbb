package za.co.bsg.services.api.response;

import java.io.Serializable;

public class BigBlueButtonResponse implements Serializable {

    private String messageKey;
    private String returncode;
    private String message;

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getReturncode() {
        return returncode;
    }

    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
