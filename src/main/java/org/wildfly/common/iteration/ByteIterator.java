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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.crypto.Mac;

import org.wildfly.common.Assert;
import org.wildfly.common.bytes.ByteStringBuilder;
import org.wildfly.common.codec.Base32Alphabet;
import org.wildfly.common.codec.Base64Alphabet;

/**
 * A byte iterator.
 */
public abstract class ByteIterator implements BiDirIntIterator, IndexIterator {

    private static final int OP_BUFFER_SIZE = 8192;

    private static final ThreadLocal<byte[]> OP_BUFFER = new ThreadLocal<byte[]>() {
        protected byte[] initialValue() {
            return new byte[OP_BUFFER_SIZE];
        }
    };

    ByteIterator() {
    }

    /**
     * Determine if there are more bytes after the current byte.
     *
     * @return {@code true} if there are more bytes, {@code false} otherwise
     */
    public abstract boolean hasNext();

    /**
     * Determine if there are more bytes before the current byte.
     *
     * @return {@code true} if there are more bytes, {@code false} otherwise
     */
    public abstract boolean hasPrevious();

    /**
     * Get the next byte.
     *
     * @return the next byte
     * @throws NoSuchElementException if {@link #hasNext()} returns {@code false}
     */
    public abstract int next() throws NoSuchElementException;

    /**
     * Peek at the next byte without advancing.
     *
     * @return the next byte
     * @throws NoSuchElementException if {@link #hasNext()} returns {@code false}
     */
    public abstract int peekNext() throws NoSuchElementException;

    /**
     * Get the previous byte.
     *
     * @return the previous byte
     * @throws NoSuchElementException if {@link #hasPrevious()} returns {@code false}
     */
    public abstract int previous() throws NoSuchElementException;

    /**
     * Peek at the previous byte without moving backwards.
     *
     * @return the previous byte
     * @throws NoSuchElementException if {@link #hasPrevious()} returns {@code false}
     */
    public abstract int peekPrevious() throws NoSuchElementException;

    /**
     * Get the current offset, in bytes.
     *
     * @return the byte offset
     */
    public abstract long getIndex();

    public int getBE16() throws NoSuchElementException {
        return next() << 8 | next();
    }

    public int getBE32() throws NoSuchElementException {
        return next() << 24 | next() << 16 | next() << 8 | next();
    }

    public long getBE64() throws NoSuchElementException {
        return (long)next() << 56 | (long)next() << 48 | (long)next() << 40 | (long)next() << 32 | (long)next() << 24 | (long)next() << 16 | (long)next() << 8 | (long)next();
    }

    public int getLE16() throws NoSuchElementException {
        return next() | next() << 8;
    }

    public int getLE32() throws NoSuchElementException {
        return next() | next() << 8 | next() << 16 | next() << 24;
    }

    public long getLE64() throws NoSuchElementException {
        return (long) next() | (long) next() << 8 | (long) next() << 16 | (long) next() << 24 | (long) next() << 32 | (long) next() << 40 | (long) next() << 48 | (long) next() << 52;
    }

    public int getPackedBE32() throws NoSuchElementException {
        int v = next();
        int t = 0;
        while ((v & 0x80) != 0) {
            t = t << 7 | v & 0x7f;
            v = next();
        }
        t = t << 7 | v;
        return t;
    }

    public long getPackedBE64() throws NoSuchElementException {
        int v = next();
        long t = 0;
        while ((v & 0x80) != 0) {
            t = t << 7 | (long)(v & 0x7f);
            v = next();
        }
        t = t << 7 | v;
        return t;
    }

    public ByteStringBuilder appendTo(final ByteStringBuilder builder) {
        final byte[] buffer = OP_BUFFER.get();
        int cnt = drain(buffer);
        while (cnt > 0) {
            builder.append(buffer, 0, cnt);
            cnt = drain(buffer);
        }
        return builder;
    }

    public void update(MessageDigest digest) {
        final byte[] buffer = OP_BUFFER.get();
        int cnt = drain(buffer);
        while (cnt > 0) {
            digest.update(buffer, 0, cnt);
            cnt = drain(buffer);
        }
    }

    public ByteIterator doFinal(MessageDigest digest) {
        update(digest);
        return ByteIterator.ofBytes(digest.digest());
    }

    public void update(Mac mac) {
        final byte[] buffer = OP_BUFFER.get();
        int cnt = drain(buffer);
        while (cnt > 0) {
            mac.update(buffer, 0, cnt);
            cnt = drain(buffer);
        }
    }

    public ByteIterator doFinal(Mac mac) {
        return ByteIterator.ofBytes(mac.doFinal(drain()));
    }

    public void update(Signature signature) throws SignatureException {
        final byte[] buffer = OP_BUFFER.get();
        int cnt = drain(buffer);
        while (cnt > 0) {
            signature.update(buffer, 0, cnt);
            cnt = drain(buffer);
        }
        signature.update(drain());
    }

    public ByteIterator sign(Signature signature) throws SignatureException {
        update(signature);
        return ByteIterator.ofBytes(signature.sign());
    }

    public boolean verify(Signature signature) throws SignatureException {
        final byte[] buffer = OP_BUFFER.get();
        int cnt = drain(buffer);
        while (cnt > 0) {
            signature.update(buffer, 0, cnt);
            cnt = drain(buffer);
        }
        return signature.verify(NO_BYTES);
    }

    /**
     * Base64-encode the current stream.
     *
     * @param alphabet the alphabet to use
     * @param addPadding {@code true} to add trailing padding, {@code false} to leave it off
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base64Encode(final Base64Alphabet alphabet, final boolean addPadding) {
        if (alphabet.isLittleEndian()) {
            return new LittleEndianBase64EncodingIterator(this, addPadding, alphabet);
        } else {
            return new BigEndianBase64EncodingIterator(this, addPadding, alphabet);
        }
    }

    /**
     * Base64-encode the current stream.
     *
     * @param alphabet the alphabet to use
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base64Encode(final Base64Alphabet alphabet) {
        return base64Encode(alphabet, true);
    }

    /**
     * Base64-encode the current stream.
     *
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base64Encode() {
        return base64Encode(Base64Alphabet.STANDARD, true);
    }

    /**
     * Base32-encode the current stream.
     *
     * @param alphabet the alphabet to use
     * @param addPadding {@code true} to add trailing padding, {@code false} to leave it off
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base32Encode(final Base32Alphabet alphabet, final boolean addPadding) {
        if (alphabet.isLittleEndian()) {
            return new LittleEndianBase32EncodingIterator(this, addPadding, alphabet);
        } else {
            return new BigEndianBase32EncodingIterator(this, addPadding, alphabet);
        }
    }

    /**
     * Base32-encode the current stream.
     *
     * @param alphabet the alphabet to use
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base32Encode(final Base32Alphabet alphabet) {
        return base32Encode(alphabet, true);
    }

    /**
     * Base32-encode the current stream.
     *
     * @return an iterator over the encoded characters
     */
    public CodePointIterator base32Encode() {
        return base32Encode(Base32Alphabet.STANDARD, true);
    }

    /**
     * Hex-encode the current stream.
     *
     * @param toUpperCase {@code true} to use upper case characters when encoding,
     * {@code false} to use lower case characters
     * @return an iterator over the encoded characters
     */
    public CodePointIterator hexEncode(boolean toUpperCase) {
        return new Base16EncodingCodePointIterator(this, toUpperCase);
    }

    /**
     * Hex-encode the current stream.
     *
     * @return an iterator over the encoded characters
     */
    public CodePointIterator hexEncode() {
        return hexEncode(false);
    }

    /**
     * Get this byte iterator as a UTF-8 string.
     *
     * @return the code point iterator
     */
    public CodePointIterator asUtf8String() {
        if (! hasNext()) {
            return CodePointIterator.EMPTY;
        }
        return new Utf8DecodingIterator(this);
    }

    /**
     * Get this byte iterator as a Latin-1 string.
     *
     * @return the code point iterator
     */
    public CodePointIterator asLatin1String() {
        if (! hasNext()) {
            return CodePointIterator.EMPTY;
        }
        return new Latin1DecodingIterator(this, getIndex());
    }

    /**
     * Determine if the remaining contents of this iterator are identical to the remaining contents of the other iterator.  If the
     * contents are not equal, the iterators will be positioned at the location of the first difference.  If the contents
     * are equal, the iterators will both be positioned at the end of their contents.
     *
     * @param other the other byte iterator
     * @return {@code true} if the contents are equal, {@code false} otherwise
     */
    public final boolean contentEquals(ByteIterator other) {
        Assert.checkNotNullParam("other", other);
        for (;;) {
            if (hasNext()) {
                if (! other.hasNext()) {
                    return false;
                }
                if (next() != other.next()) {
                    return false;
                }
            } else {
                return ! other.hasNext();
            }
        }
    }

    /**
     * Return a copy of this iterator which is limited to the given number of bytes after the current one.  Advancing
     * the returned iterator will also advance this one.
     *
     * @param size the number of bytes
     * @return the limited byte iterator
     */
    public final ByteIterator limitedTo(final int size) {
        if (size <= 0 || ! hasNext()) {
            return EMPTY;
        }
        return new LimitedByteIterator(this, size);
    }

    /**
     * Get a sub-iterator that is delimited by the given bytes.  The returned iterator offset starts at 0 and cannot
     * be backed up before that point.  The returned iterator will return {@code false} for {@code hasNext()} if the next
     * character in the encapsulated iterator is a delimiter or if the underlying iterator returns {@code false} for
     * {@code hasNext()}.
     *
     * @param delims the byte delimiters
     * @return the sub-iterator
     */
    public final ByteIterator delimitedBy(final int... delims) {
        if ((delims == null) || (delims.length == 0) || ! hasNext()) {
            return EMPTY;
        }
        for (int delim : delims) {
            if (delim < 0 || delim > 0xff) {
                return EMPTY;
            }
        }
        return new DelimitedByteIterator(this, delims);
    }

    /**
     * Get a byte iterator which translates this byte iterator through an interleaving table.  The table should be
     * 256 entries in size or exceptions may result.
     *
     * @param table the interleaving table
     * @return the interleaving byte iterator
     */
    public ByteIterator interleavedWith(final byte[] table) {
        return new ByteTableTranslatingByteIterator(this, table);
    }

    /**
     * Get a byte iterator which translates this byte iterator through an interleaving table.  The table should be
     * 256 entries in size or exceptions may result.
     *
     * @param table the interleaving table
     * @return the interleaving byte iterator
     */
    public ByteIterator interleavedWith(final int[] table) {
        return new IntTableTranslatingByteIterator(this, table);
    }

    /**
     * Drain all the remaining bytes in this iterator to the given stream.
     *
     * @param stream the stream
     * @return the same stream
     */
    public ByteArrayOutputStream drainTo(ByteArrayOutputStream stream) {
        while (hasNext()) {
            stream.write(next());
        }
        return stream;
    }

    /**
     * Drain all the remaining bytes in this iterator.
     *
     * @return the remaining bytes as a single array
     */
    public byte[] drain() {
        return drainTo(new ByteArrayOutputStream()).toByteArray();
    }

    /**
     * Drain up to {@code count} bytes from this iterator, returning the result.
     *
     * @param count the number of bytes to read
     * @return the array of consumed bytes (may be smaller than {@code count})
     */
    public byte[] drain(int count) {
        if (count == 0) return NO_BYTES;
        final byte[] b = new byte[count];
        final int cnt = drain(b);
        return cnt == 0 ? NO_BYTES : cnt < b.length ? Arrays.copyOf(b, cnt) : b;
    }

    /**
     * Drain exactly {@code count} bytes from this iterator, returning the result.
     *
     * @param count the number of bytes to read
     * @return the array of consumed bytes
     * @throws NoSuchElementException if there are not enough bytes to fill the array
     */
    public byte[] drainAll(int count) throws NoSuchElementException {
        if (count == 0) return NO_BYTES;
        final byte[] b = new byte[count];
        final int cnt = drain(b);
        if (cnt < b.length) {
            throw new NoSuchElementException();
        }
        return b;
    }

    /**
     * Drains up to {@code dst.length} bytes from this iterator into the given {@code dst} array.
     * An attempt is made to drain as many as {@code dst.length} bytes, but a smaller number may
     * be drained.
     * <p>
     * The number of bytes actually drained is returned as an integer. Unlike
     * {@link InputStream#read(byte[], int, int)}, this method never returns a negative result.
     *
     * @param dst the buffer into which the data is drained
     * @return the total number of bytes drained into {@code dst}, always greater or equal to {@code 0}
     */
    public int drain(byte[] dst) {
        return drain(dst, 0, dst.length);
    }

    /**
     * Drains up to {@code len} bytes from this iterator into the given {@code dst} array.
     * An attempt is made to drain as many as {@code len} bytes, but a smaller number may
     * be drained.
     * <p>
     * The number of bytes actually drained is returned as an integer. Unlike
     * {@link InputStream#read(byte[], int, int)}, this method never returns a negative result.
     *
     * @param dst the buffer into which the data is drained
     * @param offs the start offset in array {@code dst} at which the data is written.
     * @param len the maximum number of bytes to drain
     * @return the total number of bytes drained into {@code dst}, always greater or equal to {@code 0}
     */
    public int drain(byte[] dst, int offs, int len) {
        for (int i = 0; i < len; i ++) {
            if (! hasNext()) return i;
            dst[offs + i] = (byte) next();
        }
        return len;
    }

    /**
     * Convenience method to directly drain a certain number of bytes to a UTF-8 string.  If fewer than {@code count}
     * bytes are available, only the available bytes will be used to construct the string.
     *
     * @param count the maximum number of bytes to consume
     * @return the UTF-8 string
     */
    public String drainToUtf8(int count) {
        return new String(drain(count), StandardCharsets.UTF_8);
    }

    /**
     * Convenience method to directly drain a certain number of bytes to a Latin-1 string.  If fewer than {@code count}
     * bytes are available, only the available bytes will be used to construct the string.
     *
     * @param count the maximum number of bytes to consume
     * @return the Latin-1 string
     */
    public String drainToLatin1(int count) {
        return new String(drain(count), StandardCharsets.ISO_8859_1);
    }

    /**
     * Get a byte iterator for a byte array.
     *
     * @param bytes the array
     * @return the byte iterator
     */
    public static ByteIterator ofBytes(final byte... bytes) {
        Assert.checkNotNullParam("bytes", bytes);
        return ofBytes(bytes, 0, bytes.length);
    }

    /**
     * Get a byte iterator for a byte array.
     *
     * @param bytes the array
     * @param offs the array offset
     * @param len the number of bytes to include
     * @return the byte iterator
     */
    public static ByteIterator ofBytes(final byte[] bytes, final int offs, final int len) {
        Assert.checkNotNullParam("bytes", bytes);
        if (len <= 0) {
            return EMPTY;
        }
        return new ByteArrayIterator(len, bytes, offs);
    }

    /**
     * Get a byte iterator for a byte array with interleave.
     *
     * @param bytes the array
     * @param offs the array offset
     * @param len the number of bytes to include
     * @param interleave the interleave table to use
     * @return the byte iterator
     */
    public static ByteIterator ofBytes(final byte[] bytes, final int offs, final int len, final int[] interleave) {
        Assert.checkNotNullParam("bytes", bytes);
        Assert.checkNotNullParam("interleave", interleave);
        if (len <= 0) {
            return EMPTY;
        }
        return new InterleavedByteArrayIterator(len, bytes, offs, interleave);
    }

    /**
     * Get a byte iterator for a byte array with interleave.
     *
     * @param bytes the array
     * @param interleave the interleave table to use
     * @return the byte iterator
     */
    public static ByteIterator ofBytes(final byte[] bytes, final int[] interleave) {
        return ofBytes(bytes, 0, bytes.length, interleave);
    }

    /**
     * Get a byte iterator for a byte buffer.  The buffer's position is kept up to date with the number of bytes
     * consumed by the iterator.  The iterator cannot be moved before the position that the buffer had when the
     * iterator was constructed (this position is considered the zero offset).
     *
     * @param buffer the byte buffer (must not be {@code null})
     * @return the byte iterator (not {@code null})
     */
    public static ByteIterator ofByteBuffer(ByteBuffer buffer) {
        Assert.checkNotNullParam("buffer", buffer);
        return new ByteBufferIterator(buffer);
    }

    /**
     * Get a concatenated byte iterator.  The array and the byte iterators in the array must not be modified or
     * inconsistent behavior will result.
     *
     * @param iterators the iterators array (must not be {@code null} or contain {@code null} elements)
     * @return a concatenated iterator
     */
    public static ByteIterator ofIterators(ByteIterator... iterators) {
        Assert.checkNotNullParam("iterators", iterators);
        if (iterators.length == 0) return EMPTY;
        if (iterators.length == 1) return iterators[0];
        return new ConcatByteIterator(iterators);
    }

    private static final byte[] NO_BYTES = new byte[0];

    /**
     * The empty byte iterator.
     */
    public static final ByteIterator EMPTY = new ByteArrayIterator(0, NO_BYTES, 0);

    /**
     * Get this iterator as an input stream.
     *
     * @return the input stream (not {@code null})
     */
    public final InputStream asInputStream() {
        return new ByteIteratorInputStream(this);
    }
}
