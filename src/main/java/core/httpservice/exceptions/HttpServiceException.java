package core.httpservice.exceptions;

public class HttpServiceException extends Exception {
    private static final long serialVersionUID = 8131835638981459288L;

    public HttpServiceException() {
        super();
    }

    public HttpServiceException(String message) {
        super(message);
    }

    public HttpServiceException(Throwable cause) {
        super(cause);
    }

    public HttpServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
