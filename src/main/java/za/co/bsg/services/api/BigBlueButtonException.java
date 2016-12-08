package za.co.bsg.services.api;

/**
 * Created by Kapeshi.Kongolo on 2016/12/08.
 */
public class BigBlueButtonException extends Exception {

    public static final String  MESSAGEKEY_HTTPERROR            = "httpError";
    public static final String  MESSAGEKEY_NOTFOUND             = "notFound";
    public static final String  MESSAGEKEY_NOACTION             = "noActionSpecified";
    public static final String  MESSAGEKEY_IDNOTUNIQUE          = "idNotUnique";
    public static final String  MESSAGEKEY_NOTSTARTED           = "notStarted";
    public static final String  MESSAGEKEY_ALREADYENDED         = "alreadyEnded";
    public static final String  MESSAGEKEY_INTERNALERROR        = "internalError";
    public static final String  MESSAGEKEY_UNREACHABLE          = "unreachableServerError";
    public static final String  MESSAGEKEY_INVALIDRESPONSE      = "invalidResponseError";
    public static final String  MESSAGEKEY_GENERALERROR         = "generalError";

    private String messageKey;

    public BigBlueButtonException(String messageKey, String message, Throwable cause) {
        super(message, cause);
        this.messageKey = messageKey;
    }

    public BigBlueButtonException(String messageKey, String message) {
        super(message);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getPrettyMessage() {
        String _message = getMessage();
        String _messageKey = getMessageKey();

        StringBuilder pretty = new StringBuilder();
        if(_message != null) {
            pretty.append(_message);
        }
        if(_messageKey != null && !"".equals(_messageKey.trim())) {
            pretty.append(" (");
            pretty.append(_messageKey);
            pretty.append(")");
        }
        return pretty.toString();
    }

}