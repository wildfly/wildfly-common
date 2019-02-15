package org.wildfly.common.archive;

import java.io.InputStream;
import java.nio.ByteBuffer;

final class ByteBufferInputStream extends InputStream {
    private final ByteBuffer[] bufs;
    private final long offset;
    private final long size;
    long pos;
    long mark;

    ByteBufferInputStream(final ByteBuffer[] bufs, final long offset, final long size) {
        this.bufs = bufs;
        this.offset = offset;
        this.size = size;
    }

    public int read() {
        return pos < size ? Archive.getByte(bufs, offset + pos++) : -1;
    }

    public int read(final byte[] b) {
        return read(b, 0, b.length);
    }

    public int read(final byte[] b, final int off, final int len) {
        final long rem = size - pos;
        if (rem == 0) return -1;
        final int realLen = (int) Math.min(len, rem);
        if (realLen > 0) {
            Archive.readBytes(bufs, offset + pos, b, off, realLen);
            return realLen;
        } else {
            return 0;
        }
    }

    public long skip(final long n) {
        final long rem = size - pos;
        final long cnt = Math.min(rem, n);
        if (cnt > 0) {
            pos += cnt;
            return cnt;
        } else {
            return 0;
        }
    }

    public int available() {
        return (int) Math.min(Integer.MAX_VALUE, size - pos);
    }

    public void close() {
    }

    public void mark(final int readLimit) {
        mark = pos;
    }

    public void reset() {
        pos = mark;
    }

    public boolean markSupported() {
        return true;
    }
}
