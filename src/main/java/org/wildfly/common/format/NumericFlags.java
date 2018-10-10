/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
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

package org.wildfly.common.format;

/**
 */
public final class NumericFlags extends FormatFlags<NumericFlag, NumericFlags> {

    private static final NumericFlag[] flagValues = NumericFlag.values();
    private static final NumericFlags[] setValues;

    static {
        final int cnt = 1 << flagValues.length;
        NumericFlags[] array = new NumericFlags[cnt];
        for (int i = 0; i < cnt; i ++) {
            array[i] = new NumericFlags(i);
        }
        setValues = array;
    }

    /**
     * The empty set of numeric flags.
     */
    public static final NumericFlags NONE = setValues[0];

    public static NumericFlags of(NumericFlag flag) {
        return NONE.with(flag);
    }

    public static NumericFlags of(NumericFlag flag1, NumericFlag flag2) {
        return NONE.with(flag1, flag2);
    }

    private NumericFlags(int bits) {
        super(bits);
    }

    protected NumericFlags this_() {
        return this;
    }

    protected NumericFlags value(final int bits) {
        return setValues[bits & setValues.length - 1];
    }

    protected NumericFlag itemOf(final int index) {
        return flagValues[index];
    }

    protected NumericFlag castItemOrNull(final Object obj) {
        return obj instanceof NumericFlag ? (NumericFlag) obj : null;
    }

    protected NumericFlags castThis(final Object obj) {
        return (NumericFlags) obj;
    }
}
