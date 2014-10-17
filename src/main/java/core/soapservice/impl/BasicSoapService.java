package core.soapservice.impl;

import com.google.common.net.MediaType;
import core.httpservice.HttpService;
import core.httpservice.entities.HttpMethod;
import core.httpservice.entities.ServiceResponseMessage;
import core.httpservice.exceptions.HttpServiceException;
import core.httpservice.exceptions.MediaTypeConversionException;
import core.httpservice.impl.BasicHttpService;
import core.httpservice.serializers.Deserializer;
import core.httpservice.serializers.Serializer;
import core.logger.Logger;
import core.soapservice.SoapService;
import core.soapservice.entities.SoapMessageRq;
import core.soapservice.entities.SoapMessageRs;
import core.soapservice.exceptions.SoapServiceException;
import core.soapservice.util.SoapEnvelopeUtil;
import core.utils.XmlUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MIME;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.wsdl.*;
import javax.wsdl.extensions.ExtensibilityElement;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPOperation;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.wsdl.extensions.soap12.SOAP12Operation;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

public class BasicSoapService implements SoapService {

    private static final Map<String, String> SOAP_1_1_CONTENT_TYPE = Collections.singletonMap(MIME.CONTENT_TYPE, SOAPConstants.SOAP_1_1_CONTENT_TYPE + "; charset=utf-8");
    private static final Map<String, String> SOAP_1_2_CONTENT_TYPE = Collections.singletonMap(MIME.CONTENT_TYPE, SOAPConstants.SOAP_1_2_CONTENT_TYPE + "; charset=utf-8");

    private static final MediaType SOAP_1_1_TYPE = MediaType.parse(SOAPConstants.SOAP_1_1_CONTENT_TYPE);
    private static final MediaType SOAP_1_2_TYPE = MediaType.parse(SOAPConstants.SOAP_1_2_CONTENT_TYPE);
    private static final MediaType HTML_TYPE = MediaType.parse("text/html");

    private static final Serializer SOAP_SERIALIZER = new Serializer() {
        @Override
        public boolean handles(MediaType type) {
            return SOAP_1_1_TYPE.is(type.withoutParameters()) || SOAP_1_2_TYPE.is(type.withoutParameters());
        }

        @Override
        public byte[] serialize(Object rq) throws MediaTypeConversionException {
            return XmlUtil.getBytesFromDocument((Document) rq);
        }
    };

    private static final Deserializer SOAP_DESERIALIZER = new Deserializer() {
        @Override
        public boolean handles(MediaType type) {
            return SOAP_1_1_TYPE.is(type.withoutParameters()) || SOAP_1_2_TYPE.is(type.withoutParameters());
        }

        @Override
        public Object deserialize(byte[] data) throws MediaTypeConversionException {
            try {
                return XmlUtil.parseDocument(new ByteArrayInputStream(data));
            } catch (SAXException | IOException e) {
                throw new MediaTypeConversionException("Deserializetion error. ", e);
            }
        }
    };

    // To handle common error such as 404 and 403
    private static final Deserializer HTML_DESERIALIZER = new Deserializer() {
        @Override
        public boolean handles(MediaType type) {
            return HTML_TYPE.is(type.withoutParameters());
        }

        @Override
        public Object deserialize(byte[] data) throws MediaTypeConversionException {
            return new String(data);
        }
    };

    private Definition wsdl;
    private String serviceName;
    private HttpService httpService;
    private String protocolVersion;
    private Map<String, String> methods;

    @SuppressWarnings("unchecked")
    public BasicSoapService(String wsdlUrl, String serviceName, HttpClient client) throws IOException, WSDLException, SoapServiceException {
        wsdl = parseWsdl(wsdlUrl);
        this.serviceName = serviceName;
        Service service = getService(serviceName);
        Port port = (Port) service.getPorts().values().iterator().next();
        List<ExtensibilityElement> extensions = port.getExtensibilityElements();
        for (ExtensibilityElement extension : extensions) {
            Map<String, String> headers = null;
            if (extension instanceof SOAPAddress) {
                headers = SOAP_1_1_CONTENT_TYPE;
                this.protocolVersion = SOAPConstants.SOAP_1_1_PROTOCOL;
            }
            if (extension instanceof SOAP12Address) {
                headers = SOAP_1_2_CONTENT_TYPE;
                this.protocolVersion = SOAPConstants.SOAP_1_2_PROTOCOL;
            }
            this.httpService = new BasicHttpService(client, ((SOAPAddress) extension).getLocationURI(), headers, Collections.singletonList(SOAP_SERIALIZER), Arrays.asList(SOAP_DESERIALIZER, HTML_DESERIALIZER));
            break;
        }
        this.methods = getMethods(port);
    }

    @Override
    public Collection<String> getMethodNames() throws SoapServiceException {
        return methods.keySet();
    }

    @Override
    public SoapMessageRs executeMethod(String method, SoapMessageRq message) throws HttpServiceException, SoapServiceException {
        checkMethodExists(method);

        Document envelope;
        try {
            envelope = SoapEnvelopeUtil.packEnvelope(protocolVersion, message.getPayload());
        } catch (SOAPException e) {
            throw new SoapServiceException("Bad input document");
        }
        Logger.operation("Executing method '" + method + "' of service '" + serviceName + "'");

        Map<String, String> messageHeaders = new HashMap<String, String>();
        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(protocolVersion)) {
            messageHeaders.put("SOAPAction", methods.get(method));
        }

        ServiceResponseMessage result = httpService.execute(HttpMethod.POST, messageHeaders, null, envelope);

        if (!result.isSuccess() && !(result.getResult() instanceof Document)) {
            throw new SoapServiceException("Bad response: " + result.getStatusCode() + " - " + result.getStatusMessage());
        }

        try {
            return new SoapMessageRs(result.getStatusCode(), result.getStatusMessage(), result.isSuccess(), SoapEnvelopeUtil.unpackEnvelope(protocolVersion, (Document) result.getResult()));
        } catch (SOAPException | TransformerException e) {
            throw new SoapServiceException("Bad document received. ");
        }
    }

    private Definition parseWsdl(String wsdlUri) throws WSDLException {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        return reader.readWSDL(wsdlUri);
    }

    @SuppressWarnings("unchecked")
    private Service getService(String serviceName) throws SoapServiceException {
        Map<String, Service> services = (Map<String, Service>) wsdl.getServices();
        if (services.size() <= 0) {
            throw new SoapServiceException("No services found in WSDL" + wsdl.getQName());
        }
        for (Service service : services.values()) {
            if (service.getQName().getLocalPart().equals(serviceName)) {
                return service;
            }
        }
        throw new SoapServiceException("No services named '" + serviceName + "' found in WSDL" + wsdl.getQName());
    }

    private Map<String, String> getMethods(Port port) throws WSDLException, SoapServiceException {
        Binding wsdlBinding = port.getBinding();
        if (wsdlBinding == null) {
            throw new SoapServiceException("Binding not found: " + port.getBinding().getQName().toString());
        }
        Map<String, String> result = new HashMap<String, String>();
        for (Object obOperation : wsdlBinding.getBindingOperations()) {
            BindingOperation bo = (BindingOperation) obOperation;
            String methodName = bo.getName();
            String action = null;
            for (Object obExElement : bo.getExtensibilityElements()) {
                ExtensibilityElement exElement = (ExtensibilityElement) obExElement;
                if (exElement instanceof SOAPOperation) {
                    action = ((SOAPOperation) exElement).getSoapActionURI();
                }
                if (exElement instanceof SOAP12Operation) {
                    action = ((SOAP12Operation) exElement).getSoapActionURI();
                }
                if (action != null) {
                    break;
                }
            }
            result.put(methodName, action);
        }
        return result;
    }

    private void checkMethodExists(String method) throws SoapServiceException {
        if (!getMethodNames().contains(method)) {
            throw new SoapServiceException("An attempt to call undefined method '" + method + "'");
        }
    }
}
