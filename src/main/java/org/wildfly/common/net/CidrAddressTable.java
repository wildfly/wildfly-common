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

package org.wildfly.common.net;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;

/**
 * A table for mapping IP addresses to objects using {@link CidrAddress} instances for matching.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @deprecated Use {@link io.smallrye.common.net.CidrAddressTable} instead.
 */
@Deprecated(forRemoval = true)
public final class CidrAddressTable<T> implements Iterable<CidrAddressTable.Mapping<T>> {

    private final io.smallrye.common.net.CidrAddressTable<T> cidrAddressTable;

    public CidrAddressTable() {
        this(new io.smallrye.common.net.CidrAddressTable<>());
    }

    private CidrAddressTable(final io.smallrye.common.net.CidrAddressTable<T> cidrAddressTable) {
        this.cidrAddressTable = cidrAddressTable;
    }

    public T getOrDefault(InetAddress address, T defVal) {
        return cidrAddressTable.getOrDefault(address, defVal);
    }

    public T get(InetAddress address) {
        return getOrDefault(address, null);
    }

    public T put(CidrAddress block, T value) {
        return cidrAddressTable.put(block.cidrAddress, value);
    }

    public T putIfAbsent(CidrAddress block, T value) {
        return cidrAddressTable.putIfAbsent(block.cidrAddress, value);
    }

    public T replaceExact(CidrAddress block, T value) {
        return cidrAddressTable.replaceExact(block.cidrAddress, value);
    }

    public boolean replaceExact(CidrAddress block, T expect, T update) {
        return cidrAddressTable.replaceExact(block.cidrAddress, expect, update);
    }

    public T removeExact(CidrAddress block) {
        return cidrAddressTable.removeExact(block.cidrAddress);
    }

    public boolean removeExact(CidrAddress block, T expect) {
        return cidrAddressTable.removeExact(block.cidrAddress, expect);
    }

    public void clear() {
        cidrAddressTable.clear();
    }

    public int size() {
        return cidrAddressTable.size();
    }

    public boolean isEmpty() {
        return cidrAddressTable.isEmpty();
    }

    public CidrAddressTable<T> clone() {
        return new CidrAddressTable<>(cidrAddressTable.clone());
    }

    public Iterator<Mapping<T>> iterator() {
        // quite ugly, but also as lazy as possible
        Map<io.smallrye.common.net.CidrAddressTable.Mapping<T>, Mapping<T>> map = new HashMap<>();
        Iterator<io.smallrye.common.net.CidrAddressTable.Mapping<T>> iter = cidrAddressTable.iterator();
        return new Iterator<Mapping<T>>() {
            public boolean hasNext() {
                return iter.hasNext();
            }

            public Mapping<T> next() {
                return computeOne(iter.next(), map);
            }
        };
    }

    private static <T> Mapping<T> computeOne(io.smallrye.common.net.CidrAddressTable.Mapping<T> orig, Map<io.smallrye.common.net.CidrAddressTable.Mapping<T>, Mapping<T>> map) {
        if (orig == null) {
            return null;
        }
        Mapping<T> mapped = map.get(orig);
        if (mapped == null) {
            Mapping<T> parent = computeOne(orig.getParent(), map);
            mapped = new Mapping<>(new CidrAddress(orig.getRange()), orig.getValue(), parent);
            map.put(orig, mapped);
        }
        return mapped;
    }

    public Spliterator<Mapping<T>> spliterator() {
        return Spliterators.spliterator(iterator(), size(), Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    public String toString() {
        return cidrAddressTable.toString();
    }
    /**
     * A single mapping in the table.
     *
     * @param <T> the value type
     */
    public static final class Mapping<T> {
        final CidrAddress range;
        final T value;
        final Mapping<T> parent;

        Mapping(final CidrAddress range, final T value, final Mapping<T> parent) {
            this.range = range;
            this.value = value;
            this.parent = parent;
        }

        /**
         * Get the address range of this entry.
         *
         * @return the address range of this entry (not {@code null})
         */
        public CidrAddress getRange() {
            return range;
        }

        /**
         * Get the stored value of this entry.
         *
         * @return the stored value of this entry
         */
        public T getValue() {
            return value;
        }

        /**
         * Get the parent of this entry, if any.
         *
         * @return the parent of this entry, or {@code null} if there is no parent
         */
        public Mapping<T> getParent() {
            return parent;
        }
    }
}
