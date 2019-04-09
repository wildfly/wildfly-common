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

package org.wildfly.common.iteration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CompositeIterable}.
 * @author Paul Ferraro
 */
public class CompositeIterableTestCase {

    @Test
    public void test() {
        List<Integer> expected = IntStream.range(0, 10).mapToObj(Integer::valueOf).collect(Collectors.toList());

        test(expected, new CompositeIterable<>(new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4)), Arrays.asList(5, 6, 7, 8, 9)));
        test(expected, new CompositeIterable<>(new ArrayList<>(Arrays.asList(0, 1)), Arrays.asList(2, 3), Arrays.asList(4, 5), Arrays.asList(6, 7), Arrays.asList(8, 9)));
        test(expected, new CompositeIterable<>(Collections.emptyList(), new ArrayList<>(expected), Collections.emptyList()));
    }

    @Test
    public void testRemove() {
        
    }

    static void test(Iterable<Integer> expected, Iterable<Integer> subject) {
        Assert.assertEquals(expected.hashCode(), subject.hashCode());
        Assert.assertEquals(expected.toString(), subject.toString());

        Iterator<Integer> subjectIterator = subject.iterator();
        Iterator<Integer> expectedIterator = expected.iterator();
        while (expectedIterator.hasNext()) {
            Assert.assertTrue(subjectIterator.hasNext());
            Assert.assertEquals(expectedIterator.next(), subjectIterator.next());
        }
        Assert.assertFalse(subjectIterator.hasNext());
    }
}
