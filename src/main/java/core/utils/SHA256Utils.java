/**
 *
 */
package core.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Utils {

    private static MessageDigest getSHAinstance() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256");
    }

    public static String getHash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = getSHAinstance();
        md.update(input.getBytes());

        byte byteData[] = md.digest();

        // convert the byte to hex format
        StringBuffer hexString = new StringBuffer();

        for (byte aByteData : byteData) {
            String hex = Integer.toHexString(0xff & aByteData);

            if (hex.length() == 1) {
                hexString.append('0');
            }

            hexString.append(hex);
        }

        return hexString.toString();
    }
}
