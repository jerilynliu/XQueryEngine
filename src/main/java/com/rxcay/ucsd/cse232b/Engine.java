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
        final String EVA_OPTION = "eva";
        final String REW_OPTION = "rew";
        if(args.length != 2){
            System.out.printf("wrong args number: expect 2 received %d \n", args.length);
            System.out.println("usage java -jar [eva | rew] [filename]");
        }
        switch (args[0]) {
            case REW_OPTION:
                reWriteXQueryToJoin(args[1]);
                break;
            case EVA_OPTION:
                xQueryEvaluate(args[1]);
                break;
            default:
                System.out.println("invalid option argument use [eva | rew]");
        }

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
        writeResultToFile(rawEvaluateRes, "xquery_result.xml", true);
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
    private static void reWriteXQueryToJoin(String xQueryFilePath) {
        String reWriteRes = "";
        try (InputStream xQueryIStream = new FileInputStream(xQueryFilePath)){
            reWriteRes = XQueryReWriter.rewriteToJoinXquery(xQueryFilePath, xQueryIStream);
        }catch (IOException e) {
            System.err.println("open xQuery file failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("rewrite xquery failed : " + e.getMessage());
        }
        File file =new File("rewrite-" + xQueryFilePath);

        //if file doesnt exists, then create it
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        //true = append file
        try (            FileWriter fileWriter = new FileWriter(file.getName());
                         BufferedWriter bufferWriter = new BufferedWriter(fileWriter)){
            bufferWriter.write(reWriteRes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
