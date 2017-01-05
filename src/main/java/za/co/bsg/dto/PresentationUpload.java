package za.co.bsg.dto;

import java.io.Serializable;

public class PresentationUpload implements Serializable {
    public String url;
    public String response;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
