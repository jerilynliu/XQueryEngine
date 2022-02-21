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
        if(args.length != 1){
            System.out.printf("wrong args number: expect 1 received %d \n", args.length);
            System.out.println("usage java -jar ");
        }
        xQueryEvaluate(args[0]);
    }

    private static void xPathEvaluate(String xPathFilePath) {
        List<Node> rawEvaluateRes = null;
        try(
                InputStream xPathIStream = new FileInputStream(xPathFilePath)
        ) {
             rawEvaluateRes = XPathEvaluator.evaluateXPathWithoutExceptionPrintErr(xPathIStream);
        } catch (IOException e) {
            System.err.println("open xPath file failed: " + e.getMessage());
        }
        if( rawEvaluateRes == null ){
            System.err.println("XPath evaluation failed. No result file generated.");
            return;
        }
        System.out.println("XPath evaluation finished, writing result file...");
        writeResultToFile(rawEvaluateRes, "xpath_result.xml", true);
    }

    private static void xQueryEvaluate(String xQueryFilePath) {
        List<Node> rawEvaluateRes = null;
        try (InputStream xQueryIStream = new FileInputStream(xQueryFilePath)){
            rawEvaluateRes = XQueryEvaluator.evaluateXQueryWithoutExceptionPrintErr(xQueryIStream);

        }catch (IOException e) {
            System.err.println("open xQuery file failed: " + e.getMessage());
        }
        if (rawEvaluateRes == null) {
            System.err.println("XQuery evaluation failed. No result file generated.");
            return;
        }
        System.out.println("XQuery evaluation finished, writing result file...");
        writeResultToFile(rawEvaluateRes, "xquery_result.xml", false);
    }

    private static void writeResultToFile(List<Node> rawRes, String fileName, boolean addResEle) {
        try(
                OutputStream resultXMLOStream = new FileOutputStream(fileName)
        ) {
            XMLProcessor.generateResultXMLThenOutput(rawRes, resultXMLOStream, addResEle);
        }  catch (IOException e) {
            System.err.println("open result file failed: " + e.getMessage());
        } catch (ParserConfigurationException | TransformerException e){
            System.err.println("generating XML or transforming failed:" + e.getMessage());
        }
        catch (Exception e){
            System.err.println("runtime exception while generating/writing result:" + e.getMessage());
        }
    }

}
