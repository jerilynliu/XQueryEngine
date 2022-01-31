package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XPathLexer;
import com.rxcay.ucsd.cse232b.antlr4.XPathParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/29/22 5:01 PM
 * @description
 */
public class XPathEvaluator {
    public static List<Node> evaluateXPath(InputStream xPathStream) throws Exception{
        CharStream cs = CharStreams.fromStream(xPathStream);
        XPathLexer lexer = new XPathLexer(cs);
        CommonTokenStream tks = new CommonTokenStream(lexer);
        XPathParser parser = new XPathParser(tks);
        parser.removeErrorListeners();
        QEngineXPathVisitor visitor = new QEngineXPathVisitor();
        return visitor.visit(parser.ap());
    }

    public static List<Node> evaluateXPathWithoutException(InputStream xPathStream) {
        List<Node> res= null;
        try {
            res = evaluateXPath(xPathStream);
        } catch (Exception e){
            System.err.println("XPath evaluation terminated with error: " + e.getMessage());
            //e.printStackTrace();
        }
        return res;
    }

}
