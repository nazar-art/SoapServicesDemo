package core.example.poena;

public class PoenaServiceException extends Exception {
    private static final long serialVersionUID = 4782970672982829969L;

    public PoenaServiceException() {
        super();
    }

    public PoenaServiceException(String message) {
        super(message);
    }

    public PoenaServiceException(Throwable cause) {
        super(cause);
    }

    public PoenaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PoenaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
