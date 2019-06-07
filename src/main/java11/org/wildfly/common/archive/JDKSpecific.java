/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    static ByteBuffer inflate(final Inflater inflater, final ByteBuffer[] bufs, long offset, final int compSize, final int uncompSize) throws DataFormatException, IOException {
        int cnt = 0;
        byte[] out = new byte[uncompSize];
        int op = 0;
        while (cnt < compSize) {
            int rem = compSize - cnt;
            final ByteBuffer buf = bufs[Archive.bufIdx(offset + cnt)].duplicate();
            buf.position(Archive.bufOffs(offset + cnt));
            buf.limit(min(buf.capacity(), buf.position() + rem));
            cnt += buf.remaining();
            inflater.setInput(buf);
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
