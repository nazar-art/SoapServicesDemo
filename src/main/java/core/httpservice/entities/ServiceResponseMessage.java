package core.httpservice.entities;

public class ServiceResponseMessage {

    private int statusCode;

    private String statusMessage;

    private boolean isSuccess;

    private Object result;

    public ServiceResponseMessage(int statusCode, String statusMessage, boolean isSuccess, Object result) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.isSuccess = isSuccess;
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

}
