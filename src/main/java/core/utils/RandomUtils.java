/**
 *
 */
package core.utils;

import java.util.Random;

public class RandomUtils {

    public static String generateNumberString(int lenght) {
        Random random = new Random();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < lenght; i++) {
            stringBuffer.append(random.nextInt(10));
        }

        return stringBuffer.toString();
    }

    public static String generateCharString(int lenght) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder stringBuffer = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < lenght; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuffer.append(c);
        }
        return stringBuffer.toString();
    }
}
