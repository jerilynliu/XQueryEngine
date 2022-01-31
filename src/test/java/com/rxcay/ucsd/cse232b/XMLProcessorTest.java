package com.rxcay.ucsd.cse232b;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 6:07 PM
 * @description
 */
public class XMLProcessorTest {
    public static List<Node> defaultXMLDomDoc;

    @BeforeClass
    public static void setDefaultXMLDomDoc(){
        try {
            defaultXMLDomDoc = XMLProcessor
                    .checkFileNameAndGetNodes(XMLProcessor.DEFAULT_XML_DATA_FILE_NAME);

        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testGetNodes()
    {   try {
            List<Node> r = XMLProcessor
                    .checkFileNameAndGetNodes(XMLProcessor.DEFAULT_XML_DATA_FILE_NAME);
            Assert.assertTrue(r.size() != 0);
            Node docNode = r.get(0);
            Assert.assertEquals(docNode.getNodeType(), Node.DOCUMENT_NODE);
//            Document d = (Document) docNode;
//            System.out.println(d.getChildNodes().item(1).getNodeType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGenerateResult(){
           try {
            OutputStream o = new ByteArrayOutputStream();
            XMLProcessor.generateResultXMLThenOutput(defaultXMLDomDoc, o);
            String s = o.toString();
            Assert.assertTrue(s.contains("result"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
