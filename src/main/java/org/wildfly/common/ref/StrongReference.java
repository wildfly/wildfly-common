package org.wildfly.common.ref;

/**
 * A strong reference with an attachment.  Since strong references are always reachable, a reaper may not be used.
 *
 * @param <T> the reference value type
 * @param <A> the attachment type
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class StrongReference<T, A> implements Reference<T, A> {

    private volatile T referent;
    private final A attachment;

    /**
     * Construct a new instance.
     *
     * @param referent the referent
     * @param attachment the attachment
     */
    public StrongReference(final T referent, final A attachment) {
        this.referent = referent;
        this.attachment = attachment;
    }

    /**
     * Construct a new instance.
     *
     * @param referent the referent
     */
    public StrongReference(final T referent) {
        this(referent, null);
    }

    public T get() {
        return referent;
    }

    public void clear() {
        referent = null;
    }

    public A getAttachment() {
        return attachment;
    }

    public Type getType() {
        return Type.STRONG;
    }

    public String toString() {
        return "strong reference to " + String.valueOf(get());
    }
}
