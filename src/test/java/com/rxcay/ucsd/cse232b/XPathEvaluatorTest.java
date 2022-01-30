package com.rxcay.ucsd.cse232b;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author rx_w@outlook.com
 * @version 1.0
 * @date 1/30/22 6:53 AM
 * @description
 */
public class XPathEvaluatorTest {
    final String XPATH_EX1 = "doc(\"j_caesar.xml\")//ACT/SCENE/SPEECH/SPEAKER";
    final String XPATH_EX2 = "doc(\"j_caesar.xml\")/ACT";
    final String XPATH_INVALID = "???XXX-THIs-is|om-invalid";
    final String XPATH_INVALID2 = "doc(\"no_exist.xml\")/ACT";
    public static InputStream getByteArrayIStreamWithUTF8(String s) {
        return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
    }
    @Test
    public void testEvaluateXPathNaive(){
        InputStream s = getByteArrayIStreamWithUTF8(XPATH_EX2);
        List<Node> res = XPathEvaluator.evaluateXPathWithoutException(s);
        Assert.assertNotNull(res);
    }

    @Test
    public void testEvaluateXPathEx2(){
        List<Node> res = XPathEvaluator
                .evaluateXPathWithoutException(getByteArrayIStreamWithUTF8(XPATH_EX2));
        Assert.assertNotNull(res);
        Assert.assertTrue(res.size() > 0);
    }
    @Test
    public void testEvaluateXPathInvalid(){
        List<Node> res = XPathEvaluator
                .evaluateXPathWithoutException(getByteArrayIStreamWithUTF8(XPATH_INVALID));
        Assert.assertNull(res);

    }

    @Test
    public void testEvaluateXPathInvalidXMLFile() {
        List<Node> res = XPathEvaluator
                .evaluateXPathWithoutException(getByteArrayIStreamWithUTF8(XPATH_INVALID2));
        Assert.assertNull(res);
    }
}
