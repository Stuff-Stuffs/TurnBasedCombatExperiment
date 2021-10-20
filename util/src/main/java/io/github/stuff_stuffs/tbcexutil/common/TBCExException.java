package io.github.stuff_stuffs.tbcexutil.common;

public class TBCExException extends RuntimeException {
    public TBCExException() {
    }

    public TBCExException(String message) {
        super(message);
    }

    public TBCExException(String message, Throwable cause) {
        super(message, cause);
    }
}
