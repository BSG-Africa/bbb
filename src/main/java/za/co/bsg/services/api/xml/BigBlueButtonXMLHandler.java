package za.co.bsg.services.api.xml;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import za.co.bsg.services.api.exception.BigBlueButtonException;
import za.co.bsg.services.api.response.BigBlueButtonResponse;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BigBlueButtonXMLHandler extends ObjectMapper{

    /**
     * This nethod processes an XML response returned to API calls
     *
     * @param responseType
     * @param responseXML
     * @param <T>
     * @return
     * @throws BigBlueButtonException
     */
    public <T extends BigBlueButtonResponse> T processXMLResponse(Class<T> responseType, String responseXML) throws BigBlueButtonException {
        // Initialize XML libraries
        Document dom = null;
        responseXML = responseXML.replaceAll(">.\\s+?<", "><");
        DocumentBuilderFactory docBuilderFactory;
        DocumentBuilder docBuilder;
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            docBuilder = docBuilderFactory.newDocumentBuilder();

            dom = docBuilder.parse(new InputSource( new StringReader(responseXML)));
        } catch (ParserConfigurationException e) {
            System.out.println("Failed to initialise XML Parser: "+e);
        } catch (SAXException e) {
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_INVALIDRESPONSE, e.getMessage(), e);
        } catch (IOException e) {
            throw new BigBlueButtonException(BigBlueButtonException.MESSAGEKEY_UNREACHABLE, e.getMessage(), e);
        }

        Node firstNode = dom != null ? dom.getElementsByTagName("response").item(0) : null;

        //Create Map from Node and map to pojo
        Map<String, Object> map  = processNode(firstNode);
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BigBlueButtonResponse bigBlueButtonResponse = this.convertValue(map, responseType);
        return responseType.cast(bigBlueButtonResponse);
    }

    /**
     *
     * @param _node
     * @return
     */
    protected Map<String, Object> processNode(Node _node) {
        Map<String, Object> map = new HashMap<String, Object>();
        NodeList responseNodes = _node.getChildNodes();
        int images = 1; //counter for images (i.e image1, image2, image3)
        for (int i = 0; i < responseNodes.getLength(); i++) {
            Node node = responseNodes.item(i);
            String nodeName = node.getNodeName().trim();
            if (node.getChildNodes().getLength() == 1
                    && ( node.getChildNodes().item(0).getNodeType() == org.w3c.dom.Node.TEXT_NODE || node.getChildNodes().item(0).getNodeType() == org.w3c.dom.Node.CDATA_SECTION_NODE) ) {
                String nodeValue = node.getTextContent();
                if ("image".equals(nodeName) && node.getAttributes() != null){
                    Map<String, String> imageMap = new HashMap<String, String>();
                    Node heightAttr = node.getAttributes().getNamedItem("height");
                    Node widthAttr = node.getAttributes().getNamedItem("width");
                    Node altAttr = node.getAttributes().getNamedItem("alt");

                    imageMap.put("height", heightAttr.getNodeValue());
                    imageMap.put("width", widthAttr.getNodeValue());
                    imageMap.put("title", altAttr.getNodeValue());
                    imageMap.put("url", nodeValue);
                    map.put(nodeName + images, imageMap);
                    images++;
                } else {
                    map.put(nodeName, nodeValue != null ? nodeValue.trim() : null);
                }
            } else if (node.getChildNodes().getLength() == 0
                    && node.getNodeType() != org.w3c.dom.Node.TEXT_NODE
                    && node.getNodeType() != org.w3c.dom.Node.CDATA_SECTION_NODE) {
                map.put(nodeName, "");

            } else if ( node.getChildNodes().getLength() >= 1
                    && node.getChildNodes().item(0).getChildNodes().item(0).getNodeType() != org.w3c.dom.Node.TEXT_NODE
                    && node.getChildNodes().item(0).getChildNodes().item(0).getNodeType() != org.w3c.dom.Node.CDATA_SECTION_NODE ) {

                List<Object> list = new ArrayList<Object>();
                for (int c = 0; c < node.getChildNodes().getLength(); c++) {
                    Node n = node.getChildNodes().item(c);
                    list.add(processNode(n));
                }
                if ("preview".equals(nodeName)){
                    Node n = node.getChildNodes().item(0);
                    map.put(nodeName, processNode(n));
                }else{
                    map.put(nodeName, list);
                }

            } else {
                map.put(nodeName, processNode(node));
            }
        }
        return map;
    }

}
