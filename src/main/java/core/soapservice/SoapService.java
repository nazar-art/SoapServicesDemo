package core.soapservice;


import core.httpservice.exceptions.HttpServiceException;
import core.soapservice.entities.SoapMessageRq;
import core.soapservice.entities.SoapMessageRs;
import core.soapservice.exceptions.SoapServiceException;

import java.util.Collection;

public interface SoapService {

    /**
     * @return supported methods
     */
    Collection<String> getMethodNames() throws SoapServiceException;

    /**
     * Execute a web service method
     *
     * @param method - method name
     * @param data   - web service method I/O wrapper
     * @return a wrapper object as a result of operation
     * @throws HttpServiceException in case of any problem
     */
    SoapMessageRs executeMethod(String method, SoapMessageRq data) throws HttpServiceException, SoapServiceException;
}
