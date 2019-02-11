/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018 Red Hat, Inc., and individual contributors
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

package org.wildfly.common;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.AlwaysInline;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

/**
 */
final class Substitutions {
    @TargetClass(Branch.class)
    static final class Target_Branch {
        @AlwaysInline("Straight call to GraalVM")
        @Substitute
        public static boolean veryLikely(boolean expr) {
            return Target_GraalDirectives.injectBranchProbability(Target_GraalDirectives.FASTPATH_PROBABILITY, expr);
        }

        @AlwaysInline("Straight call to GraalVM")
        @Substitute
        public static boolean veryUnlikely(boolean expr) {
            return Target_GraalDirectives.injectBranchProbability(Target_GraalDirectives.SLOWPATH_PROBABILITY, expr);
        }

        @AlwaysInline("Straight call to GraalVM")
        @Substitute
        public static boolean likely(boolean expr) {
            return Target_GraalDirectives.injectBranchProbability(Target_GraalDirectives.LIKELY_PROBABILITY, expr);
        }

        @AlwaysInline("Straight call to GraalVM")
        @Substitute
        public static boolean unlikely(boolean expr) {
            return Target_GraalDirectives.injectBranchProbability(Target_GraalDirectives.UNLIKELY_PROBABILITY, expr);
        }

        @AlwaysInline("Straight call to GraalVM")
        @Substitute
        public static boolean probability(float prob, boolean expr) {
            return Target_GraalDirectives.injectBranchProbability(prob, expr);
        }
    }

    @TargetClass(className = "org.graalvm.compiler.api.directives.GraalDirectives")
    static final class Target_GraalDirectives {
        @Alias
        public static double LIKELY_PROBABILITY;
        @Alias
        public static double UNLIKELY_PROBABILITY;

        @Alias
        public static double SLOWPATH_PROBABILITY;
        @Alias
        public static double FASTPATH_PROBABILITY;

        @Alias
        public static boolean injectBranchProbability(double probability, boolean condition) {
            assert probability >= 0.0 && probability <= 1.0;
            return condition;
        }
    }
}
