package org.wildfly.common.iteration;

import java.io.IOException;
import java.io.InputStream;

/**
 */
final class ByteIteratorInputStream extends InputStream {
    private final ByteIterator iter;

    ByteIteratorInputStream(final ByteIterator iter) {
        this.iter = iter;
    }

    public int read() throws IOException {
        return iter.hasNext() ? iter.next() : - 1;
    }

    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) return 0;
        if (! iter.hasNext()) return - 1;
        return iter.drain(b, off, len);
    }
}
