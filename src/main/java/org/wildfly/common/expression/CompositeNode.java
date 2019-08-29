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
