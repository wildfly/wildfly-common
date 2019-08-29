package org.wildfly.common.iteration;

import static org.wildfly.common._private.CommonMessages.msg;

import org.wildfly.common.codec.Base32Alphabet;

/**
 */
final class BigEndianBase32DecodingByteIterator extends Base32DecodingByteIterator {
    private final Base32Alphabet alphabet;

    BigEndianBase32DecodingByteIterator(final CodePointIterator iter, final boolean requirePadding, final Base32Alphabet alphabet) {
        super(iter, requirePadding);
        this.alphabet = alphabet;
    }

    int calc0(final int b0, final int b1) {
        final int d0 = alphabet.decode(b0);
        final int d1 = alphabet.decode(b1);
        // d0 = r0[7..3]
        // d1 = r0[2..0] + r1[7..6]
        if (d0 == - 1 || d1 == - 1) throw msg.invalidBase32Character();
        return (d0 << 3 | d1 >> 2) & 0xff;
    }

    int calc1(final int b1, final int b2, final int b3) {
        final int d1 = alphabet.decode(b1);
        final int d2 = alphabet.decode(b2);
        final int d3 = alphabet.decode(b3);
        // d1 = r0[2..0] + r1[7..6]
        // d2 = r1[5..1]
        // d3 = r1[0] + r2[7..4]
        if (d1 == - 1 || d2 == - 1 || d3 == - 1) throw msg.invalidBase32Character();
        return (d1 << 6 | d2 << 1 | d3 >> 4) & 0xff;
    }

    int calc2(final int b3, final int b4) {
        final int d3 = alphabet.decode(b3);
        final int d4 = alphabet.decode(b4);
        // d3 = r1[0] + r2[7..4]
        // d4 = r2[3..0] + r3[7]
        if (d3 == - 1 || d4 == - 1) throw msg.invalidBase32Character();
        return (d3 << 4 | d4 >> 1) & 0xff;
    }

    int calc3(final int b4, final int b5, final int b6) {
        final int d4 = alphabet.decode(b4);
        final int d5 = alphabet.decode(b5);
        final int d6 = alphabet.decode(b6);
        // d4 = r2[3..0] + r3[7]
        // d5 = r3[6..2]
        // d6 = r3[1..0] + r4[7..5]
        if (d4 == - 1 || d5 == - 1 || d6 == - 1) throw msg.invalidBase32Character();
        return (d4 << 7 | d5 << 2 | d6 >> 3) & 0xff;
    }

    int calc4(final int b6, final int b7) {
        final int d6 = alphabet.decode(b6);
        final int d7 = alphabet.decode(b7);
        // d6 = r3[1..0] + r4[7..5]
        // d7 = r4[4..0]
        if (d6 == - 1 || d7 == - 1) throw msg.invalidBase32Character();
        return (d6 << 5 | d7) & 0xff;
    }
}
