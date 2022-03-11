package com.rxcay.ucsd.cse232b;

import java.io.*;
import java.util.Arrays;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 3/11/22 9:23 AM
 * @description
 */
public class XQueryReWriter {
    static String rewriteToJoinXquery(String originFilePath, InputStream inputStreamToParse) {
        final String NO_CHANGE_MARK = "no change";
        String res = NO_CHANGE_MARK;
        if( NO_CHANGE_MARK.equals(res)) {
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
}
