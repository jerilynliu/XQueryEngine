package com.rxcay.ucsd.cse232b;


import org.w3c.dom.Node;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.List;

public class Engine
{

    public static void main( String[] args )
    {
       String xPathFilePath = args[0];
       xPathEvaluate(xPathFilePath);
    }

    private static void xPathEvaluate(String xPathFilePath) {
        List<Node> rawEvaluateRes = null;
        try(
                InputStream xPathIStream = new FileInputStream(xPathFilePath);
        ) {
             rawEvaluateRes = XPathEvaluator.evaluateXPathWithoutException(xPathIStream);
        } catch (IOException e) {
            System.err.println("open xPath file failed: " + e.getMessage());
        }
        if( rawEvaluateRes == null ){
            System.err.println("XPath evaluation actually failed. No result file generated.");
            return;
        }
        try(
                OutputStream resultXMLOStream = new FileOutputStream("xpath_result.xml")
        ) {
            XMLProcessor.generateResultXMLThenOutput(rawEvaluateRes, resultXMLOStream);
        }  catch (IOException | ParserConfigurationException | TransformerException e) {
           System.err.println("after evaluation, write result file failed: " + e.getMessage());
        } catch (Exception e){
            System.err.println("runtime exception while writing result:" + e.getMessage());
        }
    }
}
