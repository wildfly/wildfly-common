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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link CompositeIterator}.
 * @author Paul Ferraro
 */
public class CompositeIteratorTestCase {

    @Test
    public void remove() {
        List<Integer> list = new ArrayList<>(IntStream.range(0, 10).mapToObj(Integer::valueOf).collect(Collectors.toList()));
        Iterator<Integer> iterator = new CompositeIterator<>(Collections.<Integer>emptyList().iterator(), list.iterator(), Collections.<Integer>emptyList().iterator());

        try {
            iterator.remove();
            Assert.fail("remove() should fail before first call to next()");
        } catch (IllegalStateException e) {
            // Expected
        }

        IntStream.range(0, 10).forEach(i -> {
            Assert.assertTrue(iterator.hasNext());
            Assert.assertEquals(i, iterator.next().intValue());
            iterator.remove();
        });
        Assert.assertFalse(iterator.hasNext());

        try {
            iterator.next();
            Assert.fail("next() should fail when hasNext() = false");
        } catch (NoSuchElementException e) {
            // Expected
        }

        Assert.assertTrue(list.isEmpty());
    }


    @Test
    public void forEachRemaining() {
        List<Integer> list1 = IntStream.range(0, 5).mapToObj(Integer::valueOf).collect(Collectors.toList());
        List<Integer> list2 = IntStream.range(5, 10).mapToObj(Integer::valueOf).collect(Collectors.toList());
        Iterator<Integer> iterator = new CompositeIterator<>(Collections.<Integer>emptyList().iterator(), list1.iterator(), list2.iterator(), Collections.<Integer>emptyList().iterator());

        Assert.assertTrue(iterator.hasNext());
        // Skip 0
        iterator.next();
        Assert.assertTrue(iterator.hasNext());

        int expected = IntStream.range(1, 10).reduce(1, Math::multiplyExact);
        AtomicInteger result = new AtomicInteger(1);
        iterator.forEachRemaining(value -> result.accumulateAndGet(value, Math::multiplyExact));
        Assert.assertEquals(expected, result.get());

        // Iterator should be drained
        Assert.assertFalse(iterator.hasNext());

        try {
            iterator.next();
            Assert.fail("next() should fail when hasNext() = false");
        } catch (NoSuchElementException e) {
            // Expected
        }
    }
}
