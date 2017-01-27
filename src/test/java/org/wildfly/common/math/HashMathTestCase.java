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

package org.wildfly.common.math;

import static org.junit.Assert.*;
import static org.wildfly.common.math.HashMath.multiHashOrdered;
import static org.wildfly.common.math.HashMath.multiHashUnordered;

import org.junit.Test;

public class HashMathTestCase {

    @Test
    public void testOrderedCommutative() {
        final int ab = multiHashOrdered(multiHashOrdered(1234, 65537, 13), 16633, 5342);
        final int ba = multiHashOrdered(multiHashOrdered(1234, 16633, 5342), 65537, 13);
        assertNotEquals(ab, ba);
        final int cd = multiHashOrdered(multiHashOrdered(0, 65537, 13), 16633, 5342);
        final int dc = multiHashOrdered(multiHashOrdered(0, 16633, 5342), 65537, 13);
        assertNotEquals(cd, dc);
        final int ef = multiHashOrdered(multiHashOrdered(0xf948_1829, 65537, 13), 16633, 5342);
        final int fe = multiHashOrdered(multiHashOrdered(0xf948_1829, 16633, 5342), 65537, 13);
        assertNotEquals(ef, fe);
        final int gh = multiHashOrdered(multiHashOrdered(0xf948_1829, 65537, 13), 16633, 0);
        final int hg = multiHashOrdered(multiHashOrdered(0xf948_1829, 16633, 0), 65537, 13);
        assertNotEquals(gh, hg);
    }

    @Test
    public void testUnorderedCommutative() {
        final int ab = multiHashUnordered(multiHashUnordered(1234, 65537, 13), 16633, 5342);
        final int ba = multiHashUnordered(multiHashUnordered(1234, 16633, 5342), 65537, 13);
        assertEquals(ab, ba);
        final int cd = multiHashUnordered(multiHashUnordered(0, 65537, 13), 16633, 5342);
        final int dc = multiHashUnordered(multiHashUnordered(0, 16633, 5342), 65537, 13);
        assertEquals(cd, dc);
        final int ef = multiHashUnordered(multiHashUnordered(0xf948_1829, 65537, 13), 16633, 5342);
        final int fe = multiHashUnordered(multiHashUnordered(0xf948_1829, 16633, 5342), 65537, 13);
        assertEquals(ef, fe);
        final int gh = multiHashUnordered(multiHashUnordered(0xf948_1829, 65537, 13), 16633, 0);
        final int hg = multiHashUnordered(multiHashUnordered(0xf948_1829, 16633, 0), 65537, 13);
        assertEquals(gh, hg);
    }
}
