package core.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressionUtils {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private CompressionUtils() {
    }

    public static void compressFileZip(String filePath, String zipPath) throws IOException {
        InputStream input = new FileInputStream(filePath);
        OutputStream output = new FileOutputStream(zipPath);
        compressZip(input, output);
    }

    public static byte[] compressZip(String text) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        InputStream input = new ByteArrayInputStream(text.getBytes(UTF8));
        try {
            compressZip(input, result);
            result.flush();
        } catch (IOException e) {
            // This exception shouldn't be thrown for in-memory operation
        }
        return result.toByteArray();
    }

    private static void compressZip(InputStream input, OutputStream output) throws IOException {
        try (ZipOutputStream zipStream = new ZipOutputStream(new BufferedOutputStream(output, 1024), UTF8); BufferedInputStream bis = new BufferedInputStream(input, 1024)) {
            ZipEntry entry = new ZipEntry("input.xml");
            zipStream.putNextEntry(entry);

            byte[] dataToWrite = new byte[1024];
            int length;
            while ((length = bis.read(dataToWrite)) > 0) {
                zipStream.write(dataToWrite, 0, length);
            }
        }
    }

}
