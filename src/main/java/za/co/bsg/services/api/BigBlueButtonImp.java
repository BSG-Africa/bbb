package za.co.bsg.services.api;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import za.co.bsg.config.AppPropertiesConfiguration;
import za.co.bsg.model.Meeting;
import za.co.bsg.model.User;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Service
public class BigBlueButtonImp implements BigBlueButtonAPI {

    @Autowired
    AppPropertiesConfiguration appPropertiesConfiguration;
    // BBB API Keys
    protected final static String API_SERVER_PATH = "api/";
    protected final static String API_CREATE = "create";
    protected final static String API_JOIN = "join";
    protected final static String API_SUCCESS = "SUCCESS";
    protected final static String API_RETURNED = "returncode";

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
    public String createPublicMeeting(Meeting meeting, User user, String welcome, Map<String, String> metadata, String xml) {
        String base_url_create = getBaseURL(API_SERVER_PATH, API_CREATE);
        String base_url_join = getBaseURL(API_SERVER_PATH, API_JOIN);

        String welcome_param = "";
        if ((welcome != null) && !welcome.equals("")) {
            welcome_param = "&welcome=" + urlEncode(welcome);
        }
        String xml_param = "";
        if ((xml != null) && !xml.equals("")) {
            xml_param = xml;
        }

        // build query
        StringBuilder query = new StringBuilder();
        query.append("&name=");
        query.append(urlEncode(meeting.getName()));
        query.append("&meetingID=");
        query.append(urlEncode(meeting.getMeetingId()));
        query.append(welcome_param);
        query.append("&voiceBridge=");
        query.append(meeting.getVoiceBridge() == 0 ? urlEncode("011 215 6666") : urlEncode(String.valueOf(meeting.getVoiceBridge())));
        query.append("&attendeePW=");
        query.append(getPublicAttendeePW());
        query.append("&moderatorPW=");
        query.append(getPublicModeratorPW());
        query.append("&isBreakoutRoom=false");
        query.append("&record=");
        query.append("false");
        query.append(getMetaData( metadata ));

        //Make API call
        Document doc = null;
        try {
            String url = base_url_create + query.toString()
                    + "&checksum="
                    + checksum(API_CREATE + query.toString() + getSalt());
            doc = parseXml( postURL( url, xml_param ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doc.getElementsByTagName(API_RETURNED).item(0).getTextContent()
                .trim().equals(API_SUCCESS)) {
            // Looks good, now return a URL to join that meeting
            String join_parameters = "meetingID=" + urlEncode(meeting.getMeetingId())
                    + "&fullName=" + urlEncode(user.getName()) + "&password="+getPublicModeratorPW();
            return base_url_join + join_parameters + "&checksum="
                    + checksum(API_JOIN + join_parameters + getSalt());
        }

        return doc.getElementsByTagName("messageKey").item(0).getTextContent()
                .trim()
                + ": "
                + doc.getElementsByTagName("message").item(0).getTextContent()
                .trim();
    }

    @Override
    public boolean isMeetingRunning(Meeting meeting) {
        return isMeetingRunning(meeting.getMeetingId());
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

    private Document parseXml(String xml)
            throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
        return doc;
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

    public static String postURL(String targetURL, String urlParameters)
    {
        return postURL(targetURL, urlParameters, "text/xml");
    }

    public static String postURL(String targetURL, String urlParameters, String contentType)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    public String endMeeting(String meetingID, String moderatorPassword) {
        Document doc = null;
        try {
            String xml = getURL(getEndMeetingURL(meetingID, moderatorPassword));
            doc = parseXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doc.getElementsByTagName("returncode").item(0).getTextContent()
                .trim().equals("SUCCESS")) {
            return "true";
        }
        return "Error "
                + doc.getElementsByTagName("messageKey").item(0)
                .getTextContent().trim()
                + ": "
                + doc.getElementsByTagName("message").item(0).getTextContent()
                .trim();
    }

    public String getEndMeetingURL(String meetingID, String moderatorPassword) {
        String end_parameters = "meetingID=" + urlEncode(meetingID) + "&password="
                + urlEncode(moderatorPassword);
        return getUrl() + "api/end?" + end_parameters + "&checksum="
                + checksum("end" + end_parameters + getSalt());
    }

    public boolean isMeetingRunning(String meetingID) {
        Document doc = null;
        try {
            doc = parseXml( getURL( getURLisMeetingRunning(meetingID) ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doc.getElementsByTagName("returncode").item(0).getTextContent()
                .trim().equals("SUCCESS")) {
            return true;
        }
        return false;
    }

    public String getURLisMeetingRunning(String meetingID) {
        String meetingParameters = "meetingID=" + urlEncode(meetingID);
        return getUrl() + "api/isMeetingRunning?" + meetingParameters
                + "&checksum="
                + checksum("isMeetingRunning" + meetingParameters + getSalt());
    }

    public static String getURL(String url) {
        StringBuffer response = null;

        try {
            URL u = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) u.openConnection();

            httpConnection.setUseCaches(false);
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("GET");

            httpConnection.connect();
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream input = httpConnection.getInputStream();

                // Read server's response.
                response = new StringBuffer();
                Reader reader = new InputStreamReader(input, "UTF-8");
                reader = new BufferedReader(reader);
                char[] buffer = new char[1024];
                for (int n = 0; n >= 0;) {
                    n = reader.read(buffer, 0, buffer.length);
                    if (n > 0)
                        response.append(buffer, 0, n);
                }

                input.close();
                httpConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response != null) {
            return response.toString();
        } else {
            return "";
        }
    }
}
