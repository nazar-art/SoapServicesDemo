package core.httpservice.entities;


import org.apache.http.Header;

public class ServiceResponseRawMessage {

    private int statusCode;

    private String statusMessage;

    private boolean isSuccess;

    private Header[] headers;

    private byte[] result;

    public ServiceResponseRawMessage(int statusCode, String statusMessage, boolean isSuccess, Header[] headers, byte[] result) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.isSuccess = isSuccess;
        this.headers = headers;
        this.result = result;
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

    public Header[] getHeaders() {
        return headers;
    }

    public byte[] getResult() {
        return result;
    }
}
