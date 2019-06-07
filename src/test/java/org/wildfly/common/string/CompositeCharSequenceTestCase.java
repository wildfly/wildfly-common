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


package org.wildfly.common.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.stream.IntStream;

import org.junit.Test;

/**
 * Unit test for {@link CompositeCharSequence}.
 * @author Paul Ferraro
 */
public class CompositeCharSequenceTestCase {
    private final CharSequence sequence = new CompositeCharSequence("01", "23", "45");

    @Test
    public void length() {
        assertEquals(6, this.sequence.length());
    }

    @Test
    public void string() {
        assertEquals("012345", this.sequence.toString());
    }

    @Test
    public void charAt() {
        for (int i = -1; i < 7; ++i) {
            try {
                char result = this.sequence.charAt(i);
                if ((i < 0) || (i >= 6)) {
                    fail(String.format("charAt(%d) returned '%s', but IndexOutOfBoundsException was expected", i, result));
                }
                assertEquals('0' + i, result);
            } catch (IndexOutOfBoundsException e) {
                if ((i >= 0) && (i < 6)) {
                    fail(String.format("Unexpected IndexOutOfBoundsException during charAt(%d)", i));
                }
            }
        }
    }

    @Test
    public void subSequence() {
        for (int i = -1; i < 7; ++i) {
            for (int j = -1; j <= 7; ++j) {
                try {
                    CharSequence result = this.sequence.subSequence(i, j);
                    if ((i < 0) || (i > j) || (j > 6)) {
                        fail(String.format("subSequence(%d, %d) returned '%s', but IndexOutOfBoundsException was expected", i, j, result.toString()));
                    }
                    StringBuilder expected = new StringBuilder(j - i);
                    IntStream.range(i, j).forEach(value -> expected.append((char) ('0' + value)));
                    assertEquals(expected.toString(), result.toString());
                } catch (IndexOutOfBoundsException e) {
                    if ((i >= 0) && (j <= 6) && (i <= j)) {
                        fail(String.format("Unexpected IndexOutOfBoundsException during subSequence(%d, %d)", i, j));
                    }
                }
            }
        }
    }

    @Test
    public void equals() {
        assertTrue(this.sequence.equals("012345"));
        assertTrue(this.sequence.equals(new CompositeCharSequence("012", "345")));
        assertTrue(this.sequence.equals(new CompositeCharSequence("012345")));
    }

    @Test
    public void hash() {
        int hashCode = this.sequence.hashCode();
        assertEquals("012345".hashCode(), hashCode);
        assertEquals(new CompositeCharSequence("012", "345").hashCode(), hashCode);
        assertEquals(new CompositeCharSequence("012345").hashCode(), hashCode);
    }
}
