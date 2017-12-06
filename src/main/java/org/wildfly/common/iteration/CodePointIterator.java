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

import java.util.NoSuchElementException;
import java.util.function.IntPredicate;

import org.wildfly.common.codec.Base32Alphabet;
import org.wildfly.common.codec.Base64Alphabet;

/**
 * A code point by code point iterator.
 */
public abstract class CodePointIterator implements BiDirIntIterator, IndexIterator {

    CodePointIterator() {
    }

    /**
     * Determine if there are more code points after the current code point.
     *
     * @return {@code true} if there are more code points, {@code false} otherwise
     */
    public abstract boolean hasNext();

    /**
     * Determine if there are more code points before the current code point.
     *
     * @return {@code true} if there are more code points, {@code false} otherwise
     */
    public abstract boolean hasPrevious();

    /**
     * Get the next code point.
     *
     * @return the next code point
     * @throws NoSuchElementException if {@link #hasNext()} returns {@code false}
     */
    public abstract int next() throws NoSuchElementException;

    /**
     * Peek at the next code point without advancing.
     *
     * @return the next code point
     * @throws NoSuchElementException if {@link #hasNext()} returns {@code false}
     */
    public abstract int peekNext() throws NoSuchElementException;

    /**
     * Get the previous code point.
     *
     * @return the previous code point
     * @throws NoSuchElementException if {@link #hasPrevious()} returns {@code false}
     */
    public abstract int previous() throws NoSuchElementException;

    /**
     * Peek at the previous code point without moving backwards.
     *
     * @return the previous code point
     * @throws NoSuchElementException if {@link #hasPrevious()} returns {@code false}
     */
    public abstract int peekPrevious() throws NoSuchElementException;

    /**
     * Get the current offset, by code point.
     *
     * @return the code point offset
     */
    public abstract long getIndex();

    /**
     * Determine if the remaining contents of this iterator are identical to the remaining contents of the other iterator.  If the
     * contents are not equal, the iterators will be positioned at the location of the first difference (i.e. the code point
     * returned by {@link #next()} will be the differing code point.  If the contents are equal, the iterators will both be
     * positioned at the end of their contents.
     *
     * @param other the other byte iterator
     * @return {@code true} if the contents are equal, {@code false} otherwise
     */
    public final boolean contentEquals(CodePointIterator other) {
        for (;;) {
            if (hasNext()) {
                if (! other.hasNext()) {
                    return false;
                }
                if (peekNext() != other.peekNext()) {
                    return false;
                }
                next();
                other.next();
            } else {
                return ! other.hasNext();
            }
        }
    }

    /**
     * Determine if the remaining contents of this iterator are identical to the given string.  If the
     * contents are not equal, the iterator will be positioned at the location of the first difference (i.e. the code point
     * returned by {@link #next()} will be the differing code point.  If the contents are equal, the iterator will be
     * positioned at the end of its contents.
     *
     * @param other the other string
     * @return {@code true} if the contents are equal, {@code false} otherwise
     */
    public boolean contentEquals(String other) {
        return contentEquals(CodePointIterator.ofString(other));
    }

    /**
     * Return a copy of this iterator which is limited to the given number of code points after the current one.  Advancing
     * the returned iterator will also advance this one.
     *
     * @param size the number of code points
     * @return the limited code point iterator
     */
    public final CodePointIterator limitedTo(final long size) {
        if (size <= 0 || ! hasNext()) {
            return EMPTY;
        }
        return new LimitedCodePointIterator(this, size);
    }

    /**
     * Get a sub-iterator that is delimited by the given code points.  The returned iterator offset starts at 0 and cannot
     * be backed up before that point.  The returned iterator will return {@code false} for {@code hasNext()} if the next
     * character in the encapsulated iterator is a delimiter or if the underlying iterator returns {@code false} for
     * {@code hasNext()}.
     *
     * @param delims the code point delimiters
     * @return the sub-iterator
     */
    public final CodePointIterator delimitedBy(final int... delims) {
        if ((delims == null) || (delims.length == 0) || ! hasNext()) {
            return EMPTY;
        }
        return new DelimitedCodePointIterator(this, delims);
    }

    /**
     * Drain all the remaining code points in this iterator to the given string builder.
     *
     * @param b the string builder
     * @return the same string builder
     */
    public StringBuilder drainTo(StringBuilder b) {
        while (hasNext()) {
            b.appendCodePoint(next());
        }
        return b;
    }

    /**
     * Skip all the remaining code points in this iterator.
     * (Useful in combination with {@link #delimitedBy(int...)})
     *
     * @return the same code point iterator
     */
    public CodePointIterator skipAll() {
        while (hasNext()) next();
        return this;
    }

    /**
     * Drain all the remaining code points in this iterator to the given string builder,
     * inserting the given prefix and delimiter before and after every {@code n} code points,
     * respectively.
     *
     * @param b the string builder
     * @param prefix the prefix
     * @param delim the delimiter
     * @param n the number of code points between each prefix and delimiter
     * @return the same string builder
     */
    public StringBuilder drainTo(StringBuilder b, final String prefix, final int delim, final int n) {
        int i = 0;
        boolean insertPrefix = (prefix != null);
        boolean insertDelim = Character.isValidCodePoint(delim);
        if (hasNext()) {
            if (insertPrefix) {
                b.append(prefix);
            }
            b.appendCodePoint(next());
            i ++;
            while (hasNext()) {
                if (i == n) {
                    if (insertDelim) {
                        b.appendCodePoint(delim);
                    }
                    if (insertPrefix) {
                        b.append(prefix);
                    }
                    b.appendCodePoint(next());
                    i = 1;
                } else {
                    b.appendCodePoint(next());
                    i ++;
                }
            }
        }
        return b;
    }

    /**
     * Drain all the remaining code points in this iterator to the given string builder,
     * inserting the given delimiter after every {@code n} code points.
     *
     * @param b the string builder
     * @param delim the delimiter
     * @param n the number of code points between each delimiter
     * @return the same string builder
     */
    public StringBuilder drainTo(StringBuilder b, final int delim, final int n) {
        return drainTo(b, null, delim, n);
    }

    /**
     * Drain all the remaining code points in this iterator to the given string builder,
     * inserting the given prefix before every {@code n} code points.
     *
     * @param b the string builder
     * @param prefix the prefix
     * @param n the number of code points between each prefix
     * @return the same string builder
     */
    public StringBuilder drainTo(StringBuilder b, final String prefix, final int n) {
        return drainTo(b, prefix, -1, n);
    }

    /**
     * Drain all the remaining code points in this iterator to a new string.
     *
     * @return the string
     */
    public String drainToString() {
        return hasNext() ? drainTo(new StringBuilder()).toString() : "";
    }

    /**
     * Drain all the remaining code points in this iterator to a new string,
     * inserting the given prefix and delimiter before and after every {@code n}
     * code points, respectively.
     *
     * @param prefix the prefix
     * @param delim the delimiter
     * @param n the number of code points between each prefix and delimiter
     * @return the string
     */
    public String drainToString(final String prefix, final int delim, final int n) {
        return hasNext() ? drainTo(new StringBuilder(), prefix, delim, n).toString() : "";
    }

    /**
     * Drain all the remaining code points in this iterator to a new string,
     * inserting the given delimiter after every {@code n} code points.
     *
     * @param delim the delimiter
     * @param n the number of code points between each delimiter
     * @return the string
     */
    public String drainToString(final int delim, final int n) {
        return hasNext() ? drainTo(new StringBuilder(), null, delim, n).toString() : "";
    }

    /**
     * Drain all the remaining code points in this iterator to a new string,
     * inserting the given prefix before every {@code n} code points.
     *
     * @param prefix the prefix
     * @param n the number of code points between each prefix
     * @return the string
     */
    public String drainToString(final String prefix, final int n) {
        return hasNext() ? drainTo(new StringBuilder(), prefix, -1, n).toString() : "";
    }

    /**
     * Base64-decode the current stream.
     *
     * @param alphabet the alphabet to use
     * @param requirePadding {@code true} to require padding, {@code false} if padding is optional
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base64Decode(final Base64Alphabet alphabet, boolean requirePadding) {
        if (! hasNext()) return ByteIterator.EMPTY;
        if (alphabet.isLittleEndian()) {
            return new LittleEndianBase64DecodingByteIterator(this, requirePadding, alphabet);
        } else {
            return new BigEndianBase64DecodingByteIterator(this, requirePadding, alphabet);
        }
    }

    /**
     * Base32-decode the current stream.
     *
     * @param alphabet the alphabet to use
     * @param requirePadding {@code true} to require padding, {@code false} if padding is optional
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base32Decode(final Base32Alphabet alphabet, boolean requirePadding) {
        if (! hasNext()) return ByteIterator.EMPTY;
        if (alphabet.isLittleEndian()) {
            return new LittleEndianBase32DecodingByteIterator(this, requirePadding, alphabet);
        } else {
            return new BigEndianBase32DecodingByteIterator(this, requirePadding, alphabet);
        }
    }

    /**
     * Hex-decode the current stream.
     *
     * @return an iterator over the decoded bytes
     */
    public ByteIterator hexDecode() {
        if (! hasNext()) return ByteIterator.EMPTY;
        return new Base16DecodingByteIterator(this);
    }

    /**
     * Base64-decode the current stream.
     *
     * @param alphabet the alphabet to use
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base64Decode(final Base64Alphabet alphabet) {
        return base64Decode(alphabet, true);
    }

    /**
     * Base64-decode the current stream.
     *
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base64Decode() {
        return base64Decode(Base64Alphabet.STANDARD, true);
    }

    /**
     * Base32-decode the current stream.
     *
     * @param alphabet the alphabet to use
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base32Decode(final Base32Alphabet alphabet) {
        return base32Decode(alphabet, true);
    }

    /**
     * Base32-decode the current stream.
     *
     * @return an iterator over the decoded bytes
     */
    public ByteIterator base32Decode() {
        return base32Decode(Base32Alphabet.STANDARD, true);
    }

    /**
     * Get a byte iterator over the latin-1 encoding of this code point iterator.
     *
     * @return the byte iterator
     */
    public ByteIterator asLatin1() {
        return new Latin1EncodingByteIterator(this);
    }

    /**
     * Get a byte iterator over the UTF-8 encoding of this code point iterator.
     *
     * @return the byte iterator
     */
    public ByteIterator asUtf8() {
        return asUtf8(false);
    }

    /**
     * Get a byte iterator over the UTF-8 encoding of this code point iterator.
     *
     * @param escapeNul {@code true} to escape NUL (0) characters as two bytes, {@code false} to encode them as one byte
     * @return the byte iterator
     */
    public ByteIterator asUtf8(final boolean escapeNul) {
        return new Utf8EncodingByteIterator(this, escapeNul);
    }

    /**
     * Get a code point iterator for a string.
     *
     * @param string the string
     * @return the code point iterator
     */
    public static CodePointIterator ofString(final String string) {
        return ofString(string, 0, string.length());
    }

    /**
     * Get a code point iterator for a string.
     *
     * @param string the string
     * @return the code point iterator
     */
    public static CodePointIterator ofString(final String string, final int offs, final int len) {
        if (len == 0) {
            return EMPTY;
        }
        return new StringIterator(len, string, offs);
    }

    /**
     * Get a code point iterator for a character array.
     *
     * @param chars the array
     * @return the code point iterator
     */
    public static CodePointIterator ofChars(final char[] chars) {
        return ofChars(chars, 0, chars.length);
    }

    /**
     * Get a code point iterator for a character array.
     *
     * @param chars the array
     * @param offs the array offset
     * @return the code point iterator
     */
    public static CodePointIterator ofChars(final char[] chars, final int offs) {
        return ofChars(chars, offs, chars.length - offs);
    }

    /**
     * Get a code point iterator for a character array.
     *
     * @param chars the array
     * @param offs the array offset
     * @param len the number of characters to include
     * @return the code point iterator
     */
    public static CodePointIterator ofChars(final char[] chars, final int offs, final int len) {
        if (len <= 0) {
            return EMPTY;
        }
        return new CharArrayIterator(len, chars, offs);
    }

    /**
     * Get a code point iterator for a UTF-8 encoded byte array.
     *
     * @param bytes the array
     * @return the code point iterator
     */
    public static CodePointIterator ofUtf8Bytes(final byte[] bytes) {
        return ofUtf8Bytes(bytes, 0, bytes.length);
    }

    /**
     * Get a code point iterator for a UTF-8 encoded array.
     *
     * @param bytes the array
     * @param offs the array offset
     * @param len the number of characters to include
     * @return the code point iterator
     */
    public static CodePointIterator ofUtf8Bytes(final byte[] bytes, final int offs, final int len) {
        if (len <= 0) {
            return EMPTY;
        }
        return ByteIterator.ofBytes(bytes, offs, len).asUtf8String();
    }

    /**
     * Get a code point iterator for a ISO-8859-1 (Latin-1) encoded array.
     *
     * @param bytes the array
     * @return the code point iterator
     */
    public static CodePointIterator ofLatin1Bytes(final byte[] bytes) {
        return ofLatin1Bytes(bytes, 0, bytes.length);
    }

    /**
     * Get a code point iterator for a ISO-8859-1 (Latin-1) encoded array.
     *
     * @param bytes the array
     * @param offs the array offset
     * @param len the number of characters to include
     * @return the code point iterator
     */
    public static CodePointIterator ofLatin1Bytes(final byte[] bytes, final int offs, final int len) {
        if (len <= 0) {
            return EMPTY;
        }
        return ByteIterator.ofBytes(bytes, offs, len).asLatin1String();
    }

    /**
     * Get a sub-iterator that removes the following code points: <code>10</code>(\n) and <code>13</code>(\r).
     *
     * @return the code point iterator
     */
    public CodePointIterator skipCrLf() {
        return skip(value -> value == '\n' || value == '\r');
    }

    /**
     * Get a sub-iterator that removes code points based on a <code>predicate</code>.
     *
     * @param predicate a {@link IntPredicate} that evaluates the code points that should be skipper. Returning true from the predicate
     * indicates that the code point must be skipped.
     * @return the code point iterator
     */
    public CodePointIterator skip(IntPredicate predicate) {
        if (!hasNext()) {
            return EMPTY;
        }
        return new SkippingCodePointIterator(this, predicate);
    }

    private static final char[] NO_CHARS = new char[0];

    /**
     * The empty code point iterator.
     */
    public static final CodePointIterator EMPTY = new CharArrayIterator(0, NO_CHARS, 0);
}
