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

import org.wildfly.common.codec.Base32Alphabet;

/**
 */
final class LittleEndianBase32EncodingIterator extends Base32EncodingCodePointIterator {
    private final Base32Alphabet alphabet;

    LittleEndianBase32EncodingIterator(final ByteIterator iter, final boolean addPadding, final Base32Alphabet alphabet) {
        super(iter, addPadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0) {
        // d0 = r0[4..0]
        return alphabet.encode(b0 & 0x1f);
    }

    int calc1(final int b0, final int b1) {
        // d1 = r1[1..0] + r0[7..5]
        return alphabet.encode((b1 << 3 | b0 >> 5) & 0x1f);
    }

    int calc2(final int b1) {
        // d2 = r1[6..2]
        return alphabet.encode((b1 >> 2) & 0x1f);
    }

    int calc3(final int b1, final int b2) {
        // d3 = r2[3..0] + r1[7]
        return alphabet.encode((b2 << 1 | b1 >> 7) & 0x1f);
    }

    int calc4(final int b2, final int b3) {
        // d4 = r3[0] + r2[7..4]
        return alphabet.encode((b3 << 4 | b2 >> 4) & 0x1f);
    }

    int calc5(final int b3) {
        // d5 = r3[5..1]
        return alphabet.encode((b3 >> 1) & 0x1f);
    }

    int calc6(final int b3, final int b4) {
        // d6 = r4[2..0] + r3[7..6]
        return alphabet.encode((b4 << 2 | b3 >> 6) & 0x1f);
    }

    int calc7(final int b4) {
        // d7 = r4[7..3]
        return alphabet.encode((b4 >> 3) & 0x1f);
    }
}
