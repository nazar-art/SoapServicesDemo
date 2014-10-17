package core.soapservice.entities;

import org.w3c.dom.Document;

public class SoapMessageRs {
    private int statusCode;
    private String statusMessage;
    private boolean isSuccess;
    private Document payload;

    public SoapMessageRs(int statusCode, String statusMessage, boolean isSuccess, Document payload) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.isSuccess = isSuccess;
        this.payload = payload;
    }

    public Document getPayload() {
        return payload;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
