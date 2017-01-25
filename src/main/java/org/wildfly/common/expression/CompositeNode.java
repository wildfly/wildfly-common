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
final class CompositeNode extends Node {
    private final Node[] subNodes;

    CompositeNode(final Node[] subNodes) {
        this.subNodes = subNodes;
    }

    CompositeNode(final List<Node> subNodes) {
        this.subNodes = subNodes.toArray(NO_NODES);
    }

    <E extends Exception> void emit(final ResolveContext<E> context, final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
        for (Node subNode : subNodes) {
            subNode.emit(context, resolveFunction);
        }
    }

    void catalog(final HashSet<String> strings) {
        for (Node node : subNodes) {
            node.catalog(strings);
        }
    }

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('*');
        for (Node subNode : subNodes) {
            b.append('<').append(subNode.toString()).append('>');
        }
        return b.toString();
    }
}
