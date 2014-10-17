package core.utils;

import core.exception.CommonTestRuntimeException;
import core.logger.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class IOUtils {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    private IOUtils() {
    }

    public static FileInputStream getFileInputStream(String filePath) {
        FileInputStream is = null;

        try {
            is = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            Logger.error("File '" + filePath + "' not found! Exception=" + e.getMessage());
            throw new CommonTestRuntimeException(e);
        }

        return is;
    }

    public static File createFolder(String folderPath) {
        deleteFolder(folderPath);
        File folder = new File(folderPath);

        if (!folder.mkdir()) {
            Logger.error("Failed to create directory - " + folderPath);
        }

        return folder;
    }

    public static void deleteFolder(String folderPath) {
        File folder = new File(folderPath);

        if (folder.listFiles() != null) {
            for (File file : folder.listFiles()) {
                file.delete();
            }
        }

        if (folder.exists()) {
            folder.delete();
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            file.delete();
        }
    }

    public static void replaceOldFileOnNew(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        oldFile.delete();
        File newFile = new File(newFilePath);
        newFile.renameTo(oldFile);
    }

    public static void copyFile(String fromPath, String toPath) {
        File from = new File(fromPath);
        File to = new File(toPath);

        try {
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            Logger.error("Copy failed - fromPath=" + fromPath + ", toPath=" + toPath, new CommonTestRuntimeException(e));
        }
    }

    public static void saveFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = getWriter(filePath, false)) {
            writer.write(content);
        } catch (Exception e) {
            Logger.error("saveFile throw Exception=" + e.getMessage());
            throw new CommonTestRuntimeException(e);
        }
    }

    public static void appendFileToFile(String fromPath, String toPath) throws IOException {
        try (BufferedReader reader = getReader(fromPath); BufferedWriter writer = getWriter(toPath, true)) {
            String line;
            {
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.write("\n");
                }
            }
        } catch (Exception e) {
            Logger.error("appendFileToFile throw Exception=" + e.getMessage());
            throw new CommonTestRuntimeException(e);
        }
    }

    public static void appendFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
        fileWriter.append(content);
        bufferFileWriter.close();
    }

    public static void replaceFileContent(String filePath, Map<String, ?> replacePairs) throws IOException {
        String tmpFilePath = File.createTempFile("tmp", ".dat").getCanonicalPath();

        Context context = new VelocityContext(replacePairs);
        try (Writer writer = getWriter(tmpFilePath, false); Reader reader = getReader(filePath)) {
            Velocity.evaluate(context, writer, "VELOCITY", reader);
        }

        replaceOldFileOnNew(filePath, tmpFilePath);
    }

    public static BufferedReader getReader(String filePath) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(filePath), UTF8));
    }

    public static BufferedWriter getWriter(String filePath, boolean append) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, append), UTF8));
    }

    public static String generateXMLFileName(String name) {
        return name + ".xml";
    }

    public static String readFileIntoString(String filePath) throws IOException {
        BufferedReader reader = getReader(filePath);
        String line = reader.readLine();
        StringBuffer buffer = new StringBuffer();

        while (line != null) {
            buffer.append(line);
            buffer.append(System.lineSeparator());
            line = reader.readLine();
        }

        reader.close();
        return buffer.toString();
    }

    public static List<String> readFileIntoStrings(String filePath) throws IOException {
        BufferedReader reader = getReader(filePath);
        String line = reader.readLine();
        List<String> strings = new ArrayList<String>();

        while (line != null) {
            strings.add(line);
            line = reader.readLine();
        }

        return strings;
    }

    public static String getAbsoluteFilePath(String partPath) {
        return new File(partPath).getAbsolutePath();
    }
}
