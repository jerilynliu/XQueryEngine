package com.rxcay.ucsd.cse232b;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for simple Engine.
 */
public class EngineTest {
//    private static class XPathQueryValidateCase {
//        public String xpathFileName;
//        public String standardQueryResFileName;
//        public static XPathQueryValidateCase from (String xpathFileName, String standardQueryResFileName){
//            XPathQueryValidateCase r = new XPathQueryValidateCase();
//            r.xpathFileName = xpathFileName;
//            r.standardQueryResFileName = standardQueryResFileName;
//            return r;
//        }
//    }
//    private static List<XPathQueryValidateCase> testCases;
//    @BeforeClass
//    public static void setTestCases(){
//        testCases = Arrays.asList(
////                XPathQueryValidateCase.from(),
////                XPathQueryValidateCase.from()
//        );
//    }
//    public void validateXPathQueryWithCases() {
//        for (XPathQueryValidateCase testCase : testCases){
//            try(
//                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(testCase.xpathFileName)
//            ) {
//
//            } catch (Exception e){
//                throw new RuntimeException(e);
//            }
//        }
//    }
    List<String> xPathFiles = Arrays.asList(
        "XPath1.txt",
        "XPath2.txt",
        "XPath3.txt",
        "XPath4.txt",
        "XPath5.txt"
    );
    @Test
    @Ignore // no files output now.
    public void testXPathQueryOutputFile(){
        for (String fileName: xPathFiles) {
            String prefix = fileName.substring(0, fileName.lastIndexOf("."));
            try (
                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream testXPathResultOStream = new FileOutputStream(prefix + "_result.xml")
            ) {
                List<Node> rawResult = XPathEvaluator.evaluateXPath(testXPathIStream);
                XMLProcessor.generateResultXMLThenOutput(rawResult, testXPathResultOStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    public void textXPathQueryPrintOutput(){
        for (String fileName: xPathFiles) {
            System.out.println(fileName + " XML result:");
            try (
                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream testXPathResultOStream = new ByteArrayOutputStream()
            ) {
                List<Node> rawResult = XPathEvaluator.evaluateXPath(testXPathIStream);
                XMLProcessor.generateResultXMLThenOutput(rawResult, testXPathResultOStream);
                assert testXPathIStream != null;
                System.out.println(testXPathResultOStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

