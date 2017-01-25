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

package org.wildfly.common.expression;

import java.util.HashSet;
import java.util.List;

import org.wildfly.common.function.ExceptionBiConsumer;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
abstract class Node {

    static final Node[] NO_NODES = new Node[0];

    Node() {
    }

    static Node fromList(List<Node> list) {
        if (list == null || list.isEmpty()) {
            return NULL;
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            return new CompositeNode(list);
        }
    }

    static final Node NULL = new Node() {
        <E extends Exception> void emit(final ResolveContext<E> context, final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
        }

        void catalog(final HashSet<String> strings) {
        }

        public String toString() {
            return "<<null>>";
        }
    };

    abstract <E extends Exception> void emit(final ResolveContext<E> context, final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E;

    abstract void catalog(final HashSet<String> strings);
}

