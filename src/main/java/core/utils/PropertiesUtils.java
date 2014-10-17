/**
 *
 */
package core.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtils {

    public static Properties loadProperties(String propFilePath) throws IOException {
        Properties prop = new Properties();
        InputStream in = new FileInputStream(propFilePath);
        prop.load(in);

        return prop;
    }

    public static void appendProperty(String key, String value, String separator) {
        if (System.getProperty(key) != null) {
            System.setProperty(key, System.getProperty(key) + separator + value);
        } else {
            System.setProperty(key, value);
        }
    }
}
