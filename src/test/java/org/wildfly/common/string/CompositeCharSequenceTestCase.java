/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2019, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.wildfly.common.string;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.stream.IntStream;

import org.junit.Test;
import org.wildfly.common.string.CompositeCharSequence;

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
