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
import java.util.Random;

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
    public String getLogoutURL() {
        return appPropertiesConfiguration.getLogoutURL();
    }

    /**
     * This method creates a public bbb meeting that can be joined by external users.
     * First the base url is retrieved, the a meeting query is constructed.
     * If a defaultPresentationURL is not empty or null, set uploadPresentation to true
     * and append the presentation link to the meeting query.
     * A checksumParameter is then appended to the meeting.
     * The bbb create api call is invoked to create a bbb meeting and the a
     * response is build based on whether or not a presentation is uploaded
     *
     * @param meeting a Meeting data type - details of meeting to be created
     * @param user User data type - user creating a bbb meeting
     * @return a String object
     */
    @Override
    public String createPublicMeeting(Meeting meeting, User user) {
        String base_url_join = getBaseURL(API_SERVER_PATH, API_JOIN);
        boolean uploadPresentation = false;

        // build query
        StringBuilder query = new StringBuilder();
        Random random = new Random();
        String voiceBridge_param = "&voiceBridge=" + (70000 + random.nextInt(9999));
        query.append("&name=");
        query.append(urlEncode(meeting.getName()));
        query.append("&meetingID=");
        query.append(urlEncode(meeting.getMeetingId()));
        query.append(voiceBridge_param);
        query.append("&attendeePW=");
        query.append(getPublicAttendeePW());
        query.append("&moderatorPW=");
        query.append(getPublicModeratorPW());
        query.append("&isBreakoutRoom=false");
        query.append("&record=");
        query.append("false");
        query.append("&logoutURL=");
        query.append(urlEncode(getLogoutURL()));
        query.append(getMetaData( meeting.getMeta() ));
        query.append("&welcome=");
        query.append(urlEncode("<br>" + meeting.getWelcomeMessage() + "<br>"));

        if (meeting.getDefaultPresentationURL() != "" && meeting.getDefaultPresentationURL() != null) {
            uploadPresentation = true;
            query.append(urlEncode("<br><br><br>" + "The presentation will appear in a moment.  To download click <a href=\"event:" + meeting.getDefaultPresentationURL() + "\"><u>" + meeting.getDefaultPresentationURL() + "</u></a>.<br>" + "<br>"));
        }

        query.append(getCheckSumParameter(API_CREATE, query.toString()));

        //Make API call
        CreateMeeting response = null;
        String responseCode = "";
        try {
            //pre-upload presentation
            if (uploadPresentation) {
                String xml_presentation = "<modules> <module name=\"presentation\"> <document url=\"" + meeting.getDefaultPresentationURL() + "\" /> </module> </modules>";
                response = makeAPICall(API_CREATE, query.toString(), xml_presentation, CreateMeeting.class);
            } else {
                response = makeAPICall(API_CREATE, query.toString(), CreateMeeting.class);
            }
            responseCode = response.getReturncode();
        } catch (BigBlueButtonException e) {
            e.printStackTrace();
        }


        if (API_SUCCESS.equals(responseCode)) {
            // Looks good, now return a URL to join that meeting
            String join_parameters = "meetingID=" + urlEncode(meeting.getMeetingId())
                    + "&fullName=" + urlEncode(user.getName()) + "&password="+getPublicModeratorPW();
            return base_url_join + join_parameters + "&checksum="
                    + getCheckSumParameter(API_JOIN + join_parameters + getSalt());
        }

        return ""+response;
    }

    /**
     * This method check whether or not meeting is running using meeting id
     *
     * @param meeting a Meeting object data type - which is used to get meeting id
     * @return boolean value indicating whether or not meeting is running
     */
    @Override
    public boolean isMeetingRunning(Meeting meeting) {
        try {
            return isMeetingRunning(meeting.getMeetingId());
        } catch (BigBlueButtonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * This method ends a bbb meeting if the user ending the meeting is a moderator,
     * by building a query containing meetingId and moderator password.
     * This query is then used to make an API call to end the bbb meeting,
     * if meeting has ended successfully or meeting does not exists return true boolean value
     *
     * @param meetingID a String data type - is used to query a meeting to be ended
     * @param moderatorPassword a String data type - a moderator password used to end a meeting
     * @return boolean value indicating whether meeting has ended
     */
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

    /**
     * This method gets public meeting url, which an external user can use to join meeting
     * The url is generated by, getting the base url and join parameters, which are
     * combined together with the checksum
     *
     * @param name a String data type - name of the meeting to be joined
     * @param meetingID a String data type - meetingId of the bbb meeting to be joined
     * @return a String object
     */
    @Override
    public String getPublicJoinURL(String name, String meetingID) {
        String base_url_join = getUrl() + "api/join?";
        String join_parameters = "meetingID=" + urlEncode(meetingID)
                + "&fullName=" + urlEncode(name) + "&password="+getPublicAttendeePW();
        return base_url_join + join_parameters + "&checksum="
                + getCheckSumParameter(API_JOIN + join_parameters + getSalt());
    }

    /**
     * This method builds the base url by combining the bbb url with provided path and api call
     *
     * @param path - a String data type - a server path
     * @param api_call a String data type - name of api call
     * @return a String object
     */
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

    /**
     * This method converts string to UTF-8 type
     *
     * @param s a String data type - url to encoded
     * @return a String object
     */
    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     *  This method generates a metadata parameter name
     * @param metadata Map of String type object - containing metadata keys user to generate parameter name
     *
     * @return a String object
     */
    private String getMetaData( Map<String, String> metadata ) {
        String metadata_params = "";

        if ( metadata!=null ){
            for(String key : metadata.keySet()){
                metadata_params = metadata_params + "&meta_" + urlEncode(key) + "=" + urlEncode(metadata.get(key));
            }
        }

        return metadata_params;
    }

    /**
     * This method check whether a bbb meeting is running
     * by building a query containing meetingId to get meeting by.
     * This query is then used to make an API call to check whether
     * the bbb meeting is running.
     *
     * @param meetingID a String data type - is used to query a meeting that is running
     * @return boolean value
     * @throws BigBlueButtonException
     */
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

    /**
     * This method gets check sum for a api call
     *
     * @param apiCall a String type - a api call name used as part of generation a checksum
     * @param queryString a String type - query string used as part of generating a checksum
     * @return a String object
     */
    private String getCheckSumParameter(String apiCall, String queryString) {
        if (getSalt() != null){
            return "&checksum=" + DigestUtils.sha1Hex(apiCall + queryString + getSalt());
        } else{
            return "";
        }

    }

    /**
     * This method generates a checksum which must be included in all api calls
     *
     * @param s a String data type - which is a SHA-1 hash of callName + queryString + securitySalt
     * @return a String object
     */
    private String getCheckSumParameter(String s) {
        String checksum = "";
        try {
            checksum = DigestUtils.sha1Hex(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return checksum;
    }

    /**
     * This method returns a bbb response, by making an api call taking api call type,
     * query and response type as parameters.
     *
     * @param apiCall a String data type - type of api call being made
     * @param query a String data type - query to query bbb data
     * @param responseType - a generic Class type - used to determine response type
     * @return a generic object
     * @throws BigBlueButtonException
     */
    protected <T extends BigBlueButtonResponse> T makeAPICall(String apiCall, String query, Class<T> responseType)
            throws BigBlueButtonException {
        return makeAPICall(apiCall, query, "", responseType);
    }

    /**
     * This method returns a bbb response, by making an api call taking api call type,
     * query, a presentation and response type as parameters.
     * The api call is used to generate a base url that is used to open a connection.
     * Once there a connection an XML response to be returned to a api call is processed
     * Else a BigBlueButtonException is thrown
     *
     * @param apiCall a String data type - used to get the base url
     * @param query a String data type - used to query bbb data
     * @param presentation a String type - presentation
     * @param responseType a generic class type - response type
     * @return a BigBlueButtonResponse
     * @throws BigBlueButtonException
     */
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
