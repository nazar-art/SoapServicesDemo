package core.soapservice.exceptions;

public class SoapServiceException extends Exception {
    private static final long serialVersionUID = -1611152372971962517L;

    public SoapServiceException() {
        super();
    }

    public SoapServiceException(String message) {
        super(message);
    }

    public SoapServiceException(Throwable cause) {
        super(cause);
    }

    public SoapServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SoapServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
