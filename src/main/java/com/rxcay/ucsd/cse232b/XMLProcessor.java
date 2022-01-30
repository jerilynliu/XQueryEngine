package com.rxcay.ucsd.cse232b;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
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
    public static List<Node> loadXMLDataFileToDomNodes(String xmlFileName,String dtdFileName) {
        List<Node> res = new LinkedList<>();
        InputStream xmlDataFile = XMLProcessor.class.getClassLoader().getResourceAsStream(xmlFileName);
        DocumentBuilderFactory docBldFactory = DocumentBuilderFactory.newInstance();
        //docBldFactory.setIgnoringElementContentWhitespace(true);
        try {
            DocumentBuilder bd = docBldFactory.newDocumentBuilder();
            bd.setEntityResolver((publicId, systemId) -> {
                if (systemId.contains(dtdFileName)) {
                    InputStream dtdStream = XMLProcessor.class
                            .getClassLoader().getResourceAsStream(dtdFileName);
                    return new InputSource(dtdStream);
                } else {
                    return null;
                }
            });
            Document d = bd.parse(xmlDataFile);
            d.getDocumentElement().normalize();
            res.add(d);

        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return res;
    }

    public static List<Node> loadDefaultDataFile(){
        return  loadXMLDataFileToDomNodes(XML_DATA_FILE_NAME,DTD_FILE_NAME);
    }
}
