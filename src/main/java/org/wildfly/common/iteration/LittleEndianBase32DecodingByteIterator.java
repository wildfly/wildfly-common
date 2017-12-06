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

import static org.wildfly.common._private.CommonMessages.msg;

import org.wildfly.common.codec.Base32Alphabet;

/**
 */
final class LittleEndianBase32DecodingByteIterator extends Base32DecodingByteIterator {
    private final Base32Alphabet alphabet;

    LittleEndianBase32DecodingByteIterator(final CodePointIterator iter, final boolean requirePadding, final Base32Alphabet alphabet) {
        super(iter, requirePadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0, final int b1) {
        final int d0 = alphabet.decode(b0);
        final int d1 = alphabet.decode(b1);
        // d0 = r0[4..0]
        // d1 = r1[1..0] + r0[7..5]
        if (d0 == - 1 || d1 == - 1) throw msg.invalidBase32Character();
        return (d0 | d1 << 5) & 0xff;
    }

    int calc1(final int b1, final int b2, final int b3) {
        final int d1 = alphabet.decode(b1);
        final int d2 = alphabet.decode(b2);
        final int d3 = alphabet.decode(b3);
        // d1 = r1[1..0] + r0[7..5]
        // d2 = r1[6..2]
        // d3 = r2[3..0] + r1[7]
        if (d1 == - 1 || d2 == - 1 || d3 == - 1) throw msg.invalidBase32Character();
        return (d1 >> 3 | d2 << 2 | d3 << 7) & 0xff;
    }

    int calc2(final int b3, final int b4) {
        final int d3 = alphabet.decode(b3);
        final int d4 = alphabet.decode(b4);
        // d3 = r2[3..0] + r1[7]
        // d4 = r3[0] + r2[7..4]
        if (d3 == - 1 || d4 == - 1) throw msg.invalidBase32Character();
        return (d3 >> 1 | d4 << 4) & 0xff;
    }

    int calc3(final int b4, final int b5, final int b6) {
        final int d4 = alphabet.decode(b4);
        final int d5 = alphabet.decode(b5);
        final int d6 = alphabet.decode(b6);
        // d4 = r3[0] + r2[7..4]
        // d5 = r3[5..1]
        // d6 = r4[2..0] + r3[7..6]
        if (d4 == - 1 || d5 == - 1 || d6 == - 1) throw msg.invalidBase32Character();
        return (d4 >> 4 | d5 << 1 | d6 << 6) & 0xff;
    }

    int calc4(final int b6, final int b7) {
        final int d6 = alphabet.decode(b6);
        final int d7 = alphabet.decode(b7);
        // d6 = r4[2..0] + r3[7..6]
        // d7 = r4[7..3]
        if (d6 == - 1 || d7 == - 1) throw msg.invalidBase32Character();
        return (d6 >> 2 | d7 << 3) & 0xff;
    }
}
