package org.wildfly.common.iteration;

import org.wildfly.common.codec.Base64Alphabet;

/**
 */
final class BigEndianBase64EncodingIterator extends Base64EncodingIterator {
    private final Base64Alphabet alphabet;

    BigEndianBase64EncodingIterator(final ByteIterator iter, final boolean addPadding, final Base64Alphabet alphabet) {
        super(iter, addPadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0) {
        // d0 = r0[7..2]
        return alphabet.encode((b0 >> 2) & 0x3f);
    }

    int calc1(final int b0, final int b1) {
        // d1 = r0[1..0] + r1[7..4]
        return alphabet.encode((b0 << 4 | b1 >> 4) & 0x3f);
    }

    int calc2(final int b1, final int b2) {
        // d2 = r1[3..0] + r2[7..6]
        return alphabet.encode((b1 << 2 | b2 >> 6) & 0x3f);
    }

    int calc3(final int b2) {
        // d3 = r2[5..0]
        return alphabet.encode(b2 & 0x3f);
    }
}
