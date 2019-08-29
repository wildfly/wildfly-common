package org.wildfly.common.iteration;

import org.wildfly.common.codec.Base32Alphabet;

/**
 */
final class BigEndianBase32EncodingIterator extends Base32EncodingCodePointIterator {
    private final Base32Alphabet alphabet;

    BigEndianBase32EncodingIterator(final ByteIterator iter, final boolean addPadding, final Base32Alphabet alphabet) {
        super(iter, addPadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0) {
        // d0 = r0[7..3]
        return alphabet.encode((b0 >> 3) & 0x1f);
    }

    int calc1(final int b0, final int b1) {
        // d1 = r0[2..0] + r1[7..6]
        return alphabet.encode((b0 << 2 | b1 >> 6) & 0x1f);
    }

    int calc2(final int b1) {
        // d2 = r1[5..1]
        return alphabet.encode((b1 >> 1) & 0x1f);
    }

    int calc3(final int b1, final int b2) {
        // d3 = r1[0] + r2[7..4]
        return alphabet.encode((b1 << 4 | b2 >> 4) & 0x1f);
    }

    int calc4(final int b2, final int b3) {
        // d4 = r2[3..0] + r3[7]
        return alphabet.encode((b2 << 1 | b3 >> 7) & 0x1f);
    }

    int calc5(final int b3) {
        // d5 = r3[6..2]
        return alphabet.encode((b3 >> 2) & 0x1f);
    }

    int calc6(final int b3, final int b4) {
        // d6 = r3[1..0] + r4[7..5]
        return alphabet.encode((b3 << 3 | b4 >> 5) & 0x1f);
    }

    int calc7(final int b4) {
        // d7 = r4[4..0]
        return alphabet.encode(b4 & 0x1f);
    }
}
