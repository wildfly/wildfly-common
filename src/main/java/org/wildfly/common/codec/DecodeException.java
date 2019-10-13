package org.wildfly.common.codec;

/**
 * An exception which indicates that decoding has failed due to invalid or truncated input.
 */
public class DecodeException extends IllegalArgumentException {

    private static final long serialVersionUID = 5823281980783313991L;

    /**
     * Constructs a new {@code DecodeException} instance.  The message is left blank ({@code null}), and no cause is
     * specified.
     */
    public DecodeException() {
    }

    /**
     * Constructs a new {@code DecodeException} instance with an initial message.  No cause is specified.
     *
     * @param msg the message
     */
    public DecodeException(final String msg) {
        super(msg);
    }

    /**
     * Constructs a new {@code DecodeException} instance with an initial cause.  If a non-{@code null} cause is
     * specified, its message is used to initialize the message of this {@code DecodeException}; otherwise the message
     * is left blank ({@code null}).
     *
     * @param cause the cause
     */
    public DecodeException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code DecodeException} instance with an initial message and cause.
     *
     * @param msg the message
     * @param cause the cause
     */
    public DecodeException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
