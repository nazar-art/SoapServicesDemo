package core.httpservice.exceptions;

public class MediaTypeConversionException extends Exception {
    private static final long serialVersionUID = -777866190803795055L;

    public MediaTypeConversionException() {
        super();
    }

    public MediaTypeConversionException(String message) {
        super(message);
    }

    public MediaTypeConversionException(Throwable cause) {
        super(cause);
    }

    public MediaTypeConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MediaTypeConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
