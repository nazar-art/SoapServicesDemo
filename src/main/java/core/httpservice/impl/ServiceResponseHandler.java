package core.httpservice.impl;


import core.httpservice.entities.ServiceResponseRawMessage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ServiceResponseHandler implements ResponseHandler<ServiceResponseRawMessage> {
    @Override
    public ServiceResponseRawMessage handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            return new ServiceResponseRawMessage(statusLine.getStatusCode(), statusLine.getReasonPhrase(), false, response.getAllHeaders(), entity == null ? null : EntityUtils.toByteArray(entity));
        }
        return new ServiceResponseRawMessage(statusLine.getStatusCode(), statusLine.getReasonPhrase(), true, response.getAllHeaders(), entity == null ? null : EntityUtils.toByteArray(entity));
    }
}
