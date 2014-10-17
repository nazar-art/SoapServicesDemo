package core.utils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.Map;

public class XmlUtil {

    public static final String INPUT_NODE = "input";

    public static final String DEFAULT_ENCODING = "UTF-8";

    private static final Logger LOGGER = Logger.getLogger(XmlUtil.class);

    protected static final ThreadLocal<TransformerFactory> transformerFactoryCache = new ThreadLocal<TransformerFactory>() {
        @Override
        protected TransformerFactory initialValue() {
            try {
                return TransformerFactory.newInstance();
            } catch (TransformerFactoryConfigurationError ignore) {
                return null; // no config
            }
        }
    };

    protected static final ThreadLocal<Transformer> emptyTransformerCache = new ThreadLocal<Transformer>() {
        @Override
        protected Transformer initialValue() {
            try {
                return transformerFactoryCache.get().newTransformer();
            } catch (TransformerConfigurationException ignore) {
                return null; // no config
            }
        }
    };

    private static final ThreadLocal<DocumentBuilderFactory> docBuilderFactoryCache = new ThreadLocal<DocumentBuilderFactory>() {
        @Override
        protected DocumentBuilderFactory initialValue() {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            return dbf;
        }
    };

    private static final ThreadLocal<DocumentBuilder> docBuilderCache = new ThreadLocal<DocumentBuilder>() {
        @Override
        protected DocumentBuilder initialValue() {
            try {
                return docBuilderFactoryCache.get().newDocumentBuilder();
            } catch (ParserConfigurationException ignore) {
                // No config
                return null;
            }
        }
    };

    private static final ThreadLocal<XPathFactory> xPathFactoryCache = new ThreadLocal<XPathFactory>() {
        @Override
        protected XPathFactory initialValue() {
            return XPathFactory.newInstance();
        }
    };

    private static final ThreadLocal<XMLInputFactory> xmlInputFactoryCache = new ThreadLocal<XMLInputFactory>() {
        @Override
        protected XMLInputFactory initialValue() {
            return XMLInputFactory.newInstance();
        }
    };

    public static Document generateDoc(String rootNode, Map<String, String> parameters) {
        Document paramsDoc = getDocument(rootNode);
        Node rootElement = paramsDoc.getFirstChild();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Element element = paramsDoc.createElement(key);
                element.setTextContent(parameters.get(key));
                rootElement.appendChild(element);
            }
        }
        return paramsDoc;
    }

    public static Document transform(String xsltPath, Map<String, String> parameters) throws TransformerException {

        Document paramsDoc = getDocument(INPUT_NODE);
        Node rootElement = paramsDoc.getFirstChild();
        if (parameters != null) {
            for (String key : parameters.keySet()) {
                Element element = paramsDoc.createElement(key);
                element.setTextContent(parameters.get(key));
                rootElement.appendChild(element);
            }
        }
        return transform(xsltPath, paramsDoc);
    }

    public static Document transform(String xsltPath, Document parameters) throws TransformerException {
        return transform(ClassLoader.getSystemResourceAsStream(xsltPath), parameters);
    }

    public static void transform(Source source, Result result) throws TransformerException {
        Transformer transformer = emptyTransformerCache.get();
        transformer.transform(source, result);
    }

    public static Document transform(InputStream xslt, Document parameters) throws TransformerException {
        Source xsltSource = new StreamSource(xslt);
        TransformerFactory factory = transformerFactoryCache.get();

        Transformer transformation = null;
        Templates template = factory.newTemplates(xsltSource);
        transformation = template.newTransformer();

        DOMResult result = new DOMResult();

        transformation.transform(new DOMSource(parameters), result);
        return (Document) result.getNode();
    }

    public static String getStringFromDocument(Document input) {
        Transformer transformer = emptyTransformerCache.get();
        Writer buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
        try {
            transformer.transform(new DOMSource(input), new StreamResult(buffer));
        } catch (TransformerException ignore) {
            // no such exceptions while transforming to String
            return null;
        }
        return buffer.toString();
    }

    public static byte[] getBytesFromDocument(Document input) {
        Transformer transformer = emptyTransformerCache.get();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Writer buffer = new OutputStreamWriter(stream);
        transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
        try {
            transformer.transform(new DOMSource(input), new StreamResult(buffer));
        } catch (TransformerException ignore) {
            // no such exceptions while transforming to String
            return null;
        }
        return stream.toByteArray();
    }

    public static Document parseDocument(String input) throws SAXException {
        DocumentBuilder builder = docBuilderCache.get();
        try {
            return builder.parse(new ByteArrayInputStream(input.getBytes()));
        } catch (IOException ignore) {
            LOGGER.error("Unable to parse document", ignore);
            // No files/etc. so no exceptions
            return null;
        }
    }

    public static Document parseDocument(File input) throws SAXException, IOException {
        DocumentBuilder builder = docBuilderCache.get();
        try {
            FileInputStream fis = new FileInputStream(input);
            return builder.parse(fis);
        } catch (IOException e) {
            LOGGER.error("Failed to read from input stream", e);
            throw e;
        }
    }

    public static Document parseDocument(InputStream input) throws SAXException, IOException {
        DocumentBuilder builder = docBuilderCache.get();
        return builder.parse(input);
    }

    public static Document getDocument(String rootElementName) {
        DocumentBuilder builder = docBuilderCache.get();
        Document doc = builder.newDocument();
        Element rootElement = doc.createElement(rootElementName);
        doc.appendChild(rootElement);
        return doc;
    }

    public static String getStringContentByXPath(Node parentNode, NamespaceContext context, String xPathExpression) throws XPathExpressionException {
        return (String) getByXPath(parentNode, context, xPathExpression, XPathConstants.STRING);
    }

    public static NodeList getNodeListByXPath(Node parentNode, NamespaceContext context, String xPathExpression) throws XPathExpressionException {
        return (NodeList) getByXPath(parentNode, context, xPathExpression, XPathConstants.NODESET);
    }

    public static Object getByXPath(Node parentNode, NamespaceContext context, String xPathExpression, QName type) throws XPathExpressionException {
        XPathFactory xPathFactory = xPathFactoryCache.get();
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(context);
        return xPath.evaluate(xPathExpression, parentNode, type);
    }

    public static NamespaceContext getNamespaceContext(String document) {
        XMLInputFactory factory = xmlInputFactoryCache.get();
        XMLEventReader evtReader;
        try {
            evtReader = factory.createXMLEventReader(new StringReader(document));

            while (evtReader.hasNext()) {
                XMLEvent event = evtReader.nextEvent();
                if (event.isStartElement()) {
                    return ((StartElement) event).getNamespaceContext();
                }
            }
        } catch (XMLStreamException e) {
            LOGGER.error("There is an Error in document", e);
        }
        return null;
    }

    public static Node getChildNodeByLocalName(String name, Node parent) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (name.equals(child.getLocalName())) {
                return child;
            }
        }
        return null;
    }
}
