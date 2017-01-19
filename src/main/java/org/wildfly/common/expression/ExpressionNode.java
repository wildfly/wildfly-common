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

import org.wildfly.common.function.ExceptionBiConsumer;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
class ExpressionNode extends Node {
    private final boolean generalExpression;
    private final Node key;
    private final Node defaultValue;

    ExpressionNode(final boolean generalExpression, final Node key, final Node defaultValue) {
        this.generalExpression = generalExpression;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    <E extends Exception> void emit(final ResolveContext<E> context, final ExceptionBiConsumer<ResolveContext<E>, StringBuilder, E> resolveFunction) throws E {
        ExpressionNode oldCurrent = context.setCurrent(this);
        try {
            resolveFunction.accept(context, context.getStringBuilder());
        } finally {
            context.setCurrent(oldCurrent);
        }
    }

    void catalog(final HashSet<String> strings) {
        if (key instanceof LiteralNode) {
            strings.add(key.toString());
        } else {
            key.catalog(strings);
        }
        defaultValue.catalog(strings);
    }

    boolean isGeneralExpression() {
        return generalExpression;
    }

    Node getKey() {
        return key;
    }

    Node getDefaultValue() {
        return defaultValue;
    }

    public String toString() {
        return String.format("Expr<%s:%s>", key, defaultValue);
    }
}
