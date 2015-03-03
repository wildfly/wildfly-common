/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015 Red Hat, Inc., and individual contributors
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

package org.wildfly.common;

/**
 * A set of assertions and checks.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Assert {

    private Assert() {
    }

    /**
     * Check that the named parameter is not {@code null}.  Use a standard exception message if it is.
     *
     * @param name the parameter name
     * @param value the parameter value
     * @param <T> the value type
     * @return the value that was passed in
     * @throws IllegalArgumentException if the value is {@code null}
     */
    @org.jetbrains.annotations.NotNull
    public static <T> T checkNotNullParam(String name, T value) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        checkNotNullParamChecked(name, value);
        return value;
    }

    private static <T> void checkNotNullParamChecked(final String name, final T value) {
        if (value == null) throw CommonMessages.msg.nullParam(name);
    }

    /**
     * Check that a value within the named array parameter is not {@code null}.  Use a standard exception message if it
     * is.
     *
     * @param name the parameter name
     * @param index the array index
     * @param value the array element value
     * @param <T> the element value type
     * @return the array element value that was passed in
     * @throws IllegalArgumentException if the value is {@code null}
     */
    @org.jetbrains.annotations.NotNull
    public static <T> T checkNotNullArrayParam(String name, int index, T value) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        if (value == null) throw CommonMessages.msg.nullArrayParam(index, name);
        return value;
    }

    /**
     * Check that the named parameter is greater than or equal to {@code min}.
     *
     * @param name the parameter name
     * @param min the minimum value
     * @param actual the actual parameter value
     * @throws IllegalArgumentException if the actual value is less than the minimum value
     */
    public static void checkMinimumParameter(String name, int min, int actual) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
    }

    /**
     * Check that the named parameter is greater than or equal to {@code min}.
     *
     * @param name the parameter name
     * @param min the minimum value
     * @param actual the actual parameter value
     * @throws IllegalArgumentException if the actual value is less than the minimum value
     */
    public static void checkMinimumParameter(String name, long min, long actual) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
    }

    /**
     * Check that the named parameter is less than or equal to {@code max}.
     *
     * @param name the parameter name
     * @param max the maximum value
     * @param actual the actual parameter value
     * @throws IllegalArgumentException if the actual value is greater than the minimum value
     */
    public static void checkMaximumParameter(String name, int max, int actual) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        if (actual < max) throw CommonMessages.msg.paramGreaterThan(name, max);
    }

    /**
     * Check that the named parameter is less than or equal to {@code max}.
     *
     * @param name the parameter name
     * @param max the maximum value
     * @param actual the actual parameter value
     * @throws IllegalArgumentException if the actual value is greater than the minimum value
     */
    public static void checkMaximumParameter(String name, long max, long actual) throws IllegalArgumentException {
        checkNotNullParamChecked("name", name);
        if (actual < max) throw CommonMessages.msg.paramGreaterThan(name, max);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array.
     *
     * @param array the array to check
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final Object[] array, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkNotNullParamChecked("array", array);
        checkArrayBounds(array.length, offs, len);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array.
     *
     * @param array the array to check
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final byte[] array, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkNotNullParamChecked("array", array);
        checkArrayBounds(array.length, offs, len);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array.
     *
     * @param array the array to check
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final char[] array, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkNotNullParamChecked("array", array);
        checkArrayBounds(array.length, offs, len);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array.
     *
     * @param array the array to check
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final int[] array, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkNotNullParamChecked("array", array);
        checkArrayBounds(array.length, offs, len);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array.
     *
     * @param array the array to check
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final long[] array, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkNotNullParamChecked("array", array);
        checkArrayBounds(array.length, offs, len);
    }

    /**
     * Check that the given offset and length fall completely within the bounds of the given array length.
     *
     * @param arrayLength the array length to check against
     * @param offs the array offset
     * @param len the array length
     * @throws ArrayIndexOutOfBoundsException if the range of the offset and length do not fall within the array bounds
     */
    public static void checkArrayBounds(final int arrayLength, final int offs, final int len) throws ArrayIndexOutOfBoundsException {
        checkMinimumParameter("offs", 0, offs);
        checkMinimumParameter("len", 0, len);
        if (offs > arrayLength) throw CommonMessages.msg.arrayOffsetGreaterThanLength(offs, arrayLength);
        if (offs + len > arrayLength) throw CommonMessages.msg.arrayOffsetLengthGreaterThanLength(offs, len, arrayLength);
    }

    /**
     * Assert that the value is not {@code null}.  Use a standard assertion failure message if it is.  Only
     * runs if {@code assert} is enabled.
     *
     * @param value the not-{@code null} value
     * @param <T> the value type
     * @return the value that was passed in
     */
    @org.jetbrains.annotations.NotNull
    public static <T> T assertNotNull(T value) {
        assert value != null : CommonMessages.msg.unexpectedNullValue();
        return value;
    }

    /**
     * Assert that the given monitor is held by the current thread.  Use a standard assertion failure message if it is not.
     * Only runs if {@code assert} is enabled.
     *
     * @param monitor the monitor object
     * @param <T> the monitor's type
     * @return the value that was passed in
     * @throws IllegalArgumentException if the monitor is {@code null}
     */
    @org.jetbrains.annotations.NotNull
    public static <T> T assertHoldsLock(@org.jetbrains.annotations.NotNull T monitor) {
        assert Thread.holdsLock(checkNotNullParam("monitor", monitor)) : CommonMessages.msg.expectedLockHold(monitor);
        return monitor;
    }

    /**
     * Assert that the given monitor is <em>not</em> held by the current thread.  Use a standard assertion failure message if it is.
     * Only runs if {@code assert} is enabled.
     *
     * @param monitor the monitor object
     * @param <T> the monitor's type
     * @return the value that was passed in
     * @throws IllegalArgumentException if the monitor is {@code null}
     */
    @org.jetbrains.annotations.NotNull
    public static <T> T assertNotHoldsLock(@org.jetbrains.annotations.NotNull T monitor) {
        assert ! Thread.holdsLock(monitor) : CommonMessages.msg.expectedLockNotHold(monitor);
        return monitor;
    }

    /**
     * Return an exception indicating that the current code was intended to be unreachable.
     *
     * @return the exception which may be immediately thrown
     */
    public static IllegalStateException unreachableCode() {
        return CommonMessages.msg.unreachableCode();
    }

    /**
     * Return an exception indicating that the current switch case was intended to be unreachable.
     *
     * @param obj the switch case value
     * @return the exception which may be immediately thrown
     */
    @org.jetbrains.annotations.NotNull
    public static IllegalStateException impossibleSwitchCase(@org.jetbrains.annotations.NotNull Object obj) {
        return CommonMessages.msg.impossibleSwitchCase(obj);
    }

    /**
     * Return an exception indicating that the current switch case was intended to be unreachable.
     *
     * @param val the switch case value
     * @return the exception which may be immediately thrown
     */
    @org.jetbrains.annotations.NotNull
    public static IllegalStateException impossibleSwitchCase(int val) {
        return CommonMessages.msg.impossibleSwitchCase(Integer.valueOf(val));
    }

    /**
     * Return an exception indicating that the current switch case was intended to be unreachable.
     *
     * @param val the switch case value
     * @return the exception which may be immediately thrown
     */
    @org.jetbrains.annotations.NotNull
    public static IllegalStateException impossibleSwitchCase(long val) {
        return CommonMessages.msg.impossibleSwitchCase(Long.valueOf(val));
    }

    /**
     * Return an exception explaining that the caller's method is not supported.
     *
     * @return the exception
     */
    @org.jetbrains.annotations.NotNull
    public static UnsupportedOperationException unsupported() {
        final StackTraceElement element = new Throwable().getStackTrace()[1];
        return CommonMessages.msg.unsupported(element.getMethodName(), element.getClassName());
    }
}
