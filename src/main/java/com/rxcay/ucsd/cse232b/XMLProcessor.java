package com.rxcay.ucsd.cse232b;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 5:32 PM
 * @description
 */
public class XMLProcessor {
    public static final String DEFAULT_XML_DATA_FILE_NAME = "j_caesar.xml";
    public static final String DEFAULT_DTD_FILE_NAME = "play.dtd";
    static DocumentBuilderFactory docBldFactory = DocumentBuilderFactory.newInstance();
    static TransformerFactory transformerFactory = TransformerFactory.newInstance();
    private static List<Node> loadXMLDataFileToDomNodes(InputStream xmlDataFileStream,InputStream dtdStream)
            throws ParserConfigurationException, IOException, SAXException {
        List<Node> res = new LinkedList<>();
        //docBldFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder bd = docBldFactory.newDocumentBuilder();
        bd.setEntityResolver((publicId, systemId) ->{
            if (systemId.contains(".dtd")) {
                return new InputSource(dtdStream);
            } else {
                return null;
            }
        });
        Document d = bd.parse(xmlDataFileStream);
        d.getDocumentElement().normalize();
        res.add(d);
        return res;
    }

    static List<Node> loadDefaultDataFileFromResource()
             throws ParserConfigurationException, IOException, SAXException {
        InputStream dataFileStream = XMLProcessor.class.getClassLoader().getResourceAsStream(DEFAULT_XML_DATA_FILE_NAME);
        InputStream dtdFileStream = XMLProcessor.class.getClassLoader().getResourceAsStream(DEFAULT_DTD_FILE_NAME);
        List<Node> res = loadXMLDataFileToDomNodes(dataFileStream,dtdFileStream);
        dataFileStream.close();
        dtdFileStream.close();
        return res;
    }

    // temporary
    public static List<Node> checkFileNameAndGetNodes(String xmlFileNameInXPath) throws Exception {
        if(DEFAULT_XML_DATA_FILE_NAME.equals(xmlFileNameInXPath)) {
            return loadDefaultDataFileFromResource();
        } else {
            throw new Exception("XML data file is not in resources");
        }
    }
    public static Document generateResultXMLRaw(List<Node> rawResult) throws ParserConfigurationException {
        DocumentBuilder bd = docBldFactory.newDocumentBuilder();
        Document outputDoc = bd.newDocument();
        if (rawResult.size() != 1) {
            throw new RuntimeException("size of raw result of xquery eva is not 1, cannot create doc directly");
        }
        Node onlyNode = rawResult.get(0);
        Node newNode = outputDoc.importNode(onlyNode, true);
        outputDoc.appendChild(newNode);
        return outputDoc;
    }
    public static Document generateResultXMLAddingResultEle(List<Node> rawResult) throws ParserConfigurationException {
        DocumentBuilder bd = docBldFactory.newDocumentBuilder();
        Document outputDoc = bd.newDocument();
        Element resultEle = outputDoc.createElement("RESULT");
        outputDoc.appendChild(resultEle);
        for(Node old: rawResult){
            try {
                Node newNode;
                newNode = outputDoc.importNode(old, true);
                resultEle.appendChild(newNode);
            } catch (DOMException e) {
                if (e.code != DOMException.NOT_SUPPORTED_ERR) {
                    throw e;
                }
//                Element specialEle = outputDoc.createElement("notImportableNode");
//                Text nodeNameText = outputDoc.createTextNode("NodeName:" + old.getNodeName());
//                specialEle.appendChild(nodeNameText);
//                resultEle.appendChild(specialEle);
            }

        }
        return outputDoc;
    }
    public static void writeXMLDoc(Document outputDoc, OutputStream oStream) throws TransformerException {
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(outputDoc),new StreamResult(oStream));}

    public static void generateResultXMLThenOutput(List<Node> rawResult, OutputStream oStream, boolean addResEle)
            throws ParserConfigurationException, TransformerException {
        Document doc = addResEle ? generateResultXMLAddingResultEle(rawResult) : generateResultXMLRaw(rawResult);
            writeXMLDoc(doc,oStream);
    }
}
