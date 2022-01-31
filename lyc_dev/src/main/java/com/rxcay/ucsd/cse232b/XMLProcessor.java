package com.rxcay.ucsd.cse232b;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 5:32 PM
 * @description
 */
public class XMLProcessor {
    public static final String XML_DATA_FILE_NAME = "j_caesar.xml";
    public static final String DTD_FILE_NAME = "play.dtd";

    public static List<Node> loadXMLDataFileToDomNodes(InputStream xmlDataFileStream,InputStream dtdStream)
            throws ParserConfigurationException, IOException, SAXException {
        List<Node> res = new LinkedList<>();
        DocumentBuilderFactory docBldFactory = DocumentBuilderFactory.newInstance();
        //docBldFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder bd = docBldFactory.newDocumentBuilder();
        bd.setEntityResolver((publicId, systemId) -> new InputSource(dtdStream));
        Document d = bd.parse(xmlDataFileStream);
        d.getDocumentElement().normalize();
        res.add(d);
        return res;
    }

     static List<Node> loadDefaultDataFileFromResource()
             throws ParserConfigurationException, IOException, SAXException {
        InputStream dataFileStream = XMLProcessor.class.getClassLoader().getResourceAsStream(XML_DATA_FILE_NAME);
        InputStream dtdFileStream = XMLProcessor.class.getClassLoader().getResourceAsStream(DTD_FILE_NAME);
        return  loadXMLDataFileToDomNodes(dataFileStream,dtdFileStream);
    }

    // temporary
    public static List<Node> loadXMLFileToNodes(String xmlFileName) throws Exception {
        if(XML_DATA_FILE_NAME.equals(xmlFileName)) {
            return loadDefaultDataFileFromResource();
        } else {
            throw new Exception("XML data file is not in resources");
        }
    }

}
