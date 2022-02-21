package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XQueryLexer;
import com.rxcay.ucsd.cse232b.antlr4.XQueryParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 2/20/22 3:49 PM
 * @description
 */
public class XQueryEvaluator {
    public static List<Node> evaluateXQuery(InputStream xQueryIStream) throws IOException, ParserConfigurationException {
        CharStream cs = CharStreams.fromStream(xQueryIStream);
        XQueryLexer lexer = new XQueryLexer(cs);
        CommonTokenStream tks = new CommonTokenStream(lexer);
        XQueryParser parser = new XQueryParser(tks);
        parser.removeErrorListeners();
        DocumentBuilder bd = XMLProcessor.docBldFactory.newDocumentBuilder();
        QEngineXQueryVisitor visitor = new QEngineXQueryVisitor(bd.newDocument());
        List<Node> res = visitor.visit(parser.xq());
        if (res == null) {
            throw new RuntimeException("visitor failed to get result.");
        }
        return res;
    }

    public static List<Node> evaluateXQueryWithoutExceptionPrintErr(InputStream xQueryIStream) {
        List<Node> res = null;
        try {
            res = evaluateXQuery(xQueryIStream);
        } catch (Exception e) {
            System.err.println("XQuery evaluation terminated with error: " + e.getMessage());
        }
        return res;
    }

}
