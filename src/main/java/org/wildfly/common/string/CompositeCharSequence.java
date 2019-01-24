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

import java.io.Serializable;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A {@link CharSequence} composed of other character sequences.
 * All methods delegate to one or more of the underlying character sequences, using relative indexes.
 * @author Paul Ferraro
 */
public class CompositeCharSequence implements CharSequence, Serializable {
    private static final long serialVersionUID = 4975968165050531721L;

    private final List<CharSequence> sequences;
    private transient int hashCode = 0;

    /**
     * Constructs a new composite character sequence.
     * @param sequences an array of character sequences.
     */
    public CompositeCharSequence(CharSequence... sequences) {
        this(Arrays.asList(sequences));
    }

    /**
     * Constructs a new composite character sequence.
     * @param sequences a list of character sequences.
     */
    public CompositeCharSequence(List<CharSequence> sequences) {
        this.sequences = sequences;
    }

    @Override
    public int length() {
        int length = 0;
        for (CharSequence sequence : this.sequences) {
            length += sequence.length();
        }
        return length;
    }

    @Override
    public char charAt(int index) {
        int relativeIndex = index;
        for (CharSequence sequence : this.sequences) {
            if (relativeIndex < sequence.length()) {
                return sequence.charAt(relativeIndex);
            }
            relativeIndex -= sequence.length();
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        if ((start < 0) || (start > end) || (end > this.length())) {
            throw new IndexOutOfBoundsException();
        }
        if (start == end) return "";
        List<CharSequence> result = null;
        int relativeStart = start;
        int relativeEnd = end;
        for (CharSequence sequence : this.sequences) {
            if ((relativeStart < sequence.length()) && (relativeEnd > 0)) {
                CharSequence subSequence = sequence.subSequence(Math.max(relativeStart, 0), Math.min(relativeEnd, sequence.length()));
                if (result == null) {
                    // If subsequence falls within a single sequence
                    if ((relativeStart >= 0) && (relativeEnd <= sequence.length())) {
                        return subSequence;
                    }
                    result = new LinkedList<>();
                }
                result.add(subSequence);
            }
            relativeStart -= sequence.length();
            relativeEnd -= sequence.length();
        }
        return new CompositeCharSequence(result);
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        if (result == 0) {
            for (int i = 0; i < this.length(); ++i) {
                result = 31 * result + this.charAt(i);
            }
            this.hashCode = result;
        }
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CharSequence)) return false;
        CharSequence sequence = (CharSequence) object;
        int length = sequence.length();
        if (this.length() != length) return false;
        for (int i = 0; i < length; ++i) {
            if (this.charAt(i) != sequence.charAt(i)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        CharBuffer buffer = CharBuffer.allocate(this.length());
        for (CharSequence sequence : this.sequences) {
            buffer.put(CharBuffer.wrap(sequence));
        }
        return String.valueOf(buffer.array());
    }
}
