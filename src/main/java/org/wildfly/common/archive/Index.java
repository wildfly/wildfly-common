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

/**
 */
abstract class Index {
    final int tableSize;

    Index(final int entries) {
        if (entries >= 1 << 30) {
            throw new IllegalStateException("Index is too large");
        }
        this.tableSize = Integer.highestOneBit(entries << 2);
    }

    final int size() {
        return tableSize;
    }

    abstract long get(int index);

    abstract void put(int index, long offset);

    int getMask() {
        return tableSize - 1;
    }
}
