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
public final class GeneralFlags extends FormatFlags<GeneralFlag, GeneralFlags> {

    private static final GeneralFlag[] flagValues = GeneralFlag.values();
    private static final GeneralFlags[] setValues;

    static {
        final int cnt = 1 << flagValues.length;
        GeneralFlags[] array = new GeneralFlags[cnt];
        for (int i = 0; i < cnt; i ++) {
            array[i] = new GeneralFlags(i);
        }
        setValues = array;
    }

    /**
     * The empty set of general flags.
     */
    public static final GeneralFlags NONE = setValues[0];

    public static GeneralFlags of(GeneralFlag flag) {
        return NONE.with(flag);
    }

    public static GeneralFlags of(GeneralFlag flag1, GeneralFlag flag2) {
        return NONE.with(flag1, flag2);
    }

    public static GeneralFlags of(GeneralFlag flag1, GeneralFlag flag2, GeneralFlag flag3) {
        return NONE.with(flag1, flag2, flag3);
    }

    private GeneralFlags(int bits) {
        super(bits);
    }

    protected GeneralFlags this_() {
        return this;
    }

    protected GeneralFlags value(final int bits) {
        return setValues[bits & setValues.length - 1];
    }

    protected GeneralFlag itemOf(final int index) {
        return flagValues[index];
    }

    protected GeneralFlag castItemOrNull(final Object obj) {
        return obj instanceof GeneralFlag ? (GeneralFlag) obj : null;
    }

    protected GeneralFlags castThis(final Object obj) {
        return (GeneralFlags) obj;
    }
}
