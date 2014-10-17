package core.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ModifyXMLFile {
    private static Node id;

    private static Document openXMLFile(String filePath) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(filePath);
        return doc;
    }

    private static void saveEditedXMLFile(Document doc, String filePath) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filePath));
        transformer.transform(source, result);
    }

    public static void update(String filePath, HashMap<String, String> dataToUpdate) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        Document doc = openXMLFile(filePath);
        for (Map.Entry<String, String> entry : dataToUpdate.entrySet()) {
            id = doc.getElementsByTagName(entry.getKey()).item(0);
            id.setTextContent(entry.getValue());
        }
        saveEditedXMLFile(doc, filePath);
    }
}
