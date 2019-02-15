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

import org.junit.Test;

/**
 *
 */
public class ArchiveIndexTest {

    private void doIndexTest(Index index) {
        // simple get/put
        index.put(4, 1234);
        assertEquals(1234, index.get(4));
        index.put(0, 5555);
        assertEquals(5555, index.get(0));
        assertEquals(-1, index.get(3));
        // check linear probing
        index.put(4, 2345);
        assertEquals(1234, index.get(4));
        assertEquals(2345, index.get(5));
        assertEquals(-1, index.get(6));
    }

    @Test
    public void testTinyIndex() {
        doIndexTest(new TinyIndex(130));
    }

    @Test
    public void testLargeIndex() {
        doIndexTest(new LargeIndex(112));
    }

    @Test
    public void testHugeIndex() {
        doIndexTest(new HugeIndex(112));
    }
}
