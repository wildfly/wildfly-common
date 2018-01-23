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

package org.wildfly.common.rpc;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.sql.SQLException;
import java.util.function.Supplier;

import javax.transaction.xa.XAException;

import org.junit.Test;
import org.wildfly.common.function.ExceptionBiConsumer;

public class TestRemoteExceptionCause {

    @Test
    public void className() throws Exception {
        Throwable t = new XAException(XAException.XAER_RMERR);
        RemoteExceptionCause cause = RemoteExceptionCause.of(t);
        assertEquals(XAException.class.getName(), cause.getExceptionClassName());
        assertEquals(TestRemoteExceptionCause.class.getName(), cause.getStackTrace()[0].getClassName());
        assertNull(cause.getMessage());
    }

    @Test
    public void message() throws Exception {
        final String message = "This one has a message";
        Throwable t = new NullPointerException(message);
        RemoteExceptionCause cause = RemoteExceptionCause.of(t);
        assertEquals(message, cause.getMessage());
        assertTrue(cause.toString().endsWith(": " + message));
        t = new IllegalStateException();
        cause = RemoteExceptionCause.of(t);
        assertNull(cause.getMessage());
        assertFalse(cause.toString().contains(":"));
    }

    @Test
    public void fields() throws Exception {
        Throwable t = new XAException(XAException.XAER_RMERR);
        RemoteExceptionCause cause = RemoteExceptionCause.of(t);
        assertEquals(1, cause.getFieldNames().size());
        assertTrue(cause.getFieldNames().contains("errorCode"));
        assertEquals("-3", cause.getFieldValue("errorCode"));

        t = new IllegalArgumentException("foo bar baz");
        cause = RemoteExceptionCause.of(t);
        assertEquals(0, cause.getFieldNames().size());

        t = new SQLException("reason", "bad state", 12345);
        cause = RemoteExceptionCause.of(t);
        assertEquals(0, cause.getFieldNames().size());
    }

    @Test
    public void cause() throws Exception {
        Throwable t = new IllegalStateException("outer", new NullPointerException("inner"));
        RemoteExceptionCause cause = RemoteExceptionCause.of(t);
        assertEquals(IllegalStateException.class.getName(), cause.getExceptionClassName());
        assertEquals("outer", cause.getMessage());
        assertNotNull(cause.getCause());
        assertEquals(NullPointerException.class.getName(), cause.getCause().getExceptionClassName());
        assertEquals("inner", cause.getCause().getMessage());
        assertNull(cause.getCause().getCause());
    }

    @Test
    public void loop() throws Exception {
        Throwable t1 = new IllegalStateException();
        Throwable t2 = new NullPointerException();
        Throwable t3 = new IllegalArgumentException();
        t1.initCause(t2);
        t2.initCause(t3);
        t3.initCause(t1);
        RemoteExceptionCause cause = RemoteExceptionCause.of(t1);
        assertEquals(IllegalStateException.class.getName(), cause.getExceptionClassName());
        assertEquals(NullPointerException.class.getName(), cause.getCause().getExceptionClassName());
        assertEquals(IllegalArgumentException.class.getName(), cause.getCause().getCause().getExceptionClassName());
        assertSame(cause, cause.getCause().getCause().getCause());
    }

    private void doSerialTest(Supplier<Throwable> supplier, ExceptionBiConsumer<Throwable, RemoteExceptionCause, Exception> resultHandler) throws Exception {
        final Throwable throwable = supplier.get();
        RemoteExceptionCause cause = RemoteExceptionCause.of(throwable);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final DataOutputStream dos = new DataOutputStream(os);
        cause.writeToStream(dos);
        dos.flush();
        final ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        final DataInputStream dis = new DataInputStream(is);
        resultHandler.accept(throwable, RemoteExceptionCause.readFromStream(dis));
    }

    @Test
    public void serial0() throws Exception {
        doSerialTest(() -> new SQLException("reason", "sql-state", 12345), (t, c) -> {
            assertEquals(t.getClass().getName(), c.getExceptionClassName());
            assertStackTraceArrayEquals(t, c);
        });
    }

    @Test
    public void serial1() throws Exception {
        doSerialTest(() -> {
            Throwable t = new XAException(XAException.XAER_RMERR);
            t.initCause(new NullPointerException("Null pointer!"));
            return t;
        }, (t, c) -> {
            assertEquals(t.getClass().getName(), c.getExceptionClassName());
            assertEquals(t.getCause().getClass().getName(), c.getCause().getExceptionClassName());
            assertStackTraceArrayEquals(t, c);
            assertStackTraceArrayEquals(t.getCause(), c.getCause());
        });
    }

    @Test
    public void serial2() throws Exception {
        // cause loop
        doSerialTest(() -> {
            Throwable t1 = new IllegalStateException();
            Throwable t2 = new NullPointerException();
            Throwable t3 = new IllegalArgumentException();
            t1.initCause(t2);
            t2.initCause(t3);
            t3.initCause(t1);
            return t1;
        }, (t, c) -> {
            assertEquals(t.getClass().getName(), c.getExceptionClassName());
            assertEquals(t.getCause().getClass().getName(), c.getCause().getExceptionClassName());
            assertEquals(t.getCause().getCause().getClass().getName(), c.getCause().getCause().getExceptionClassName());
            assertSame(c.getCause().getCause().getCause(), c);
            assertStackTraceArrayEquals(t, c);
            assertStackTraceArrayEquals(t.getCause(), c.getCause());
        });
    }

    @Test
    public void serial3() throws Exception {
        // suppressed loop
        doSerialTest(() -> {
            Throwable t1 = new IllegalStateException();
            Throwable t2 = new NullPointerException();
            Throwable t3 = new IllegalArgumentException();
            t1.addSuppressed(t2);
            t2.addSuppressed(t3);
            t3.addSuppressed(t1);
            return t1;
        }, (t, c) -> {
            assertEquals(t.getClass().getName(), c.getExceptionClassName());
            assertEquals(t.getSuppressed()[0].getClass().getName(), ((RemoteExceptionCause) c.getSuppressed()[0]).getExceptionClassName());
            assertEquals(t.getSuppressed()[0].getSuppressed()[0].getClass().getName(), ((RemoteExceptionCause) c.getSuppressed()[0].getSuppressed()[0]).getExceptionClassName());
            assertSame(c.getSuppressed()[0].getSuppressed()[0].getSuppressed()[0], c);
            assertStackTraceArrayEquals(t, c);
        });
    }

    static void assertStackTraceArrayEquals(Throwable t1, Throwable t2) {
        if (t1 == t2) return;
        assertNotNull("t1", t1);
        assertNotNull("t2", t2);
        final StackTraceElement[] s1 = t1.getStackTrace();
        final StackTraceElement[] s2 = t2.getStackTrace();
        if (s1 == s2) return;
        final int length = s1.length;
        assertEquals("Stack trace lengths differ", length, s2.length);
        for (int i = 0; i < length; i ++) {
            assertEquals("Class name difference at index " + i, s1[i].getClassName(), s2[i].getClassName());
            assertEquals("Method name difference at index " + i, s1[i].getMethodName(), s2[i].getMethodName());
            assertEquals("File name difference at index " + i, s1[i].getFileName(), s2[i].getFileName());
            assertEquals("Line number difference at index " + i, s1[i].getLineNumber(), s2[i].getLineNumber());
        }
    }
}
