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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

/**
 * A remote exception cause.  Instances of this class are intended to aid with diagnostics and are not intended to be
 * directly thrown.  They may be added to other exception types as a cause or suppressed throwable.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class RemoteExceptionCause extends Throwable {
    private static final long serialVersionUID = 7849011228540958997L;

    private static final ClassValue<Function<Throwable, Map<String, String>>> fieldGetterValue = new ClassValue<Function<Throwable, Map<String, String>>>() {
        protected Function<Throwable, Map<String, String>> computeValue(final Class<?> type) {
            final Field[] fields = type.getFields();
            final int length = fields.length;
            int i, j;
            for (i = 0, j = 0; i < length; i ++) {
                if ((fields[i].getModifiers() & (Modifier.STATIC | Modifier.PUBLIC)) == Modifier.PUBLIC) {
                    fields[j ++] = fields[i];
                }
            }
            final int finalLength = j;
            final Field[] finalFields;
            if (j < i) {
                finalFields = Arrays.copyOf(fields, j);
            } else {
                finalFields = fields;
            }
            if (finalLength == 0) {
                return t -> Collections.emptyMap();
            } else if (finalLength == 1) {
                final Field field = finalFields[0];
                return t -> {
                    try {
                        return Collections.singletonMap(field.getName(), String.valueOf(field.get(t)));
                    } catch (IllegalAccessException e) {
                        // impossible
                        throw new IllegalStateException(e);
                    }
                };
            }
            return t -> {
                Map<String, String> map = new TreeMap<>();
                for (Field field : finalFields) {
                    try {
                        map.put(field.getName(), String.valueOf(field.get(t)));
                    } catch (IllegalAccessException e) {
                        // impossible
                        throw new IllegalStateException(e);
                    }
                }
                return Collections.unmodifiableMap(map);
            };
        }
    };
    private static final StackTraceElement[] EMPTY_STACK = new StackTraceElement[0];

    private final String exceptionClassName;
    private final Map<String, String> fields;
    private transient String toString;

    RemoteExceptionCause(final String msg, final RemoteExceptionCause cause, final String exceptionClassName, final Map<String, String> fields, boolean cloneFields) {
        super(msg);
        if (cause != null) {
            initCause(cause);
        }
        Assert.checkNotNullParam("exceptionClassName", exceptionClassName);
        this.exceptionClassName = exceptionClassName;
        if (cloneFields) {
            final Iterator<Map.Entry<String, String>> iterator = fields.entrySet().iterator();
            if (! iterator.hasNext()) {
                this.fields = Collections.emptyMap();
            } else {
                final Map.Entry<String, String> e1 = iterator.next();
                final String name1 = e1.getKey();
                final String value1 = e1.getValue();
                if (name1 == null || value1 == null) {
                    throw CommonMessages.msg.cannotContainNullFieldNameOrValue();
                }
                if (! iterator.hasNext()) {
                    this.fields = Collections.singletonMap(name1, value1);
                } else {
                    Map<String, String> map = new TreeMap<>();
                    map.put(name1, value1);
                    do {
                        final Map.Entry<String, String> next = iterator.next();
                        map.put(next.getKey(), next.getValue());
                    } while (iterator.hasNext());
                    this.fields = Collections.unmodifiableMap(map);
                }
            }
        } else {
            this.fields = fields;
        }
    }

    /**
     * Constructs a new {@code RemoteExceptionCause} instance with an initial message.  No
     * cause is specified.
     *
     * @param msg the message
     * @param exceptionClassName the name of the exception's class (must not be {@code null})
     */
    public RemoteExceptionCause(final String msg, final String exceptionClassName) {
        this(msg, null, exceptionClassName, Collections.emptyMap(), false);
    }

    /**
     * Constructs a new {@code RemoteExceptionCause} instance with an initial message and cause.
     *
     * @param msg the message
     * @param cause the cause
     * @param exceptionClassName the name of the exception's class (must not be {@code null})
     */
    public RemoteExceptionCause(final String msg, final RemoteExceptionCause cause, final String exceptionClassName) {
        this(msg, cause, exceptionClassName, Collections.emptyMap(), false);
    }

    /**
     * Constructs a new {@code RemoteExceptionCause} instance with an initial message.  No
     * cause is specified.
     *
     * @param msg the message
     * @param exceptionClassName the name of the exception's class (must not be {@code null})
     * @param fields the public fields of the remote exception (must not be {@code null})
     */
    public RemoteExceptionCause(final String msg, final String exceptionClassName, final Map<String, String> fields) {
        this(msg, null, exceptionClassName, fields, true);
    }

    /**
     * Constructs a new {@code RemoteExceptionCause} instance with an initial message and cause.
     *
     * @param msg the message
     * @param cause the cause
     * @param exceptionClassName the name of the exception's class (must not be {@code null})
     * @param fields the public fields of the remote exception (must not be {@code null})
     */
    public RemoteExceptionCause(final String msg, final RemoteExceptionCause cause, final String exceptionClassName, final Map<String, String> fields) {
        this(msg, cause, exceptionClassName, fields, true);
    }

    /**
     * Get a remote exception cause for the given {@link Throwable}.  All of the cause and suppressed exceptions will
     * also be converted.
     *
     * @param t the throwable, or {@code null}
     * @return the remote exception cause, or {@code null} if {@code null} was passed in
     */
    public static RemoteExceptionCause of(Throwable t) {
        return of(t, new IdentityHashMap<>());
    }

    private static RemoteExceptionCause of(Throwable t, IdentityHashMap<Throwable, RemoteExceptionCause> seen) {
        if (t == null) return null;
        if (t instanceof RemoteExceptionCause) {
            return (RemoteExceptionCause) t;
        } else {
            final RemoteExceptionCause existing = seen.get(t);
            if (existing != null) {
                return existing;
            }
            final RemoteExceptionCause e = new RemoteExceptionCause(t.getMessage(), t.getClass().getName(), fieldGetterValue.get(t.getClass()).apply(t));
            e.setStackTrace(t.getStackTrace());
            seen.put(t, e);
            final Throwable cause = t.getCause();
            if (cause != null) e.initCause(of(cause, seen));
            for (Throwable throwable : t.getSuppressed()) {
                e.addSuppressed(of(throwable, seen));
            }
            return e;
        }
    }

    /**
     * Convert this remote exception cause to a plain throwable for sending to peers which use serialization and do not
     * have this class present.  Note that this does not recursively apply; normally, a serialization framework will
     * handle the recursive application of this operation through object resolution.
     *
     * @return the throwable (not {@code null})
     */
    public Throwable toPlainThrowable() {
        final Throwable throwable = new Throwable(toString(), getCause());
        throwable.setStackTrace(getStackTrace());
        for (Throwable s : getSuppressed()) {
            throwable.addSuppressed(s);
        }
        return throwable;
    }

    /**
     * Get the original exception class name.
     *
     * @return the original exception class name (not {@code null})
     */
    public String getExceptionClassName() {
        return exceptionClassName;
    }

    /**
     * Get the field names of the remote exception.
     *
     * @return the field names of the remote exception
     */
    public Set<String> getFieldNames() {
        return fields.keySet();
    }

    /**
     * Get the string value of the given field name.
     *
     * @param fieldName the name of the field (must not be {@code null})
     * @return the string value of the given field name
     */
    public String getFieldValue(String fieldName) {
        Assert.checkNotNullParam("fieldName", fieldName);
        return fields.get(fieldName);
    }

    /**
     * Get a string representation of this exception.  The representation will return an indication of the fact that
     * this was a remote exception, the remote exception type, and optionally details of the exception content, followed
     * by the exception message.
     *
     * @return the string representation of the exception
     */
    public String toString() {
        final String toString = this.toString;
        if (toString == null) {
            final String message = getMessage();
            StringBuilder b = new StringBuilder();
            b.append(message == null ? CommonMessages.msg.remoteException(exceptionClassName) : CommonMessages.msg.remoteException(exceptionClassName, message));
            Iterator<Map.Entry<String, String>> iterator = fields.entrySet().iterator();
            if (iterator.hasNext()) {
                b.append("\n\tPublic fields:");
                do {
                    final Map.Entry<String, String> entry = iterator.next();
                    b.append('\n').append('\t').append('\t').append(entry.getKey()).append('=').append(entry.getValue());
                } while (iterator.hasNext());
            }
            return this.toString = b.toString();
        }
        return toString;
    }

    // Format:
    //   class name
    //   null | message
    //   stack trace
    //   count ( field-name field-value )*
    //   null | caused-by
    //   count suppressed*
    // Add new data to the end; old versions must ignore extra data

    private static final int ST_NULL = 0;
    private static final int ST_NEW_STRING = 1; // utf8 data follows
    private static final int ST_NEW_STACK_ELEMENT_V8 = 2; // string string string int
    private static final int ST_NEW_STACK_ELEMENT_V9 = 3; // string string string string string string int
    private static final int ST_NEW_EXCEPTION_CAUSE = 4; // recurse
    private static final int ST_INT8 = 5; // one byte
    private static final int ST_INT16 = 6; // two bytes
    private static final int ST_INT32 = 7; // four bytes
    private static final int ST_INT_MINI = 0x20; // low 5 bits == signed value
    private static final int ST_BACKREF_FAR = 0x40; // low 6 bits + next byte are distance
    private static final int ST_BACKREF_NEAR = 0x80; // low 7 bits are distance

    /**
     * Write this remote exception cause to the given stream, without using serialization.
     *
     * @param output the output stream (must not be {@code null})
     * @throws IOException if an error occurs writing the data
     */
    public void writeToStream(DataOutput output) throws IOException {
        Assert.checkNotNullParam("output", output);
        writeToStream(output, new IdentityIntMap<Object>(), new HashMap<String,String>(), 0);
    }

    private static int readPackedInt(DataInput is) throws IOException {
        final int b = is.readUnsignedByte();
        if ((b & 0xE0) == ST_INT_MINI) {
            // sign-extend it
            return b << 27 >> 27;
        } else if (b == ST_INT8) {
            return is.readByte();
        } else if (b == ST_INT16) {
            return is.readShort();
        } else if (b == ST_INT32) {
            return is.readInt();
        } else {
            throw CommonMessages.msg.corruptedStream();
        }
    }

    private static void writePackedInt(DataOutput os, int val) throws IOException {
        if (-0x10 <= val && val < 0x10) {
            os.write(ST_INT_MINI | val & 0b01_1111);
        } else if (-0x80 <= val && val < 0x80) {
            os.write(ST_INT8);
            os.write(val);
        } else if (-0x8000 <= val && val < 0x8000) {
            os.write(ST_INT16);
            os.writeShort(val);
        } else {
            os.write(ST_INT32);
            os.writeInt(val);
        }
    }

    private int writeToStream(DataOutput output, IdentityIntMap<Object> seen, HashMap<String, String> stringCache, int cnt) throws IOException {
        // register in cycle map
        seen.put(this, cnt++);
        // write the header byte
        output.writeByte(ST_NEW_EXCEPTION_CAUSE);
        // first write class name
        cnt = writeString(output, exceptionClassName, seen, stringCache, cnt);
        // null or message
        cnt = writeString(output, getMessage(), seen, stringCache, cnt);
        // stack trace
        cnt = writeStackTrace(output, getStackTrace(), seen, stringCache, cnt);
        // fields
        cnt = writeFields(output, fields, seen, stringCache, cnt);
        // caused-by
        cnt = writeThrowable(output, getCause(), seen, stringCache, cnt);
        // suppressed
        final Throwable[] suppressed = getSuppressed();
        writePackedInt(output, suppressed.length);
        for (final Throwable t : suppressed) {
            cnt = writeThrowable(output, t, seen, stringCache, cnt);
        }
        return cnt;
    }

    private int writeFields(final DataOutput output, final Map<String, String> fields, final IdentityIntMap<Object> seen, final HashMap<String, String> stringCache, int cnt) throws IOException {
        writePackedInt(output, fields.size());
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            cnt = writeString(output, entry.getKey(), seen, stringCache, cnt);
            cnt = writeString(output, entry.getValue(), seen, stringCache, cnt);
        }
        return cnt;
    }

    private int writeStackTrace(final DataOutput output, final StackTraceElement[] stackTrace, final IdentityIntMap<Object> seen, final HashMap<String, String> stringCache, int cnt) throws IOException {
        // don't bother recording in seen because stack traces are always copied
        final int length = stackTrace.length;
        writePackedInt(output, length);
        for (StackTraceElement element : stackTrace) {
            cnt = writeStackElement(output, element, seen, stringCache, cnt);
        }
        return cnt;
    }

    private int writeStackElement(final DataOutput output, final StackTraceElement element, final IdentityIntMap<Object> seen, final HashMap<String, String> stringCache, int cnt) throws IOException {
        final int idx = seen.get(element, - 1);
        final int distance = cnt - idx;
        if (idx == -1 || distance > (1 << 14) - 1) {
            output.write(ST_NEW_STACK_ELEMENT_V8);
            cnt = writeString(output, element.getClassName(), seen, stringCache, cnt);
            cnt = writeString(output, element.getMethodName(), seen, stringCache, cnt);
            cnt = writeString(output, element.getFileName(), seen, stringCache, cnt);
            writePackedInt(output, element.getLineNumber());
            seen.put(element, cnt++);
            return cnt;
        } else {
            if (distance < 127) {
                output.writeByte(ST_BACKREF_NEAR | distance);
            } else {
                assert distance <= 0x3fff;
                output.writeByte(ST_BACKREF_FAR | distance >> 8);
                output.writeByte(distance);
            }
            return cnt;
        }
    }

    private int writeThrowable(final DataOutput output, final Throwable throwable, final IdentityIntMap<Object> seen, final HashMap<String, String> stringCache, final int cnt) throws IOException {
        if (throwable == null) {
            output.write(ST_NULL);
            return cnt;
        } else {
            final int idx = seen.get(throwable, - 1);
            final int distance = cnt - idx;
            if (idx == - 1 || distance >= 0x4000) {
                RemoteExceptionCause nested;
                if (throwable instanceof RemoteExceptionCause) {
                    nested = (RemoteExceptionCause) throwable;
                } else {
                    seen.put(throwable, cnt); // do not increment yet
                    nested = of(throwable);
                }
                return nested.writeToStream(output, seen, stringCache, cnt); // this will increment it
            } else {
                if (distance < 127) {
                    output.writeByte(ST_BACKREF_NEAR | distance);
                } else {
                    assert distance <= 0x3fff;
                    output.writeByte(ST_BACKREF_FAR | distance >> 8);
                    output.writeByte(distance);
                }
                return cnt;
            }
        }
    }

    private int writeString(final DataOutput output, String string, final IdentityIntMap<Object> seen, final HashMap<String, String> stringCache, final int cnt) throws IOException {
        if (string == null) {
            output.write(ST_NULL);
            return cnt;
        }
        // make sure we never duplicate a string
        string = stringCache.computeIfAbsent(string, Function.identity());
        final int idx = seen.get(string, - 1);
        final int distance = cnt - idx;
        if (idx == -1 || distance > (1 << 14) - 1) {
            seen.put(string, cnt);
            output.write(ST_NEW_STRING);
            output.writeUTF(string);
            return cnt + 1;
        } else {
            if (distance < 127) {
                output.writeByte(ST_BACKREF_NEAR | distance);
            } else {
                assert distance <= 0x3fff;
                output.writeByte(ST_BACKREF_FAR | distance >> 8);
                output.writeByte(distance);
            }
            return cnt;
        }
    }

    public static RemoteExceptionCause readFromStream(DataInput input) throws IOException {
        return readObject(input, RemoteExceptionCause.class, new ArrayList<>(), false);
    }

    private static <T> T readObject(DataInput input, Class<T> expect, ArrayList<Object> cache, final boolean allowNull) throws IOException {
        final int b = input.readUnsignedByte();
        if (b == ST_NULL) {
            if (! allowNull) {
                throw CommonMessages.msg.corruptedStream();
            }
            return null;
        } else if (b == ST_NEW_STRING) {
            if (expect != String.class) {
                throw CommonMessages.msg.corruptedStream();
            }
            final String str = input.readUTF();
            cache.add(str);
            return expect.cast(str);
        } else if (b == ST_NEW_EXCEPTION_CAUSE) {
            if (expect != RemoteExceptionCause.class) {
                throw CommonMessages.msg.corruptedStream();
            }
            final int idx = cache.size();
            cache.add(null);
            String exClassName = readObject(input, String.class, cache, false);
            String exMessage = readObject(input, String.class, cache, true);
            int length = readPackedInt(input);
            StackTraceElement[] stackTrace;
            if (length == 0) {
                stackTrace = EMPTY_STACK;
            } else {
                stackTrace = new StackTraceElement[length];
                for (int i = 0; i < length; i++) {
                    stackTrace[i] = readObject(input, StackTraceElement.class, cache, false);
                }
            }
            Map<String, String> fields;
            length = readPackedInt(input);
            if (length == 0) {
                fields = Collections.emptyMap();
            } else if (length == 1) {
                fields = Collections.singletonMap(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false));
            } else {
                fields = new HashMap<>(length);
                for (int i = 0; i < length; i++) {
                    fields.put(readObject(input, String.class, cache, false), readObject(input, String.class, cache, false));
                }
            }
            final RemoteExceptionCause result = new RemoteExceptionCause(exMessage, null, exClassName, fields, false);
            cache.set(idx, result);
            RemoteExceptionCause causedBy = readObject(input, RemoteExceptionCause.class, cache, true);
            result.initCause(causedBy);
            length = readPackedInt(input);
            result.setStackTrace(stackTrace);
            for (int i = 0; i < length; i++) {
                // this can't actually be null because we passed {@code false} in to allowNull
                //noinspection ConstantConditions
                result.addSuppressed(readObject(input, RemoteExceptionCause.class, cache, false));
            }
            return expect.cast(result);
        } else if (b == ST_NEW_STACK_ELEMENT_V8) {
            if (expect != StackTraceElement.class) {
                throw CommonMessages.msg.corruptedStream();
            }
            // this can't actually be null because we passed {@code false} in to allowNull
            //noinspection ConstantConditions
            final StackTraceElement element = new StackTraceElement(
                readObject(input, String.class, cache, false),
                readObject(input, String.class, cache, false),
                readObject(input, String.class, cache, true),
                readPackedInt(input)
            );
            cache.add(element);
            return expect.cast(element);
        } else if (b == ST_NEW_STACK_ELEMENT_V9) {
            if (expect != StackTraceElement.class) {
                throw CommonMessages.msg.corruptedStream();
            }
            // discard CL name, module name, and module version
            readObject(input, String.class, cache, true);
            readObject(input, String.class, cache, true);
            readObject(input, String.class, cache, true);
            // this can't actually be null because we passed {@code false} in to allowNull
            //noinspection ConstantConditions
            final StackTraceElement element = new StackTraceElement(
                readObject(input, String.class, cache, false),
                readObject(input, String.class, cache, false),
                readObject(input, String.class, cache, true),
                readPackedInt(input)
            );
            cache.add(element);
            return expect.cast(element);
        } else if ((b & ST_BACKREF_NEAR) != 0) {
            int idx = b & 0x7f;
            if (idx > cache.size()) {
                throw CommonMessages.msg.corruptedStream();
            }
            Object obj = cache.get(cache.size() - idx);
            if (expect.isInstance(obj)) {
                return expect.cast(obj);
            } else {
                throw CommonMessages.msg.corruptedStream();
            }
        } else if ((b & ST_BACKREF_FAR) != 0) {
            final int b2 = input.readUnsignedByte();
            int idx = (b & 0x3f) << 8 | b2;
            if (idx > cache.size()) {
                throw CommonMessages.msg.corruptedStream();
            }
            Object obj = cache.get(cache.size() - idx);
            if (expect.isInstance(obj)) {
                return expect.cast(obj);
            } else {
                throw CommonMessages.msg.corruptedStream();
            }
        } else {
            throw CommonMessages.msg.corruptedStream();
        }
    }

    private static final String[] NO_STRINGS = new String[0];
    private static final RemoteExceptionCause[] NO_REMOTE_EXCEPTION_CAUSES = new RemoteExceptionCause[0];

    Object writeReplace() {
        final Throwable[] origSuppressed = getSuppressed();
        final int length = origSuppressed.length;
        final RemoteExceptionCause[] suppressed;
        if (length == 0) {
            suppressed = NO_REMOTE_EXCEPTION_CAUSES;
        } else {
            suppressed = new RemoteExceptionCause[length];
            for (int i = 0; i < length; i ++) {
                suppressed[i] = of(origSuppressed[i]);
            }
        }
        String[] fieldArray;
        final int size = fields.size();
        if (size == 0) {
            fieldArray = NO_STRINGS;
        } else {
            fieldArray = new String[size << 1];
            int i = 0;
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                fieldArray[i++] = entry.getKey();
                fieldArray[i++] = entry.getValue();
            }
        }
        return new Serialized(getMessage(), exceptionClassName, of(getCause()), suppressed, getStackTrace(), fieldArray);
    }

    public RemoteExceptionCause getCause() {
        return (RemoteExceptionCause) super.getCause();
    }

    static final class Serialized implements Serializable {
        private static final long serialVersionUID = - 2201431870774913071L;

        // small field names serialize smaller

        final String m;
        final String cn;
        final RemoteExceptionCause c;
        final RemoteExceptionCause[] s;
        final StackTraceElement[] st;
        final String[] f;

        Serialized(final String m, final String cn, final RemoteExceptionCause c, final RemoteExceptionCause[] s, final StackTraceElement[] st, final String[] f) {
            this.m = m;
            this.cn = cn;
            this.c = c;
            this.s = s;
            this.st = st;
            this.f = f;
        }

        Object readResolve() {
            final Map<String, String> fields;
            if (f == null) {
                fields = Collections.emptyMap();
            } else {
                final int fl = f.length;
                if ((fl & 1) != 0) {
                    throw CommonMessages.msg.invalidOddFields();
                } else if (fl == 0) {
                    fields = Collections.emptyMap();
                } else if (fl == 2) {
                    fields = Collections.singletonMap(f[0], f[1]);
                } else {
                    final TreeMap<String, String> map = new TreeMap<>();
                    for (int i = 0; i < fl; i += 2) {
                        map.put(f[i], f[i + 1]);
                    }
                    fields = Collections.unmodifiableMap(map);
                }
            }
            final RemoteExceptionCause ex = new RemoteExceptionCause(m, c, cn, fields, false);
            ex.setStackTrace(st);
            final RemoteExceptionCause[] suppressed = s;
            if (suppressed != null) for (RemoteExceptionCause c : suppressed) {
                ex.addSuppressed(c);
            }
            return ex;
        }
    }
}
