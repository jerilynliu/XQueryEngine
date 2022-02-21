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
            // manually check
        "XPath1.txt", //OK
        "XPath2.txt", //OK
        "XPath3.txt", //OK
        "XPath4.txt", //OK
        "XPath5.txt", //OK
        "NEW_XPATH1.txt", //OK
        "NEW_XPATH2.txt",  // seems OK, out of order
        "NEW_XPATH3.txt", //OK
        "NEW_XPATH4.txt",
        "NEW_XPATH5.txt" // seems OK, ood
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
                XMLProcessor.generateResultXMLThenOutput(rawResult, testXPathResultOStream, true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Test
    @Ignore
    public void testXPathQueryPrintOutput(){
        for (String fileName: xPathFiles) {
            System.out.println(fileName + " XML result:");
            try (
                    InputStream testXPathIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream testXPathResultOStream = new ByteArrayOutputStream()
            ) {
                List<Node> rawResult = XPathEvaluator.evaluateXPath(testXPathIStream);
                XMLProcessor.generateResultXMLThenOutput(rawResult, testXPathResultOStream, true);
                assert testXPathIStream != null;
                System.out.println(testXPathResultOStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    List<String> xQueryFiles = Arrays.asList(
            "XQuery1.txt",
            "XQuery2.txt",
            "Extra_XQuery1.txt",
            "Extra_XQuery2.txt",
            "Extra_XQuery3.txt",
            "Extra_XQuery4.txt"
    );

    @Test
    public void testXQueryPrintOutput(){
        for (String fileName: xQueryFiles){
            System.out.println(fileName + " XML result:");
            try (
                    InputStream testXQueryIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream om = new ByteArrayOutputStream()
                    ) {
                assert testXQueryIStream != null;
                List<Node> rawResult = XQueryEvaluator.evaluateXQuery(testXQueryIStream);
                XMLProcessor.generateResultXMLThenOutput(rawResult, om, false);
                System.out.println(om);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}

