package org.wildfly.common.codec;

import org.wildfly.common.iteration.ByteIterator;
import org.wildfly.common.iteration.CodePointIterator;

/**
 * A base-32 alphabet.
 *
 * @see ByteIterator#base32Encode(Base32Alphabet)
 * @see CodePointIterator#base32Decode(Base32Alphabet)
 */
public abstract class Base32Alphabet extends Alphabet {

    /**
     * Construct a new instance.
     *
     * @param littleEndian {@code true} if the alphabet is little-endian (LSB first), {@code false} otherwise
     */
    protected Base32Alphabet(final boolean littleEndian) {
        super(littleEndian);
    }

    /**
     * Encode the given 5-bit value to a code point.
     *
     * @param val the 5-bit value
     * @return the Unicode code point
     */
    public abstract int encode(int val);

    /**
     * Decode the given code point.  If the code point is not valid, -1 is returned.
     *
     * @param codePoint the code point
     * @return the decoded 5-bit value or -1 if the code point is not valid
     */
    public abstract int decode(int codePoint);

    /**
     * The standard <a href="http://tools.ietf.org/html/rfc4648">RFC 4648</a> base-32 alphabet.
     */
    public static final Base32Alphabet STANDARD = new Base32Alphabet(false) {
        public int encode(final int val) {
            if (val <= 25) {
                return 'A' + val;
            } else {
                assert val < 32;
                return '2' + val - 26;
            }
        }

        public int decode(final int codePoint) {
            if ('A' <= codePoint && codePoint <= 'Z') {
                return codePoint - 'A';
            } else if ('2' <= codePoint && codePoint <= '7') {
                return codePoint - '2' + 26;
            } else {
                return -1;
            }
        }
    };

    /**
     * The standard <a href="http://tools.ietf.org/html/rfc4648">RFC 4648</a> base-32 alphabet mapped to lowercase.
     */
    public static final Base32Alphabet LOWERCASE = new Base32Alphabet(false) {
        public int encode(final int val) {
            if (val <= 25) {
                return 'a' + val;
            } else {
                assert val < 32;
                return '2' + val - 26;
            }
        }

        public int decode(final int codePoint) {
            if ('a' <= codePoint && codePoint <= 'z') {
                return codePoint - 'a';
            } else if ('2' <= codePoint && codePoint <= '7') {
                return codePoint - '2' + 26;
            } else {
                return -1;
            }
        }
    };

}
