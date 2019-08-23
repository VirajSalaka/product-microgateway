package org.wso2.apimgt.gateway.cli.exception;

/**
 * Thrown to indicate that the requested field type is not supported.
 *
 */
public class GrpcCodeGenException extends RuntimeException {

    /**
     * Constructs an UnsupportedFieldTypeException with the specified detail message.
     *
     * @param message the detail message
     */
    public GrpcCodeGenException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param  message the detail message
     * @param  cause the cause
     */
    public GrpcCodeGenException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause
     */
    public GrpcCodeGenException(Throwable cause) {
        super(cause);
    }
}
