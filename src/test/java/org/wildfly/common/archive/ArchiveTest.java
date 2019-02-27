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

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

import org.junit.Test;

/**
 *
 */
public class ArchiveTest {

    public static final String SIMPLE_CONTENT_1 =
        "This is a string that is meant to be long but easy to compress, which can be read back in a single line.";
    private static final String TEST_CONTENT_NAME = "test/content/file.text";

    @Test
    public void testReadContentStored() throws IOException {
        doReadContent(ZipEntry.STORED);
    }

    @Test
    public void testReadContentDeflated() throws IOException {
        doReadContent(ZipEntry.DEFLATED);
    }

    private void doReadContent(int method) throws IOException {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().putValue(Attributes.Name.SPECIFICATION_TITLE.toString(), "Test JAR");
        final byte[] contentBytes;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
                try (BufferedWriter writer = new BufferedWriter(osw)) {
                    for (int i = 0; i < 50; i ++) {
                        writer.write(SIMPLE_CONTENT_1);
                        writer.newLine();
                    }
                }
            }
            contentBytes = baos.toByteArray();
        }

        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (JarOutputStream jos = new JarOutputStream(os, manifest)) {
            final JarEntry jarEntry = new JarEntry(TEST_CONTENT_NAME);
            jarEntry.setSize(contentBytes.length);
            if (method == ZipEntry.STORED) {
                jarEntry.setCompressedSize(contentBytes.length);
                final CRC32 crc32 = new CRC32();
                crc32.update(contentBytes);
                jarEntry.setCrc(crc32.getValue());
            }
            jarEntry.setMethod(method);
            jos.putNextEntry(jarEntry);
            jos.write(contentBytes);
            jos.closeEntry();
        }
        final byte[] bytes = os.toByteArray();
        final Archive archive = Archive.open(ByteBuffer.wrap(bytes));
        assertEquals(-1, archive.getEntryHandle("non/existent/file/"));
        assertEquals(-1, archive.getEntryHandle(""));
        final long handle = archive.getEntryHandle(TEST_CONTENT_NAME);
        assertNotEquals(-1, handle);
        assertEquals(contentBytes.length, archive.getUncompressedSize(handle));
        assertTrue(contentBytes.length >= archive.getCompressedSize(handle));
        final ByteBuffer contentsBuf = archive.getEntryContents(handle);
        assertNotNull(contentsBuf);
        // verify the buffer view
        final byte[] compareBytes = new byte[contentBytes.length];
        contentsBuf.get(compareBytes);
        assertEquals(0, contentsBuf.remaining());
        assertArrayEquals(contentBytes, compareBytes);
    }
}
