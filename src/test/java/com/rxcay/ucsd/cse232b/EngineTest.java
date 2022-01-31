package com.rxcay.ucsd.cse232b;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple Engine.
 */
public class EngineTest {
    private static class XPathQueryTestCase {
        public String xpathFileName;
        public String standardQueryResFileName;
        public static XPathQueryTestCase from (String xpathFileName,String standardQueryResFileName){
            XPathQueryTestCase r = new XPathQueryTestCase();
            r.xpathFileName = xpathFileName;
            r.standardQueryResFileName = standardQueryResFileName;
            return r;
        }
    }
    private static List<XPathQueryTestCase> testCases;
    @BeforeClass
    public static void setTestCases(){
        testCases = Arrays.asList(
//                XPathQueryTestCase.from(),
//                XPathQueryTestCase.from()
        );
    }
    @Test
    public void testXPathQueryOutput(){
        List<String> xPathFiles = Arrays.asList(
                "XPath1.txt",
                "XPath2.txt",
                "XPath3.txt",
                "XPath4.txt",
                "XPath5.txt"
                );
        for (String fileName: xPathFiles) {
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            try (
                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream testXPathResultOStream = new FileOutputStream(prefix + "_result.xml")
            ) {
                List<Node> rawResult;
                rawResult = XPathEvaluator.evaluateXPath(testXPathIStream);
                if(rawResult == null) {
                    throw new Exception("evaluation failed. No file.");
                }
                XMLProcessor.generateResultXMLThenOutput(rawResult, testXPathResultOStream);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public void validateXPathQueryWithCases() {
//        for (XPathQueryTestCase testCase : testCases){
//            try(
//                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(testCase.xpathFileName)
//            ) {
//
//            } catch (Exception e){
//                throw new RuntimeException(e);
//            }
//        }
//    }
}

