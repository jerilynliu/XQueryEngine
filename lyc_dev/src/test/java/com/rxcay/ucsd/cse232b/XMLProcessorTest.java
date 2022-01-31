package com.rxcay.ucsd.cse232b;

import org.junit.Test;
import org.junit.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 6:07 PM
 * @description
 */
public class XMLProcessorTest {

    @Test
    public void testXMLProcessor()
    {   try {
            List<Node> r = XMLProcessor.loadDefaultDataFileFromResource();
            Assert.assertTrue(r.size() != 0);
            Node docNode = r.get(0);
            Assert.assertEquals(docNode.getNodeType(), Node.DOCUMENT_NODE);
            Document d = (Document) docNode;
            System.out.println(d.getChildNodes().item(0));
        } catch (Exception e) {
            throw new RuntimeException(e);
    }

    }
}
