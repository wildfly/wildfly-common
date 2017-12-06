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

import org.wildfly.common.codec.Base64Alphabet;

/**
 */
final class LittleEndianBase64EncodingIterator extends Base64EncodingIterator {
    private final Base64Alphabet alphabet;

    LittleEndianBase64EncodingIterator(final ByteIterator iter, final boolean addPadding, final Base64Alphabet alphabet) {
        super(iter, addPadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0) {
        // d0 = r0[5..0]
        return alphabet.encode(b0 & 0x3f);
    }

    int calc1(final int b0, final int b1) {
        // d1 = r1[3..0] + r0[7..6]
        return alphabet.encode((b1 << 2 | b0 >> 6) & 0x3f);
    }

    int calc2(final int b1, final int b2) {
        // d2 = r2[1..0] + r1[7..4]
        return alphabet.encode((b2 << 4 | b1 >> 4) & 0x3f);
    }

    int calc3(final int b2) {
        // d3 = r2[7..2]
        return alphabet.encode((b2 >> 2) & 0x3f);
    }
}
