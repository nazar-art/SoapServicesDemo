package core.httpservice;

import core.httpservice.entities.HttpMethod;
import core.httpservice.entities.ServiceResponseMessage;
import core.httpservice.exceptions.HttpServiceException;

import java.util.Map;

public interface HttpService {

    ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Map<String, String> parameters, Object rq) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, String resource, Map<String, String> parameters, Object rq) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Object rq) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, String resource, Object rq) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource, Map<String, String> parameters) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, String resource, Map<String, String> parameters) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, Map<String, String> localHeaders, String resource) throws HttpServiceException;

    ServiceResponseMessage execute(HttpMethod method, String resource) throws HttpServiceException;
}
