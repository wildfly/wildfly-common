package org.wildfly.common.iteration;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.NoSuchElementException;

import javax.crypto.Mac;

import org.wildfly.common.bytes.ByteStringBuilder;

/**
 */
final class ByteArrayIterator extends ByteIterator {
    private final int len;
    private final byte[] bytes;
    private final int offs;
    private int idx;

    ByteArrayIterator(final int len, final byte[] bytes, final int offs) {
        this.len = len;
        this.bytes = bytes;
        this.offs = offs;
        idx = 0;
    }

    public boolean hasNext() {
        return idx < len;
    }

    public boolean hasPrevious() {
        return idx > 0;
    }

    public int next() {
        if (! hasNext()) throw new NoSuchElementException();
        return bytes[offs + idx++] & 0xff;
    }

    public int previous() {
        if (! hasPrevious()) throw new NoSuchElementException();
        return bytes[offs + -- idx] & 0xff;
    }

    public int peekNext() throws NoSuchElementException {
        if (! hasNext()) throw new NoSuchElementException();
        return bytes[offs + idx] & 0xff;
    }

    public int peekPrevious() throws NoSuchElementException {
        if (! hasPrevious()) throw new NoSuchElementException();
        return bytes[offs + idx - 1] & 0xff;
    }

    public long getIndex() {
        return idx;
    }

    public void update(final MessageDigest digest) throws IllegalStateException {
        digest.update(bytes, offs + idx, len - idx);
        idx = len;
    }

    public ByteIterator doFinal(final MessageDigest digest) throws IllegalStateException {
        update(digest);
        return ByteIterator.ofBytes(digest.digest());
    }

    public void update(final Mac mac) throws IllegalStateException {
        mac.update(bytes, offs + idx, len - idx);
        idx = len;
    }

    public ByteIterator doFinal(final Mac mac) throws IllegalStateException {
        update(mac);
        return ByteIterator.ofBytes(mac.doFinal());
    }

    public void update(final Signature signature) throws SignatureException {
        signature.update(bytes, offs + idx, len - idx);
        idx = len;
    }

    public boolean verify(final Signature signature) throws SignatureException {
        try {
            return signature.verify(bytes, offs + idx, len - idx);
        } finally {
            idx = len;
        }
    }

    public ByteArrayOutputStream drainTo(final ByteArrayOutputStream stream) {
        stream.write(bytes, offs + idx, len - idx);
        idx = len;
        return stream;
    }

    public byte[] drain() {
        try {
            return Arrays.copyOfRange(bytes, offs + idx, offs + len);
        } finally {
            idx = len;
        }
    }

    public int drain(final byte[] dst, final int offs, final int dlen) {
        int cnt = Math.min(len - idx, dlen);
        System.arraycopy(bytes, offs + idx, dst, offs, cnt);
        idx += cnt;
        return cnt;
    }

    public String drainToUtf8(final int count) {
        int cnt = Math.min(len - idx, count);
        String s = new String(bytes, idx, cnt, StandardCharsets.UTF_8);
        idx += cnt;
        return s;
    }

    public String drainToLatin1(final int count) {
        int cnt = Math.min(len - idx, count);
        String s = new String(bytes, idx, cnt, StandardCharsets.ISO_8859_1);
        idx += cnt;
        return s;
    }

    public ByteStringBuilder appendTo(final ByteStringBuilder builder) {
        builder.append(bytes, offs + idx, len - idx);
        idx = len;
        return builder;
    }
}
