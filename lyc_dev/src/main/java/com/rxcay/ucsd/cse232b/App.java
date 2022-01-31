package com.rxcay.ucsd.cse232b;


import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static InputStream getByteArrayIStreamWithUTF8(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
    public static void main( String[] args )
    {
        final String XPATH_INVALID = "???XXX-THIs-is|om-invalid";
        List<Node> res = XPathEvaluator
                .evaluateXPathWithoutException(getByteArrayIStreamWithUTF8(XPATH_INVALID));
    }
}
