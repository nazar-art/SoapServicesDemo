package core.soapservice.util;

import core.utils.XmlUtil;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

public class SoapEnvelopeUtil {

    public static Document packEnvelope(String version, Document data) throws SOAPException {
        MessageFactory factory = MessageFactory.newInstance(version);
        SOAPMessage message = factory.createMessage();
        message.getSOAPBody().addDocument(data);
        message.saveChanges();
        DOMResult result = new DOMResult();
        try {
            XmlUtil.transform(message.getSOAPPart().getContent(), result);
        } catch (TransformerException ignore) {
            // won't throw because we transform from a DOM to DOM
        }
        return (Document) result.getNode();
    }

    public static Document unpackEnvelope(String version, Document message) throws SOAPException, TransformerException {
        SAAJResult result = new SAAJResult(version);
        XmlUtil.transform(new DOMSource(message), result);
        SOAPEnvelope envelope = (SOAPEnvelope) result.getResult();
        SOAPBody body = envelope.getBody();
        if (body.getChildElements().hasNext()) {
            return envelope.getBody().extractContentAsDocument();
        } else {
            return null;
        }
    }
}
