package core.guice;

import com.google.common.net.MediaType;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import core.httpservice.HttpService;
import core.httpservice.exceptions.MediaTypeConversionException;
import core.httpservice.impl.BasicHttpService;
import core.httpservice.serializers.Deserializer;
import core.httpservice.serializers.Serializer;
import core.soapservice.SoapService;
import core.soapservice.exceptions.SoapServiceException;
import core.soapservice.impl.BasicSoapService;
import core.utils.XmlUtil;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.mime.MIME;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.w3c.dom.Document;

import javax.wsdl.WSDLException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WebServiceModel extends AbstractModule {

    private static final MediaType HTML_TYPE = MediaType.parse("text/html");
    private static final MediaType XML_TYPE = MediaType.parse("text/xml");
    private static final MediaType TEXT_TYPE = MediaType.parse("text/plain");

    private static final Serializer XML_SERIALIZER = new Serializer() {
        @Override
        public boolean handles(MediaType type) {
            return XML_TYPE.is(type.withoutParameters());
        }

        @Override
        public byte[] serialize(Object rq) throws MediaTypeConversionException {
            return XmlUtil.getBytesFromDocument((Document) rq);
        }
    };

    // To handle common error such as 404 and 403
    private static final Deserializer STRING_DESERIALIZER = new Deserializer() {
        @Override
        public boolean handles(MediaType type) {
            return HTML_TYPE.is(type.withoutParameters()) || TEXT_TYPE.is(type.withoutParameters());
        }

        @Override
        public Object deserialize(byte[] data) throws MediaTypeConversionException {
            return new String(data);
        }
    };

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Named("payment_accounting")
    public SoapService provideSoapServicePaymentAccounting(HttpClient client) throws IOException, WSDLException, SoapServiceException {
        return new BasicSoapService("http://",
//                + ProductConfig.getInstance().getServerName() + ":" + AppConfig.getValue("payment_accounting") + "?wsdl",
                "BP_03_a_mediator_ep", client);
    }

    @Provides
    @Singleton
    @Named("payment_registry_consumer")
    public SoapService provideSoapServicePaymentRegistryConsumer(HttpClient client) throws IOException, WSDLException, SoapServiceException {
        return new BasicSoapService(
//                "http://" + ProductConfig.getInstance().getServerName() + ":" + AppConfig.getValue("payment_registry_consumer") +
                        "?wsdl", "Epam.Esb.Usc.SubmissionReceiver", client);
    }

    @Provides
    @Singleton
    @Named("poena_service")
    public HttpService provideHttpServicePoena(HttpClient client) throws IOException, WSDLException, SoapServiceException {
        Map<String, String> headers = Collections.singletonMap(MIME.CONTENT_TYPE, "text/xml; charset=UTF-8");
        List<Serializer> serializers = Arrays.asList(XML_SERIALIZER);
        List<Deserializer> deserializers = Arrays.asList(STRING_DESERIALIZER);

        return new BasicHttpService(client, "http://"
//                + ProductConfig.getInstance().getServerName() + ":" + AppConfig.getValue("poena_message_consumer")
                , headers, serializers, deserializers);
    }

    @Provides
    @Singleton
    public HttpClient provideHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setMaxConnPerRoute(5);
        builder.setMaxConnTotal(20);
        builder.setConnectionManager(new PoolingHttpClientConnectionManager());
        return builder.build();
    }
}
