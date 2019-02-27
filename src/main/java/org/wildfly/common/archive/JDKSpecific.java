package org.wildfly.common.archive;

import static java.lang.Math.min;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 */
final class JDKSpecific {
    private JDKSpecific() {}

    static ByteBuffer inflate(final Inflater inflater, final ByteBuffer[] bufs, final long offset, final int compSize, final int uncompSize) throws DataFormatException, IOException {
        int cnt = 0;
        byte[] b = new byte[min(16384, compSize)];
        byte[] out = new byte[uncompSize];
        int op = 0;
        while (cnt < compSize) {
            int rem = compSize - cnt;
            final int acnt = min(rem, b.length);
            Archive.readBytes(bufs, offset, b, 0, acnt);
            cnt += acnt;
            inflater.setInput(b, 0, acnt);
            do {
                op += inflater.inflate(out, op, uncompSize - op);
            } while (! inflater.needsInput());
        }
        if (! inflater.finished()) {
            throw new IOException("Corrupted compression stream");
        }
        return ByteBuffer.wrap(out);
    }
}
