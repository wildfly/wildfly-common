/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
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
