package za.co.bsg.services.api;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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
import java.util.Random;

@Service
public class BigBlueButtonImp implements BigBlueButtonAPI {
    @Override
    public String getUrl() {
        return "https://learning.bsg.co.za/bigbluebutton/";
    }

    @Override
    public String getSalt() {
        return "aa3dda0b9ffc3a0b5047c9c444d91ab5";
    }

    @Override
    public Meeting createMeeting(Meeting meeting) throws BigBlueButtonException {
        String base_url_create = getUrl() + "api/create?";

        String welcome_param = "";

        String attendee_password_param = "&attendeePW=ap";
        String moderator_password_param = "&moderatorPW=mp";
        String voice_bridge_param = "";
        String logoutURL_param = "";
        String moderatorWelcomeMsg_param = "";

        if ((meeting.getWelcomeMessage() != null) && !meeting.getWelcomeMessage().equals("")) {
            welcome_param = "&welcome=" + urlEncode(meeting.getWelcomeMessage());
        }

        if ((meeting.getModeratorPassword() != null) && !meeting.getModeratorPassword().equals("")) {
            moderator_password_param = "&moderatorPW=" + urlEncode(meeting.getModeratorPassword());
        }

        if ((meeting.getWelcomeMessage() != null) && !meeting.getWelcomeMessage().equals("")) {
            moderatorWelcomeMsg_param = "&moderatorOnlyMessage=" + urlEncode(meeting.getWelcomeMessage());
        }

        if ((meeting.getAttendeePassword() != null) && !meeting.getAttendeePassword().equals("")) {
            attendee_password_param = "&attendeePW=" + urlEncode(meeting.getAttendeePassword());
        }

        if (meeting.getVoiceBridge() > 0){
            voice_bridge_param = "&voiceBridge=" + urlEncode(String.valueOf(meeting.getVoiceBridge()));
        } else {
            // No voice bridge number passed, so we'll generate a random one for this meeting
            Random random = new Random();
            Integer n = 70000 + random.nextInt(9999);
            voice_bridge_param = "&voiceBridge=" + n;
        }

        if ((meeting.getLogoutURL() != null) && !meeting.getLogoutURL().equals("")) {
            logoutURL_param = "&logoutURL=" + urlEncode(meeting.getLogoutURL());
        }

        //
        // Now create the URL
        //

        String create_parameters = "name=" + urlEncode(meeting.getMeetingId())
                + "&meetingID=" + urlEncode(meeting.getMeetingId()) + welcome_param
                + attendee_password_param + moderator_password_param
                + moderatorWelcomeMsg_param + voice_bridge_param + logoutURL_param;

        Document doc = null;

        try {
            // Attempt to create a meeting using meetingID
            String xml = getURL(base_url_create + create_parameters
                    + "&checksum="
                    + checksum("create" + create_parameters + getSalt()));
            doc = parseXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doc.getElementsByTagName("returncode").item(0).getTextContent().trim().equals("SUCCESS")) {
            return meeting;
        }

        String message = "Error "
                + doc.getElementsByTagName("messageKey").item(0).getTextContent().trim()
                + ": "
                + doc.getElementsByTagName("message").item(0).getTextContent()
                .trim();
        return null;
    }

    private String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String encodeURIComponent(String component)   {
        String result = null;

        try {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = component;
        }

        return result;
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
            for(String metakey : metadata.keySet()){
                metadata_params = metadata_params + "&meta_" + urlEncode(metakey) + "=" + urlEncode(metadata.get(metakey));
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

    public static String postURL(String targetURL, String urlParameters)
    {
        return postURL(targetURL, urlParameters, "text/xml");
    }

    public static String postURL(String targetURL, String urlParameters, String contentType)
    {
        URL url;
        HttpURLConnection connection = null;
        int responseCode = 0;
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

    public String getEndMeetingURL(String meetingID, String moderatorPassword) {
        String end_parameters = "meetingID=" + urlEncode(meetingID) + "&password="
                + urlEncode(moderatorPassword);
        return getUrl() + "api/end?" + end_parameters + "&checksum="
                + checksum("end" + end_parameters + getSalt());
    }

    @Override
    public boolean getMeetingStatus(String meetingID) throws BigBlueButtonException {
        return false;
    }

    @Override
    public Map<String, Object> getMeetingInfo(String meetingID, String password) throws BigBlueButtonException {
        return null;
    }

    @Override
    public boolean endMeeting(String meetingID, String password) throws BigBlueButtonException {
        Document doc = null;
        try {
            String xml = getURL(getEndMeetingURL(meetingID, password));
            doc = parseXml(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (doc.getElementsByTagName("returncode").item(0).getTextContent()
                .trim().equals("SUCCESS")) {
            return true;
        }

        return false;
    }

    @Override
    public String getJoinMeetingURL(String username, String meetingID, String password, String clientURL) {
        String base_url_join = getUrl() + "api/join?";
        String clientURL_param = "";

        if ((clientURL != null) && !clientURL.equals("")) {
            clientURL_param = "&redirectClient=true&clientURL=" + urlEncode( clientURL );
        }


        String join_parameters = "meetingID=" + urlEncode(meetingID)
                + "&fullName=" + urlEncode(username) + "&password="
                + urlEncode(password) +  clientURL_param;

        return base_url_join + join_parameters + "&checksum="
                + checksum("join" + join_parameters + getSalt());
    }

    @Override
    public String getJoinURL(Meeting meeting, User user, String welcome, Map<String, String> metadata, String xml) {
        String base_url_create = getUrl() + "api/create?";
        String base_url_join = getUrl() + "api/join?";
        String record = "false";
        String welcome_param = "";
        if ((welcome != null) && !welcome.equals("")) {
            welcome_param = "&welcome=" + urlEncode(welcome);
        }
        String xml_param = "";
        if ((xml != null) && !xml.equals("")) {
            xml_param = xml;
        }

        Random random = new Random();
        String voiceBridge_param = "&voiceBridge=" + (70000 + random.nextInt(9999));

        String create_parameters = "name=" + urlEncode(meeting.getName())
                + "&meetingID=" + urlEncode(meeting.getMeetingId()) + welcome_param + voiceBridge_param
                + "&attendeePW=ap&moderatorPW=mp"
                + "&isBreakoutRoom=false"
                + "&record=" + record + getMetaData( metadata );
        // Attempt to create a meeting using meetingID
        Document doc = null;
        try {
            String url = base_url_create + create_parameters
                    + "&checksum="
                    + checksum("create" + create_parameters + getSalt());
            doc = parseXml( postURL( url, xml_param ) );
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (doc.getElementsByTagName("returncode").item(0).getTextContent()
                .trim().equals("SUCCESS")) {
            //
            // Looks good, now return a URL to join that meeting
            //
            String join_parameters = "meetingID=" + urlEncode(meeting.getMeetingId())
                    + "&fullName=" + urlEncode(user.getUsername()) + "&password=mp";
            return base_url_join + join_parameters + "&checksum="
                    + checksum("join" + join_parameters + getSalt());
        }

        return doc.getElementsByTagName("messageKey").item(0).getTextContent()
                .trim()
                + ": "
                + doc.getElementsByTagName("message").item(0).getTextContent()
                .trim();
    }

    @Override
    public String getMeetings() throws BigBlueButtonException {
        try {
            Document doc = parseXml( getURL( getMeetingsURL() ));

            // tags needed for parsing xml documents
            final String startTag = "<meetings>";
            final String endTag = "</meetings>";
            final String startResponse = "<response>";
            final String endResponse = "</response>";

            // if the request succeeded, then calculate the checksum of each meeting and insert it into the document
            NodeList meetingsList = doc.getElementsByTagName("meeting");

            String newXMldocument = startTag;
            for (int i = 0; i < meetingsList.getLength(); i++) {
                Element meeting = (Element) meetingsList.item(i);
                String meetingID = meeting.getElementsByTagName("meetingID").item(0).getTextContent();
                String password = meeting.getElementsByTagName("moderatorPW").item(0).getTextContent();

                String data = getURL( getMeetingInfoURL(meetingID, password) );

                if (data.indexOf("<response>") != -1) {
                    int startIndex = data.indexOf(startResponse) + startTag.length();
                    int endIndex = data.indexOf(endResponse);
                    newXMldocument +=  "<meeting>" + data.substring(startIndex, endIndex) + "</meeting>";
                }
            }
            newXMldocument += endTag;

            return newXMldocument;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    public String getMeetingsURL() {
        String meetingParameters = "random=" + new Random().nextInt(9999);
        return getUrl() + "api/getMeetings?" + meetingParameters
                + "&checksum="
                + checksum("getMeetings" + meetingParameters + getSalt());
    }

    public String getMeetingInfoURL(String meetingID, String password) {
        String meetingParameters = "meetingID=" + urlEncode(meetingID)
                + "&password=" + password;
        return getUrl() + "api/getMeetingInfo?" + meetingParameters
                + "&checksum="
                + checksum("getMeetingInfo" + meetingParameters + getSalt());
    }
}
