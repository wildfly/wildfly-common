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

import org.wildfly.common.codec.Base64Alphabet;

/**
 */
final class LittleEndianBase64DecodingByteIterator extends Base64DecodingByteIterator {
    private final Base64Alphabet alphabet;

    LittleEndianBase64DecodingByteIterator(final CodePointIterator iter, final boolean requirePadding, final Base64Alphabet alphabet) {
        super(iter, requirePadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0, final int b1) {
        final int d0 = alphabet.decode(b0);
        final int d1 = alphabet.decode(b1);
        // d0 = r0[5..0]
        // d1 = r1[3..0] + r0[7..6]
        if (d0 == - 1 || d1 == - 1) throw msg.invalidBase64Character();
        return (d0 | d1 << 6) & 0xff;
    }

    int calc1(final int b1, final int b2) {
        final int d1 = alphabet.decode(b1);
        final int d2 = alphabet.decode(b2);
        // d1 = r1[3..0] + r0[7..6]
        // d2 = r2[1..0] + r1[7..4]
        if (d1 == - 1 || d2 == - 1) throw msg.invalidBase64Character();
        return (d1 >> 2 | d2 << 4) & 0xff;
    }

    int calc2(final int b2, final int b3) {
        final int d2 = alphabet.decode(b2);
        final int d3 = alphabet.decode(b3);
        // d2 = r2[1..0] + r1[7..4]
        // d3 = r2[7..2]
        if (d2 == - 1 || d3 == - 1) throw msg.invalidBase64Character();
        return (d2 >> 4 | d3 << 2) & 0xff;
    }
}
