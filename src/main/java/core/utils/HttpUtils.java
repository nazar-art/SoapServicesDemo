package core.utils;

import core.exception.CommonTestRuntimeException;
import core.logger.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class HttpUtils {

    private HttpUtils() {
    }

    private static String sendPost(HttpURLConnection con, String messageFilePath) throws IOException {
        con.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(con.getOutputStream());
        BufferedReader reader = IOUtils.getReader(messageFilePath);
        String line;

        while ((line = reader.readLine()) != null) {
            dos.writeBytes(line.toString());
            dos.writeBytes(System.lineSeparator());
        }

        dos.flush();
        dos.close();

        int code = con.getResponseCode();

        if (code != 200) {
            throw new CommonTestRuntimeException("Bad response code: " + code + " from URL: " + con.getURL().toString());
        } else {
            Logger.debug("Response code:\t" + code);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        con.disconnect();

        return response.toString();
    }

    public static String sendPost(String urlString, String messageFilePath) throws IOException {
        Logger.info("Sending POST request to '" + urlString + "' with XML file: '" + messageFilePath + "'");

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Accept-Charset", "UTF-8");
        con.setRequestProperty("charset", "UTF-8");

        return sendPost(con, messageFilePath);
    }

    public static String sendSoap(String urlString, String method, String messageFilePath) throws IOException {
        Logger.info("Sending POST SOAP request to '" + urlString + "' with XML file: '" + messageFilePath + "'");

        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "text/xml;charset=UTF-8");
        con.setRequestProperty("SOAPAction", method);

        return sendPost(con, messageFilePath);
    }
}
