/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.archive;

import static java.lang.Math.min;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.wildfly.common.Assert;

/**
 */
public final class Archive implements Closeable {

    public static final int GP_ENCRYPTED = 1 << 0;

    // only if method == implode
    public static final int GP_IMPLODE_8K_DICTIONARY = 1 << 1;
    public static final int GP_IMPLODE_3_TREES = 1 << 2;

    // only if method == deflate
    public static final int GP_DEFLATE_COMP_OPT_MASK        = 0b11 << 1;

    public static final int GP_DEFLATE_COMP_OPT_NORMAL      = 0b00 << 1;
    public static final int GP_DEFLATE_COMP_OPT_MAXIMUM     = 0b01 << 1;
    public static final int GP_DEFLATE_COMP_OPT_FAST        = 0b10 << 1;
    public static final int GP_DEFLATE_COMP_OPT_SUPER_FAST  = 0b11 << 1;

    // only if method == lzma
    public static final int GP_LZMA_EOS_USED = 1 << 1;

    public static final int GP_LATE_SIZES = 1 << 3;
    // reserved 1 << 4
    public static final int GP_COMPRESSED_PATCHED = 1 << 5;
    public static final int GP_STRONG_ENCRYPTION = 1 << 6;
    // reserved 1 << 7
    // reserved 1 << 8
    // reserved 1 << 9
    // reserved 1 << 10
    public static final int GP_UTF_8 = 1 << 11;
    // reserved 1 << 12
    public static final int GP_CD_MASKED = 1 << 13;
    // reserved 1 << 14
    // reserved 1 << 15

    public static final int METHOD_STORED = 0;
    public static final int METHOD_SHRINK = 1;
    public static final int METHOD_REDUCE_1 = 2;
    public static final int METHOD_REDUCE_2 = 3;
    public static final int METHOD_REDUCE_3 = 4;
    public static final int METHOD_REDUCE_4 = 5;
    public static final int METHOD_IMPLODE = 6;
    public static final int METHOD_DEFLATE = 8;
    public static final int METHOD_DEFLATE64 = 9;
    public static final int METHOD_BZIP2 = 12;
    public static final int METHOD_LZMA = 14;

    public static final int MADE_BY_MS_DOS = 0;
    public static final int MADE_BY_UNIX = 3;
    public static final int MADE_BY_NTFS = 10;
    public static final int MADE_BY_OS_X = 19;

    public static final int SIG_LH = 0x04034b50;

    public static final int LH_SIGNATURE = 0;
    public static final int LH_MIN_VERSION = 4;
    public static final int LH_GP_BITS = 6;
    public static final int LH_COMP_METHOD = 8;
    public static final int LH_MOD_TIME = 10;
    public static final int LH_MOD_DATE = 12;
    public static final int LH_CRC_32 = 14;
    public static final int LH_COMPRESSED_SIZE = 18;
    public static final int LH_UNCOMPRESSED_SIZE = 22;
    public static final int LH_FILE_NAME_LENGTH = 26;
    public static final int LH_EXTRA_LENGTH = 28;
    public static final int LH_END = 30;

    public static final int SIG_DD = 0x08074b50;

    public static final int DD_SIGNATURE = 0;
    public static final int DD_CRC_32 = 4;
    public static final int DD_COMPRESSED_SIZE = 8;
    public static final int DD_UNCOMPRESSED_SIZE = 12;
    public static final int DD_END = 16;
    public static final int DD_ZIP64_COMPRESSED_SIZE = 8;
    public static final int DD_ZIP64_UNCOMPRESSED_SIZE = 16;
    public static final int DD_ZIP64_END = 24;

    public static final int SIG_CDE = 0x02014b50;

    public static final int CDE_SIGNATURE = 0;
    public static final int CDE_VERSION_MADE_BY = 4;
    public static final int CDE_VERSION_NEEDED = 6;
    public static final int CDE_GP_BITS = 8;
    public static final int CDE_COMP_METHOD = 10;
    public static final int CDE_MOD_TIME = 12;
    public static final int CDE_MOD_DATE = 14;
    public static final int CDE_CRC_32 = 16;
    public static final int CDE_COMPRESSED_SIZE = 20;
    public static final int CDE_UNCOMPRESSED_SIZE = 24;
    public static final int CDE_FILE_NAME_LENGTH = 28;
    public static final int CDE_EXTRA_LENGTH = 30;
    public static final int CDE_COMMENT_LENGTH = 32;
    public static final int CDE_FIRST_DISK_NUMBER = 34;
    public static final int CDE_INTERNAL_ATTRIBUTES = 36;
    public static final int CDE_EXTERNAL_ATTRIBUTES = 38;
    public static final int CDE_LOCAL_HEADER_OFFSET = 42; // relative to the start of the above first disk number
    public static final int CDE_END = 46;

    public static final int SIG_EOCD = 0x06054b50;

    public static final int EOCD_SIGNATURE = 0;
    public static final int EOCD_DISK_NUMBER = 4;
    public static final int EOCD_CD_FIRST_DISK_NUMBER = 6;
    public static final int EOCD_CDE_COUNT_THIS_DISK = 8;
    public static final int EOCD_CDE_COUNT_ALL = 10;
    public static final int EOCD_CD_SIZE = 12;
    public static final int EOCD_CD_START_OFFSET = 16;
    public static final int EOCD_COMMENT_LENGTH = 20;
    public static final int EOCD_END = 22;

    public static final int EXT_ID_ZIP64 = 0x0001;

    public static final int ZIP64_UNCOMPRESSED_SIZE = 0;
    public static final int ZIP64_COMPRESSED_SIZE = 8;
    public static final int ZIP64_LOCAL_HEADER_OFFSET = 16;
    public static final int ZIP64_FIRST_DISK_NUMBER = 24; // 4 bytes
    public static final int ZIP64_END = 28;

    public static final int EXT_ID_UNIX = 0x000d;

    public static final int UNIX_ACCESS_TIME = 0;
    public static final int UNIX_MODIFIED_TIME = 4;
    public static final int UNIX_UID = 8;
    public static final int UNIX_GID = 10;
    public static final int UNIX_END = 12; // symlink target

    public static final int UNIX_DEV_MAJOR = 12; // if it's a device node
    public static final int UNIX_DEV_MINOR = 16;
    public static final int UNIX_DEV_END = 20;

    public static final int SIG_EOCD_ZIP64 = 0x06064b50;

    public static final int EOCD_ZIP64_SIGNATURE = 0;
    public static final int EOCD_ZIP64_SIZE = 4;
    public static final int EOCD_ZIP64_VERSION_MADE_BY = 12;
    public static final int EOCD_ZIP64_VERSION_NEEDED = 14;
    public static final int EOCD_ZIP64_DISK_NUMBER = 16;
    public static final int EOCD_ZIP64_CD_FIRST_DISK_NUMBER = 20;
    public static final int EOCD_ZIP64_CDE_COUNT_THIS_DISK = 24;
    public static final int EOCD_ZIP64_CDE_COUNT_ALL = 28;
    public static final int EOCD_ZIP64_CD_SIZE = 36;
    public static final int EOCD_ZIP64_CD_START_OFFSET = 44;
    public static final int EOCD_ZIP64_END = 52;

    public static final int SIG_EOCDL_ZIP64 = 0x07064b50;

    public static final int EOCDL_ZIP64_SIGNATURE = 0;
    public static final int EOCDL_ZIP64_EOCD_DISK_NUMBER = 4;
    public static final int EOCDL_ZIP64_EOCD_OFFSET = 8;
    public static final int EOCDL_ZIP64_DISK_COUNT = 16;
    public static final int EOCDL_ZIP64_END = 20;

    /**
     * Maximum size of a single buffer.
     */
    private static final int BUF_SIZE_MAX = 0x4000_0000;
    private static final int BUF_SHIFT = Integer.numberOfTrailingZeros(BUF_SIZE_MAX);
    private static final int BUF_SIZE_MASK = BUF_SIZE_MAX - 1;

    private final ByteBuffer[] bufs;
    private final long offset;
    private final long length;
    private final long cd;
    private final Index index;

    private Archive(final ByteBuffer[] bufs, final long offset, final long length, final long cd, final Index index) {
        this.bufs = bufs;
        this.offset = offset;
        this.length = length;
        this.cd = cd;
        this.index = index;
    }

    public static Archive open(Path path) throws IOException {
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.READ)) {
            long size = fc.size();
            final int bufCnt = Math.toIntExact((size + BUF_SIZE_MASK) >> BUF_SHIFT);
            final ByteBuffer[] array = new ByteBuffer[bufCnt];
            long offs = 0;
            int idx = 0;
            while (size > BUF_SIZE_MASK) {
                array[idx++] = fc.map(FileChannel.MapMode.READ_ONLY, offs, BUF_SIZE_MAX).order(ByteOrder.LITTLE_ENDIAN);
                size -= BUF_SIZE_MAX;
                offs += BUF_SIZE_MAX;
            }
            array[idx] = fc.map(FileChannel.MapMode.READ_ONLY, offs, size).order(ByteOrder.LITTLE_ENDIAN);
            return open(array);
        }
    }

    public static Archive open(ByteBuffer buf) throws IOException {
        Assert.checkNotNullParam("buf", buf);
        if (buf.order() == ByteOrder.BIG_ENDIAN) {
            buf = buf.duplicate().order(ByteOrder.LITTLE_ENDIAN);
        }
        return open(new ByteBuffer[] { buf });
    }

    static Archive open(ByteBuffer[] bufs) throws IOException {
        return open(bufs, 0, capacity(bufs));
    }

    static Archive open(ByteBuffer[] bufs, long offset, long length) throws IOException {
        // find the directory by looking first at its expected location and working our way backwards
        long eocd = length - EOCD_END;
        // todo: this could be optimized a bit (boyer-moore for example)
        while (getUnsignedInt(bufs, offset + eocd) != SIG_EOCD) {
            if (eocd == 0) {
                throw new IOException("Invalid archive");
            }
            eocd--;
        }
        int entries = getUnsignedShort(bufs, offset + eocd + EOCD_CDE_COUNT_ALL);
        // validate the EOCD record
        if (getUnsignedShort(bufs, offset + eocd + EOCD_CD_FIRST_DISK_NUMBER) != 0 || getUnsignedShort(bufs, offset + eocd + EOCD_DISK_NUMBER) != 0 || entries != getUnsignedShort(bufs, offset + eocd + EOCD_CDE_COUNT_THIS_DISK)) {
            throw new IOException("Multi-disk archives are not supported");
        }
        // check now for zip64
        long eocdLocZip64 = eocd - EOCDL_ZIP64_END;
        long eocdZip64 = -1;
        if (getInt(bufs, offset + eocdLocZip64 + EOCDL_ZIP64_SIGNATURE) == SIG_EOCDL_ZIP64) {
            // probably zip64
            // validate the EOCDL_ZIP64
            if (getInt(bufs, offset + eocdLocZip64 + EOCDL_ZIP64_DISK_COUNT) != 1 || getInt(bufs, offset + eocdLocZip64 + EOCDL_ZIP64_EOCD_DISK_NUMBER) != 0) {
                throw new IOException("Multi-disk archives are not supported");
            }
            eocdZip64 = getLong(bufs, offset + eocdLocZip64 + EOCDL_ZIP64_EOCD_OFFSET);
            if (getUnsignedInt(bufs, offset + eocdZip64 + EOCD_ZIP64_SIGNATURE) != SIG_EOCD_ZIP64) {
                // oops, zip64 isn't really present, just bad luck
                eocdZip64 = -1;
            }
        }
        long cd;
        cd = getUnsignedInt(bufs, offset + eocd + EOCD_CD_START_OFFSET);
        if (cd == 0xffffffffL && eocdZip64 != -1) {
            cd = getLong(bufs, offset + eocdZip64 + EOCD_ZIP64_CD_START_OFFSET);
        }
        if (entries == 0xffff && eocdZip64 != -1) {
            final long cnt = getUnsignedInt(bufs, offset + eocdZip64 + EOCD_ZIP64_CDE_COUNT_ALL);
            if (cnt > 0x07ff_ffffL) {
                throw new IOException("Archive has too many entries");
            }
            entries = (int) cnt;
        }

        // generate the index

        Index index;
        if (length <= 0xfffe) {
            index = new TinyIndex(entries);
        } else if (length <= 0xffff_ffffeL) {
            index = new LargeIndex(entries);
        } else {
            index = new HugeIndex(entries);
        }
        // iterate the directory
        final int mask = index.getMask();
        long cde = cd;
        for (int i = 0; i < entries; i ++) {
            if (getInt(bufs, offset + cde + CDE_SIGNATURE) != SIG_CDE) {
                throw new IOException("Archive appears to be corrupted");
            }
            int hc = getHashCodeOfEntry(bufs, offset + cde);
            index.put(hc & mask, cde);
            cde = cde + CDE_END + getUnsignedShort(bufs, offset + cde + CDE_FILE_NAME_LENGTH) + getUnsignedShort(bufs, offset + cde + CDE_EXTRA_LENGTH) + getUnsignedShort(bufs, offset + cde + CDE_COMMENT_LENGTH);
        }
        return new Archive(bufs, offset, length, cd, index);
    }

    private static String getNameOfEntry(ByteBuffer[] bufs, long cde) {
        long name = cde + CDE_END;
        int nameLen = getUnsignedShort(bufs, cde + CDE_FILE_NAME_LENGTH);
        boolean utf8 = (getUnsignedShort(bufs, cde + CDE_GP_BITS) & GP_UTF_8) != 0;
        if (utf8) {
            return new String(getBytes(bufs, name, nameLen), StandardCharsets.UTF_8);
        } else {
            char[] nameChars = new char[nameLen];
            for (int i = 0; i < nameLen; i ++) {
                nameChars[i] = Cp437.charFor(getUnsignedByte(bufs, name + i));
            }
            return new String(nameChars);
        }
    }

    private static int getHashCodeOfEntry(ByteBuffer[] bufs, long cde) {
        long name = cde + CDE_END;
        int nameLen = getUnsignedShort(bufs, cde + CDE_FILE_NAME_LENGTH);
        boolean utf8 = (getUnsignedShort(bufs, cde + CDE_GP_BITS) & GP_UTF_8) != 0;
        int hc = 0;
        if (utf8) {
            int cp;
            for (int i = 0; i < nameLen; i += Utf8.getByteCount(getUnsignedByte(bufs, name + i))) {
                cp = Utf8.codePointAt(bufs, name + i);
                if (Character.isSupplementaryCodePoint(cp)) {
                    hc = hc * 31 + Character.highSurrogate(cp);
                    hc = hc * 31 + Character.lowSurrogate(cp);
                } else {
                    hc = hc * 31 + cp;
                }
            }
        } else {
            for (int i = 0; i < nameLen; i ++) {
                hc = hc * 31 + Cp437.charFor(getUnsignedByte(bufs, name + i));
            }
        }
        return hc;
    }

    public long getFirstEntryHandle() {
        return cd;
    }

    public long getNextEntryHandle(long entryHandle) {
        final long next = entryHandle + CDE_END + getUnsignedShort(bufs, offset + entryHandle + CDE_FILE_NAME_LENGTH) + getUnsignedShort(bufs, offset + entryHandle + CDE_EXTRA_LENGTH);
        if (next >= length || getInt(bufs, offset + next + CDE_SIGNATURE) != SIG_CDE) {
            return -1;
        }
        return next;
    }

    public long getEntryHandle(String fileName) {
        final int mask = index.getMask();
        final int base = fileName.hashCode();
        long entryHandle;
        for (int i = 0; i < mask; i ++) {
            entryHandle = index.get(base + i & mask);
            if (entryHandle == -1) {
                return -1;
            }
            if (entryNameEquals(entryHandle, fileName)) {
                return entryHandle;
            }
        }
        return -1;
    }

    public boolean entryNameEquals(final long entryHandle, final String fileName) {
        long name = entryHandle + CDE_END;
        int nameLen = getUnsignedShort(bufs, offset + entryHandle + CDE_FILE_NAME_LENGTH);
        boolean utf8 = (getUnsignedShort(bufs, offset + entryHandle + CDE_GP_BITS) & GP_UTF_8) != 0;
        final int length = fileName.length();
        if (utf8) {
            long i;
            int j;
            for (i = 0, j = 0; i < nameLen && j < length; i += Utf8.getByteCount(getUnsignedByte(bufs, offset + name + i)), j = fileName.offsetByCodePoints(j, 1)) {
                if (Utf8.codePointAt(bufs, offset + name + i) != fileName.codePointAt(j)) {
                    return false;
                }
            }
            return i == nameLen && j == length;
        } else {
            int i, j;
            for (i = 0, j = 0; i < nameLen && j < length; i++, j = fileName.offsetByCodePoints(j, 1)) {
                if (Cp437.charFor(getUnsignedByte(bufs, offset + i + entryHandle + CDE_END)) != fileName.codePointAt(j)) {
                    return false;
                }
            }
            return i == nameLen && j == length;
        }
    }

    private long getLocalHeader(long entryHandle) {
        long lh = getUnsignedInt(bufs, offset + entryHandle + CDE_LOCAL_HEADER_OFFSET);
        if (lh == 0xffff_ffffL) {
            long zip64 = getExtraRecord(entryHandle, EXT_ID_ZIP64);
            if (zip64 != -1) {
                lh = getLong(bufs, offset + zip64 + ZIP64_LOCAL_HEADER_OFFSET);
            }
        }
        return lh;
    }

    public String getEntryName(long entryHandle) {
        return getNameOfEntry(bufs, entryHandle);
    }

    public ByteBuffer getEntryContents(long entryHandle) throws IOException {
        long size = getUncompressedSize(entryHandle);
        long compSize = getCompressedSize(entryHandle);
        if (size > 0x1000_0000 || compSize > 0x1000_0000) {
            throw new IOException("Entry is too large to read into RAM");
        }
        long localHeader = getLocalHeader(entryHandle);
        if ((getUnsignedShort(bufs, offset + localHeader + LH_GP_BITS) & (GP_ENCRYPTED | GP_STRONG_ENCRYPTION)) != 0) {
            throw new IOException("Cannot read encrypted entries");
        }
        final long offset = getDataOffset(localHeader);
        final int method = getCompressionMethod(entryHandle);
        switch (method) {
            case METHOD_STORED: {
                return bufferOf(bufs, this.offset + offset, (int) size);
            }
            case METHOD_DEFLATE: {
                final Inflater inflater = new Inflater(true);
                try {
                    return JDKSpecific.inflate(inflater, bufs, this.offset + offset, (int) compSize, (int) size);
                } catch (DataFormatException e) {
                    throw new IOException(e);
                } finally {
                    inflater.end();
                }
            }
            default: {
                throw new IOException("Unsupported compression scheme");
            }
        }
    }

    private long getDataOffset(final long localHeader) {
        return localHeader + LH_END + getUnsignedShort(bufs, offset + localHeader + LH_FILE_NAME_LENGTH) + getUnsignedShort(bufs, offset + localHeader + LH_EXTRA_LENGTH);
    }

    public InputStream getEntryStream(final long entryHandle) throws IOException {
        long size = getCompressedSize(entryHandle);
        long localHeader = getLocalHeader(entryHandle);
        if ((getUnsignedShort(bufs, offset + localHeader + LH_GP_BITS) & (GP_ENCRYPTED | GP_STRONG_ENCRYPTION)) != 0) {
            throw new IOException("Cannot read encrypted entries");
        }
        final long offset = getDataOffset(localHeader);
        final int method = getCompressionMethod(entryHandle);
        switch (method) {
            case METHOD_STORED: {
                return new ByteBufferInputStream(bufs, this.offset + offset, size);
            }
            case METHOD_DEFLATE: {
                return new InflaterInputStream(new ByteBufferInputStream(bufs, this.offset + offset, size));
            }
            default: {
                throw new IOException("Unsupported compression scheme");
            }
        }
    }

    public Archive getNestedArchive(long entryHandle) throws IOException {
        long localHeader = getLocalHeader(entryHandle);
        if ((getUnsignedShort(bufs, this.offset + localHeader + LH_GP_BITS) & (GP_ENCRYPTED | GP_STRONG_ENCRYPTION)) != 0) {
            throw new IOException("Cannot read encrypted entries");
        }
        final long offset = getDataOffset(localHeader);
        final int method = getCompressionMethod(entryHandle);
        if (method != METHOD_STORED) {
            throw new IOException("Cannot open compressed nested archive");
        }
        long size = getUncompressedSize(entryHandle);
        if (size < Integer.MAX_VALUE) {
            final ByteBuffer slice = sliceOf(bufs, this.offset + offset, (int) size);
            if (slice != null) {
                return Archive.open(slice);
            }
        }
        return Archive.open(bufs, this.offset + offset, size);
    }

    public boolean isCompressed(long entryHandle) {
        return getCompressionMethod(entryHandle) != METHOD_STORED;
    }

    private int getCompressionMethod(final long entryHandle) {
        return getUnsignedShort(bufs, offset + entryHandle + CDE_COMP_METHOD);
    }

    private long getExtraRecord(final long entryHandle, final int headerId) {
        long extra = entryHandle + CDE_END + getUnsignedShort(bufs, offset + entryHandle + CDE_FILE_NAME_LENGTH) + getUnsignedShort(bufs, offset + entryHandle + CDE_COMMENT_LENGTH);
        int extraLen = getUnsignedShort(bufs, offset + entryHandle + CDE_EXTRA_LENGTH);
        for (int i = 0; i < extraLen; i = i + getUnsignedShort(bufs, offset + extra + i + 2)) {
            if (getUnsignedShort(bufs, offset + extra + i) == headerId) {
                return extra + i + 4;
            }
        }
        return -1;
    }

    public long getUncompressedSize(long entryHandle) {
        long size = getUnsignedInt(bufs, offset + entryHandle + CDE_UNCOMPRESSED_SIZE);
        if (size == 0xffffffff) {
            long zip64 = getExtraRecord(entryHandle, EXT_ID_ZIP64);
            if (zip64 != -1) {
                size = getLong(bufs, offset + zip64 + ZIP64_UNCOMPRESSED_SIZE);
            }
        }
        return size;
    }

    public long getCompressedSize(long entryHandle) {
        long size = getUnsignedInt(bufs, offset + entryHandle + CDE_COMPRESSED_SIZE);
        if (size == 0xffffffff) {
            long zip64 = getExtraRecord(entryHandle, EXT_ID_ZIP64);
            if (zip64 != -1) {
                size = getLong(bufs, offset + zip64 + ZIP64_COMPRESSED_SIZE);
            }
        }
        return size;
    }

    public long getModifiedTime(long entryHandle) {
        long unix = getExtraRecord(entryHandle, EXT_ID_UNIX);
        if (unix != -1) {
            long unixTime = getUnsignedInt(bufs, offset + unix + UNIX_MODIFIED_TIME);
            if (unixTime != 0) {
                return unixTime * 1000;
            }
        }
        // todo: NTFS (0x000a) sub-tag ID 0x0001 contains an 8-byte mtime
        return dosTimeStamp(getUnsignedShort(bufs, offset + entryHandle + CDE_MOD_TIME), getUnsignedShort(bufs, offset + entryHandle + CDE_MOD_DATE));
    }

    public void close() {

    }

    private static long dosTimeStamp(int modTime, int modDate) {
        int year = 1980 + (modDate >> 9);
        int month = 1 + ((modDate >> 5) & 0b1111);
        int day = modDate & 0b11111;
        int hour = modTime >> 11;
        int minute = (modTime >> 5) & 0b111111;
        int second = (modTime & 0b11111) << 1;
        return LocalDateTime.of(year, month, day, hour, minute, second).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public boolean isDirectory(final long entryHandle) {
        final int madeBy = getUnsignedShort(bufs, offset + entryHandle + CDE_VERSION_MADE_BY);
        final int extAttr = getInt(bufs, entryHandle + CDE_EXTERNAL_ATTRIBUTES);
        switch (madeBy) {
            case MADE_BY_UNIX: {
                //noinspection OctalInteger
                return (extAttr & 0170000) == 0040000;
            }
            default: {
                return (extAttr & 0b10000) != 0;
            }
        }
    }

    /**
     * Only loaded if CP-437 zip entries exist, which is unlikely but allowed.
     */
    static final class Cp437 {

        static final char[] codePoints = {
            '\0','☺', '☻', '♥', '♦', '♣', '♠', '•', '◘', '○', '◙', '♂', '♀', '♪', '♫', '☼',
            '►', '◄', '↕', '‼', '¶', '§', '▬', '↨', '↑', '↓', '→', '←', '∟', '↔', '▲', '▼',
            ' ', '!', '"', '#', '$', '%', '&', '\'','(', ')', '*', '+', ',', '-', '.', '/',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?',
            '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\',']', '^', '_',
            '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '⌂',
            'Ç', 'ü', 'é', 'â', 'ä', 'à', 'å', 'ç', 'ê', 'ë', 'è', 'ï', 'î', 'ì', 'Ä', 'Å',
            'É', 'æ', 'Æ', 'ô', 'ö', 'ò', 'û', 'ù', 'ÿ', 'Ö', 'Ü', '¢', '£', '¥', '₧', 'ƒ',
            'á', 'í', 'ó', 'ú', 'ñ', 'Ñ', 'ª', 'º', '¿', '⌐', '¬', '½', '¼', '¡', '«', '»',
            '░', '▒', '▓', '│', '┤', '╡', '╢', '╖', '╕', '╣', '║', '╗', '╝', '╜', '╛', '┐',
            '└', '┴', '┬', '├', '─', '┼', '╞', '╟', '╚', '╔', '╩', '╦', '╠', '═', '╬', '╧',
            '╨', '╤', '╥', '╙', '╘', '╒', '╓', '╫', '╪', '┘', '┌', '█', '▄', '▌', '▐', '▀',
            'α', 'ß', 'Γ', 'π', 'Σ', 'σ', 'µ', 'τ', 'Φ', 'Θ', 'Ω', 'δ', '∞', 'φ', 'ε', '∩',
            '≡', '±', '≥', '≤', '⌠', '⌡', '÷', '≈', '°', '∙', '·', '√', 'ⁿ', '²', '■', 0xA0
        };

        private Cp437() {}

        static char charFor(int c) {
            return codePoints[c];
        }
    }

    static final class Utf8 {
        private Utf8() {}

        static int getByteCount(final int a) {
            if (a <= 0b0111_1111) {
                return 1;
            } else if (a <= 0b1011_1111) {
                // invalid garbage (1 byte)
                return 1;
            } else if (a <= 0b1101_1111) {
                return 2;
            } else if (a <= 0b1110_1111) {
                return 3;
            } else if (a <= 0b1111_0111) {
                return 4;
            } else {
                // invalid garbage (1 byte)
                return 1;
            }
        }

        static int codePointAt(final ByteBuffer[] bufs, final long i) {
            final int a = getUnsignedByte(bufs, i);
            if (a <= 0b0111_1111) {
                return a;
            } else if (a <= 0b1011_1111) {
                // invalid garbage (1 byte)
                return '�';
            }
            int b = getUnsignedByte(bufs, i + 1);
            if ((b & 0b11_000000) != 0b10_000000) {
                // second byte is invalid; return � instead
                return '�';
            }
            if (a <= 0b1101_1111) {
                // two bytes
                return (a & 0b000_11111) << 6 | b & 0b00_111111;
            }
            int c = getUnsignedByte(bufs, i + 2);
            if ((c & 0b11_000000) != 0b10_000000) {
                // third byte is invalid; return � instead
                return '�';
            }
            if (a <= 0b1110_1111) {
                // three bytes
                return (a & 0b0000_1111) << 12 | (b & 0b00_111111) << 6 | c & 0b00_111111;
            }
            int d = getUnsignedByte(bufs, i + 3);
            if ((d & 0b11_000000) != 0b10_000000) {
                // fourth byte is invalid; return � instead
                return '�';
            }
            if (a <= 0b1111_0111) {
                // four bytes
                return (a & 0b00000_111) << 18 | (b & 0b00_111111) << 12 | (c & 0b00_111111) << 6 | d & 0b00_111111;
            }
            // invalid garbage (1 byte)
            return '�';
        }
    }

    static int bufIdx(long idx) {
        return (int) (idx >>> BUF_SHIFT);
    }

    static int bufOffs(long idx) {
        return ((int)idx) & BUF_SIZE_MASK;
    }

    static byte getByte(ByteBuffer[] bufs, long idx) {
        return bufs[bufIdx(idx)].get(bufOffs(idx));
    }

    static int getUnsignedByte(ByteBuffer[] bufs, long idx) {
        return getByte(bufs, idx) & 0xff;
    }

    static int getUnsignedByte(ByteBuffer buf, int idx) {
        return buf.get(idx) & 0xff;
    }

    static short getShort(ByteBuffer[] bufs, long idx) {
        final int bi = bufIdx(idx);
        return bi == bufIdx(idx + 1) ? bufs[bi].getShort(bufOffs(idx)) : (short) (getUnsignedByte(bufs, idx) | getByte(bufs, idx + 1) << 8);
    }

    static int getUnsignedShort(ByteBuffer[] bufs, long idx) {
        return getShort(bufs, idx) & 0xffff;
    }

    static int getMedium(ByteBuffer[] bufs, long idx) {
        return getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1) << 8;
    }

    static long getUnsignedMedium(ByteBuffer[] bufs, long idx) {
        return getUnsignedByte(bufs, idx) | getUnsignedShort(bufs, idx + 1) << 8;
    }

    static int getInt(ByteBuffer[] bufs, long idx) {
        final int bi = bufIdx(idx);
        return bi == bufIdx(idx + 3) ? bufs[bi].getInt(bufOffs(idx)) : getUnsignedShort(bufs, idx) | getShort(bufs, idx + 2) << 16;
    }

    static long getUnsignedInt(ByteBuffer[] bufs, long idx) {
        return getInt(bufs, idx) & 0xffff_ffffL;
    }

    static long getLong(ByteBuffer[] bufs, long idx) {
        final int bi = bufIdx(idx);
        return bi == bufIdx(idx + 7) ? bufs[bi].getLong(bufOffs(idx)) : getUnsignedInt(bufs, idx) | (long)getInt(bufs, idx + 4) << 32;
    }

    static void readBytes(ByteBuffer[] bufs, long idx, byte[] dest, int off, int len) {
        while (len > 0) {
            final int bi = bufIdx(idx);
            final int bo = bufOffs(idx);
            ByteBuffer buf = bufs[bi].duplicate();
            buf.position(bo);
            final int cnt = min(len, buf.remaining());
            buf.get(dest, 0, cnt);
            len -= cnt;
            off += cnt;
            idx += cnt;
        }
    }

    static byte[] getBytes(ByteBuffer[] bufs, long idx, int len) {
        final byte[] bytes = new byte[len];
        readBytes(bufs, idx, bytes, 0, len);
        return bytes;
    }

    private static final ByteBuffer EMPTY_BUF = ByteBuffer.allocateDirect(0);

    static ByteBuffer sliceOf(ByteBuffer[] bufs, long idx, int len) {
        if (len == 0) return EMPTY_BUF;
        final int biStart = bufIdx(idx);
        final int biEnd = bufIdx(idx + len - 1);
        if (biStart == biEnd) {
            final ByteBuffer buf = bufs[biStart].duplicate();
            buf.position(bufOffs(idx));
            buf.limit(buf.position() + len);
            return buf.slice();
        } else {
            return null;
        }
    }

    static ByteBuffer bufferOf(ByteBuffer[] bufs, long idx, int len) {
        ByteBuffer buf = sliceOf(bufs, idx, len);
        if (buf == null) {
            buf = ByteBuffer.wrap(getBytes(bufs, idx, len));
        }
        return buf;
    }

    static long capacity(ByteBuffer[] bufs) {
        final int lastIdx = bufs.length - 1;
        return ((long) lastIdx) * BUF_SIZE_MAX + bufs[lastIdx].capacity();
    }
}
