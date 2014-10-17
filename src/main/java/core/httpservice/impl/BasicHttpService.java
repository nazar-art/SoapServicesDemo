package core.httpservice.impl;

import com.google.common.net.MediaType;
import core.httpservice.HttpService;
import core.httpservice.entities.HttpMethod;
import core.httpservice.entities.ServiceResponseMessage;
import core.httpservice.entities.ServiceResponseRawMessage;
import core.httpservice.exceptions.HttpServiceException;
import core.httpservice.exceptions.MediaTypeConversionException;
import core.httpservice.serializers.Deserializer;
import core.httpservice.serializers.InternetMediaTypeHandler;
import core.httpservice.serializers.Serializer;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.mime.MIME;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BasicHttpService implements HttpService {

    private HttpClient httpClient;

    private String baseUrl;

    private Map<String, String> staticHeaders;

    private List<Serializer> serializers;

    private List<Deserializer> deserializers;

    public BasicHttpService(HttpClient client, String baseUrl, Map<String, String> staticHeaders, List<Serializer> serializers, List<Deserializer> deserializers) {
        this.httpClient = client;
        this.baseUrl = baseUrl;
        this.staticHeaders = staticHeaders;
        this.serializers = serializers;
        this.deserializers = deserializers;
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Map<String, String> parameters, Object rq) throws HttpServiceException {
        HttpUriRequest httpRq = getHttpRequest(method, localHeaders, resource, parameters, rq);
        ServiceResponseRawMessage rawResult = send(httpRq);
        String contentType = getContentType(rawResult.getHeaders());
        Deserializer handler = getSupportedHandler(MediaType.parse(getContentType(rawResult.getHeaders())), deserializers);
        if (handler == null) {
            throw new HttpServiceException("Unable to find appropriate deserializer for response type: " + contentType);
        }

        Object rsData;
        try {
            rsData = handler.deserialize(rawResult.getResult());
        } catch (MediaTypeConversionException e) {
            throw new HttpServiceException("Unable to deserialize response. ", e);
        }
        return new ServiceResponseMessage(rawResult.getStatusCode(), rawResult.getStatusMessage(), rawResult.isSuccess(), rsData);
    }

    private ServiceResponseRawMessage send(HttpUriRequest httpRq) throws HttpServiceException {
        try {
            return httpClient.execute(httpRq, new ServiceResponseHandler());
        } catch (ClientProtocolException e) {
            throw new HttpServiceException("Unable to handle HTTP protocol.", e);
        } catch (IOException e) {
            throw new HttpServiceException("Connection error.", e);
        }
    }

    private <RQ> HttpUriRequest getHttpRequest(HttpMethod method, Map<String, String> localHeaders, String resource, Map<String, String> parameters, Object rq) throws HttpServiceException {
        URI uri = buildUri(baseUrl, resource, parameters);

        HttpUriRequest httpRq;
        switch (method) {
            case GET:
                httpRq = new HttpGet(uri);
                break;
            case DELETE:
                httpRq = new HttpDelete(uri);
                break;
            case POST:
                httpRq = new HttpPost(uri);
                ((HttpPost) httpRq).setEntity(new ByteArrayEntity(serialize(getSupportedHandler(MediaType.parse(getContentType(localHeaders)), serializers), rq)));
                break;
            case PUT:
                httpRq = new HttpPut(uri);
                ((HttpPut) httpRq).setEntity(new ByteArrayEntity(serialize(getSupportedHandler(MediaType.parse(getContentType(localHeaders)), serializers), rq)));
                break;
            default:
                throw new HttpServiceException("Unknown request method: " + method.name());
        }
        setHeaders(httpRq, staticHeaders);
        setHeaders(httpRq, localHeaders);

        return httpRq;
    }

    private byte[] serialize(Serializer handler, Object rq) throws HttpServiceException {
        try {
            return handler.serialize(rq);
        } catch (MediaTypeConversionException e) {
            throw new HttpServiceException("Unable to serialize request.", e);
        }
    }

    private String getContentType(Header[] headers) throws HttpServiceException {
        if (headers != null) {
            for (Header header : headers) {
                if (MIME.CONTENT_TYPE.equals(header.getName())) {
                    return header.getValue();
                }
            }
        }
        throw new HttpServiceException("'" + MIME.CONTENT_TYPE + "' header wasn't specified. This header is required for HTTP Service.");
    }

    private String getContentType(Map<String, String> localHeaders) throws HttpServiceException {
        String value = getHeaderValue(MIME.CONTENT_TYPE, localHeaders);
        if (value == null) {
            throw new HttpServiceException("'" + MIME.CONTENT_TYPE + "' header wasn't specified. This header is required for HTTP Service, please specify it either in constructor either in local headers.");
        }
        return value;
    }

    private String getHeaderValue(String headerName, Map<String, String> localHeaders) {
        String value = localHeaders.get(headerName);
        return value == null ? staticHeaders.get(headerName) : value;
    }

    private void setHeaders(HttpMessage message, Map<String, String> headers) {
        for (Entry<String, String> header : headers.entrySet()) {
            message.setHeader(header.getKey(), header.getValue());
        }
    }

    private URI buildUri(String base, String resource, Map<String, String> parameters) throws HttpServiceException {
        try {
            URIBuilder builder = new URIBuilder(base);

            if (resource != null) {
                builder = builder.setPath(resource);
            }
            if (parameters != null) {
                for (Entry<String, String> param : parameters.entrySet()) {
                    builder = builder.setParameter(param.getKey(), param.getValue());
                }
            }
            return builder.build();
        } catch (URISyntaxException e) {
            throw new HttpServiceException("Unable to build resource URI.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends InternetMediaTypeHandler> T getSupportedHandler(MediaType type, List<T> handlerList) {
        for (InternetMediaTypeHandler handler : handlerList) {
            if (handler.handles(type)) {
                return (T) handler;
            }
        }

        return null;
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, String resource, Map<String, String> parameters, Object rq) throws HttpServiceException {
        return execute(method, Collections.<String, String>emptyMap(), resource, parameters, rq);
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Object rq) throws HttpServiceException {
        return execute(method, localHeaders, resource, Collections.<String, String>emptyMap(), rq);
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, String resource, Object rq) throws HttpServiceException {
        return execute(method, Collections.<String, String>emptyMap(), resource, Collections.<String, String>emptyMap(), rq);
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Map<String, String> parameters) throws HttpServiceException {
        return execute(method, localHeaders, resource, parameters, null);
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, String resource, Map<String, String> parameters) throws HttpServiceException {
        return execute(method, Collections.<String, String>emptyMap(), resource, parameters);
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource) throws HttpServiceException {
        return execute(method, localHeaders, resource, Collections.<String, String>emptyMap());
    }

    @Override
    public ServiceResponseMessage execute(HttpMethod method, String resource) throws HttpServiceException {
        return execute(method, Collections.<String, String>emptyMap(), resource, Collections.<String, String>emptyMap());
    }

}
