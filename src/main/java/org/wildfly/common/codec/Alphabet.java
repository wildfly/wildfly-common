package org.wildfly.common.codec;

/**
 * A base-n encoder/decoder alphabet.  Alphabets may be little-endian or big-endian.  Each base has its own subclass.
 */
public abstract class Alphabet {
    private final boolean littleEndian;

    Alphabet(final boolean littleEndian) {
        this.littleEndian = littleEndian;
    }

    /**
     * Determine whether this is a little-endian or big-endian alphabet.
     *
     * @return {@code true} if the alphabet is little-endian, {@code false} if it is big-endian
     */
    public boolean isLittleEndian() {
        return littleEndian;
    }

    /**
     * Encode the given byte value to a code point.
     *
     * @param val the value
     * @return the Unicode code point
     */
    public abstract int encode(int val);

    /**
     * Decode the given code point (character).  If the code point is not valid, -1 is returned.
     *
     * @param codePoint the Unicode code point
     * @return the decoded value or -1 if the code point is not valid
     */
    public abstract int decode(int codePoint);
}
