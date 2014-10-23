/**
 *
 */
package core.config;

import java.util.ResourceBundle;

/**
 * @author Pavel_Makhakhei
 */
public class AppConfig {
    private static ResourceBundle bundle = null;

    private static void loadResources() {
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("config/app");
        }
    }

    public static String getValue(String key) {
        loadResources();
        return bundle.getString(key);
    }
}
