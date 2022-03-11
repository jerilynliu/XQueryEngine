package com.rxcay.ucsd.cse232b;

import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
            //"XQuery_some.txt"
    );
    List<String> problemXQueryFiles = Arrays.asList(
            //"XQuery_some.txt" // very slow
            "demo_XQuery5_NPEBug.txt" // NPE while demo
    );

    // added new test file for M3 (XjoinQuery1 is supposed to be the rewritten result of an original query)
    List<String> XjoinQueryFiles = Arrays.asList(
            "XjoinQuery1.txt"
    );

    @Test
    public void testXQueryPrintOutput(){
        for (String fileName: XjoinQueryFiles){
            System.out.println(fileName + " XML result:");
            try (
                    InputStream testXQueryIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    OutputStream om = new ByteArrayOutputStream()
                    ) {
                assert testXQueryIStream != null;
                List<Node> rawResult = XQueryEvaluator.evaluateXQuery(testXQueryIStream);
                XMLProcessor.generateResultXMLThenOutput(rawResult, om, true);
                System.out.println(om);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

    List<String> originXQueryFilesToJoin = Arrays.asList(
            "Ori_xquery1.txt"
    );

    List<String> anotherCheckJoinList = Arrays.asList(
            "OCheck_XQuery1.txt",
           "OCheck_XQuery2.txt"
    );
    @Test
    public void textXQueryJoinReWrite(){
        for (String fileName: anotherCheckJoinList){
            System.out.println(fileName + " re write result:");
            try (
                    InputStream testXQueryIStream = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
                    InputStream testXQueryIStream2 = EngineTest.class.getClassLoader().getResourceAsStream(fileName);
            ) {
                assert testXQueryIStream != null;
                String res = XQueryReWriter.exeJoinWrite(testXQueryIStream);
                System.out.println(res);
                System.out.println("join result: ");
                InputStream joined = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
                List<Node> jResult = XQueryEvaluator.evaluateXQuery(joined);
                OutputStream jom = new ByteArrayOutputStream();
                XMLProcessor.generateResultXMLThenOutput(jResult, jom, true);
                System.out.println(jom);
//                System.out.println("naive result: ");
//                OutputStream om = new ByteArrayOutputStream();
//                List<Node> rawResult = XQueryEvaluator.evaluateXQuery(testXQueryIStream2);
//                XMLProcessor.generateResultXMLThenOutput(rawResult, om, true);
//                System.out.println(om);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}

