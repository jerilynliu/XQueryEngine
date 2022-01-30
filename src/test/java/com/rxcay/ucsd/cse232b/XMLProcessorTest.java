package com.rxcay.ucsd.cse232b;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.w3c.dom.Node;

import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 6:07 PM
 * @description
 */
public class XMLProcessorTest extends TestCase {
    public XMLProcessorTest( String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( XMLProcessorTest.class );
    }

    public void testXMLProcessor()
    {
        List<Node> r = XMLProcessor.loadDefaultDataFile();
        Node.ELEMENT_NODE
        assertTrue(r.size()!=0);
    }
}
