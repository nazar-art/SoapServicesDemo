package core.utils;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.nio.charset.Charset;

public final class Base64Utils {

    public static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

    private Base64Utils() {
    }

    public static String encode(byte[] in) {
        return Base64.encode(in);
    }

    public static String encode(String in) {
        return Base64.encode(in.getBytes(DEFAULT_ENCODING));
    }
}
