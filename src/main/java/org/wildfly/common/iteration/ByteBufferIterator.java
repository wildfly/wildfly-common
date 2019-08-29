package org.wildfly.common.iteration;

import java.nio.ByteBuffer;
import java.util.NoSuchElementException;

/**
 */
final class ByteBufferIterator extends ByteIterator {
    private final ByteBuffer buffer;
    private final int initialPosition;

    ByteBufferIterator(final ByteBuffer buffer) {
        this.buffer = buffer;
        initialPosition = buffer.position();
    }

    public boolean hasNext() {
        return buffer.hasRemaining();
    }

    public boolean hasPrevious() {
        return buffer.position() > initialPosition;
    }

    public int next() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return buffer.get() & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return buffer.get(buffer.position()) & 0xff;
    }

    public int previous() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        buffer.position(buffer.position() - 1);
        return peekNext();
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return buffer.get(buffer.position() - 1) & 0xff;
    }

    public long getIndex() {
        return buffer.position() - initialPosition;
    }
}
