package com.rxcay.ucsd.cse232b;

import com.rxcay.ucsd.cse232b.antlr4.XQueryLexer;
import com.rxcay.ucsd.cse232b.antlr4.XQueryParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 3/11/22 9:23 AM
 * @description
 */
public class XQueryReWriter {
    static String rewriteToJoinXquery(String originFilePath, InputStream inputStreamToParse) {
        String res = exeJoinWrite(inputStreamToParse);
        if( QEngineJoinReWriterVisitor.NO_CHANGE_MARK.equals(res)) {
            File oriFile = new File(originFilePath);
            return readToString(oriFile);
        }
        return res;
    }
    private static String readToString(File file) {
        long fileLength = file.length();
        byte[] fileContent = new byte[(int) fileLength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new String(fileContent);
    }

    static String exeJoinWrite(InputStream inputStream) {
        try {
            CharStream cs = CharStreams.fromStream(inputStream);
            XQueryLexer lexer = new XQueryLexer(cs);
            CommonTokenStream tks = new CommonTokenStream(lexer);
            XQueryParser parser = new XQueryParser(tks);
            parser.removeErrorListeners();
            QEngineJoinReWriterVisitor visitor = new QEngineJoinReWriterVisitor();
            String res = visitor.visit(parser.xq());
            return res;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
