package core.exception;

public class CommonTestRuntimeException extends RuntimeException {
    private static final long serialVersionUID = -7698899776770985383L;

    public CommonTestRuntimeException() {
        super();
    }

    public CommonTestRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonTestRuntimeException(String message) {
        super(message);
    }

    public CommonTestRuntimeException(Throwable cause) {
        super(cause);
    }
}
