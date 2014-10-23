/**
 *
 */
package core.utils;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StringUtils {

    public static String buildString(Object... args) {
        StringBuilder blr = new StringBuilder();

        for (Object arg : args) {
            blr.append(arg);
        }

        return blr.toString();
    }

    public static String replaceValues(String input, Map<String, ?> map) {
        StringWriter resultSw = new StringWriter(2048);
        VelocityContext context = new VelocityContext(map);
        Velocity.evaluate(context, resultSw, "VELOCITY", new StringReader(input));
        resultSw.flush();
        String result = resultSw.toString();
        return result;
    }

    public static List<String> replaceValues(List<String> input, Map<String, ?> map) {
        List<String> result = new ArrayList<>();

        for (String line : input) {
            result.add(replaceValues(line, map));
        }

        return result;
    }
}
