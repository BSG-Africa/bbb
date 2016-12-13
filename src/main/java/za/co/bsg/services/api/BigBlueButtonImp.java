package za.co.bsg.services.api;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;
import za.co.bsg.services.api.exception.BigBlueButtonException;
import za.co.bsg.services.api.response.BigBlueButtonResponse;
import za.co.bsg.services.api.response.CreateMeeting;
import za.co.bsg.services.api.response.MeetingRunning;
import za.co.bsg.services.api.xml.BigBlueButtonXMLHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Service
public class BigBlueButtonImp implements BigBlueButtonAPI {

    @Autowired
    AppPropertiesConfiguration appPropertiesConfiguration;
    @Autowired
    BigBlueButtonXMLHandler bigBlueButtonXMLHandler;
    // BBB API Keys
    protected final static String API_SERVER_PATH = "api/";
    protected final static String API_CREATE = "create";
    protected final static String API_JOIN = "join";
    protected final static String API_SUCCESS = "SUCCESS";
    protected final static String API_MEETING_RUNNING = "isMeetingRunning";
    protected final static String API_FAILED = "FAILED";
    protected final static String API_END_MEETING = "end";

    @Override
    public String getUrl() {
        return appPropertiesConfiguration.getBbbURL();
    }

    @Override
    public String getSalt() {
        return appPropertiesConfiguration.getBbbSalt();
    }

    @Override
    public String getPublicAttendeePW() {
        return appPropertiesConfiguration.getAttendeePW();
    }

    @Override
    public String getPublicModeratorPW() {
        return appPropertiesConfiguration.getModeratorPW();
    }

    @Override
    public String createPublicMeeting(Meeting meeting, User user) {
        String base_url_join = getBaseURL(API_SERVER_PATH, API_JOIN);

        // build query
        StringBuilder query = new StringBuilder();
        query.append("&name=");
        query.append(urlEncode(meeting.getName()));
        query.append("&meetingID=");
        query.append(urlEncode(meeting.getMeetingId()));
        query.append("&welcome=");
        query.append(urlEncode("<br>" + meeting.getWelcomeMessage() + "<br>"));
        query.append("&voiceBridge=");
        query.append(meeting.getVoiceBridge() == 0 ? urlEncode("011 215 6666") : urlEncode(String.valueOf(meeting.getVoiceBridge())));
        query.append("&attendeePW=");
        query.append(getPublicAttendeePW());
        query.append("&moderatorPW=");
        query.append(getPublicModeratorPW());
        query.append("&isBreakoutRoom=false");
        query.append("&record=");
        query.append("false");
        query.append(getMetaData( meeting.getMeta() ));
        query.append(getCheckSumParameter(API_CREATE, query.toString()));

        //Make API call
        CreateMeeting response = null;
        String responseCode = "";
        try {
            response = makeAPICall(API_CREATE, query.toString(), CreateMeeting.class);
            responseCode = response.getReturncode();
        } catch (BigBlueButtonException e) {
            e.printStackTrace();
        }


        if (API_SUCCESS.equals(responseCode)) {
            // Looks good, now return a URL to join that meeting
            String join_parameters = "meetingID=" + urlEncode(meeting.getMeetingId())
                    + "&fullName=" + urlEncode(user.getName()) + "&password="+getPublicModeratorPW();
            return base_url_join + join_parameters + "&checksum="
                    + checksum(API_JOIN + join_parameters + getSalt());
        }

        return ""+response;
    }

    @Override
    public boolean isMeetingRunning(Meeting meeting) {
        try {
            return isMeetingRunning(meeting.getMeetingId());
        } catch (BigBlueButtonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean endMeeting(String meetingID, String moderatorPassword) {

        StringBuilder query = new StringBuilder();
        query.append("meetingID=");
        query.append(meetingID);
        query.append("&password=");
        query.append(moderatorPassword);
        query.append(getCheckSumParameter(API_END_MEETING, query.toString()));

        try {
            makeAPICall(API_END_MEETING, query.toString(), BigBlueButtonResponse.class);

        } catch (BigBlueButtonException e) {
            if(BigBlueButtonException.MESSAGEKEY_NOTFOUND.equals(e.getMessageKey())) {
                // we can safely ignore this one: the meeting is not running
                return true;
            }else{
                System.out.println("Error: "+e);
            }
        }

        return true;
    }

    private String getBaseURL(String path, String api_call) {
        StringBuilder url = new StringBuilder(getUrl());
        if (url.toString().endsWith("/bigbluebutton")){
            url.append("/");
        }
        url.append(path);
        url.append(api_call);
        url.append("?");
        return url.toString();
    }

    public String getPublicJoinURL(String name, String meetingID) {
        String base_url_join = getUrl() + "api/join?";
        String join_parameters = "meetingID=" + urlEncode(meetingID)
                + "&fullName=" + urlEncode(name) + "&password="+getPublicAttendeePW();
        return base_url_join + join_parameters + "&checksum="
                + checksum(API_JOIN + join_parameters + getSalt());
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMetaData( Map<String, String> metadata ) {
        String metadata_params = "";

        if ( metadata!=null ){
            for(String key : metadata.keySet()){
                metadata_params = metadata_params + "&meta_" + urlEncode(key) + "=" + urlEncode(metadata.get(key));
            }
        }

        return metadata_params;
    }

    public static String checksum(String s) {
        String checksum = "";
        try {
            checksum = DigestUtils.sha1Hex(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checksum;
    }

    public boolean isMeetingRunning(String meetingID)
            throws BigBlueButtonException {
        try {
            StringBuilder query = new StringBuilder();
            query.append("meetingID=");
            query.append(meetingID);
            query.append(getCheckSumParameter(API_MEETING_RUNNING, query.toString()));

            MeetingRunning bigBlueButtonResponse = makeAPICall(API_MEETING_RUNNING, query.toString(), MeetingRunning.class);
            return Boolean.parseBoolean(bigBlueButtonResponse.getRunning());
        } catch (Exception e) {
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_INTERNALERROR, e.getMessage(), e);
        }
    }

    protected String getCheckSumParameter(String apiCall, String queryString) {
        if (getSalt() != null){
            return "&checksum=" + DigestUtils.sha1Hex(apiCall + queryString + getSalt());
        } else{
            return "";
        }

    }

    protected <T extends BigBlueButtonResponse> T makeAPICall(String apiCall, String query, Class<T> responseType)
            throws BigBlueButtonException {
        return makeAPICall(apiCall, query, "", responseType);
    }

    protected <T extends BigBlueButtonResponse> T makeAPICall(String apiCall, String query, String presentation, Class<T> responseType)
            throws BigBlueButtonException {
        StringBuilder urlStr = new StringBuilder(getBaseURL(API_SERVER_PATH, apiCall));
        if (query != null) {
            urlStr.append(query);
        }

        try {
            // open connection
            URL url = new URL(urlStr.toString());
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);
            if(!presentation.equals("")){
                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "text/xml");
                httpConnection.setRequestProperty("Content-Length", "" + Integer.toString(presentation.getBytes().length));
                httpConnection.setRequestProperty("Content-Language", "en-US");
                httpConnection.setDoInput(true);

                DataOutputStream wr = new DataOutputStream( httpConnection.getOutputStream() );
                wr.writeBytes (presentation);
                wr.flush();
                wr.close();
            } else {
                httpConnection.setRequestMethod("GET");
            }
            httpConnection.connect();

            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // read response
                InputStreamReader isr = null;
                BufferedReader reader = null;
                StringBuilder xml = new StringBuilder();
                try {
                    isr = new InputStreamReader(httpConnection.getInputStream(), "UTF-8");
                    reader = new BufferedReader(isr);
                    String line = reader.readLine();
                    while (line != null) {
                        if( !line.startsWith("<?xml version=\"1.0\"?>"))
                            xml.append(line.trim());
                        line = reader.readLine();
                    }
                } finally {
                    if (reader != null)
                        reader.close();
                    if (isr != null)
                        isr.close();
                }
                httpConnection.disconnect();

                String stringXml = xml.toString();

                BigBlueButtonResponse bigBlueButtonResponse = bigBlueButtonXMLHandler.processXMLResponse(responseType, stringXml);
                String returnCode = bigBlueButtonResponse.getReturncode();
                if (API_FAILED.equals(returnCode)) {
                    throw new BigBlueButtonException(bigBlueButtonResponse.getMessageKey(), bigBlueButtonResponse.getMessage());
                }

                return responseType.cast(bigBlueButtonResponse);

            } else {
                throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_HTTPERROR, "BBB server responded with HTTP status code " + responseCode);
            }

        } catch(BigBlueButtonException e) {
            if( !e.getMessageKey().equals("notFound") )
                System.out.println("BBBException: MessageKey=" + e.getMessageKey() + ", Message=" + e.getMessage());
            throw new BigBlueButtonException( e.getMessageKey(), e.getMessage(), e);
        } catch(IOException e) {
            System.out.println("BBB IOException: Message=" + e.getMessage());
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_UNREACHABLE, e.getMessage(), e);

        } catch(IllegalArgumentException e) {
            System.out.printf("BBB IllegalArgumentException: Message=" + e.getMessage());
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_INVALIDRESPONSE, e.getMessage(), e);

        } catch(Exception e) {
            System.out.println("BBB Exception: Message=" + e.getMessage());
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_UNREACHABLE, e.getMessage(), e);
        }
    }
}
