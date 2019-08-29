package org.wildfly.common.lock;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;

final class Substitutions {
    @TargetClass(JDKSpecific.class)
    static final class Target_JDKSpecific {
        @Substitute
        static void onSpinWait() {
            Target_PauseNode.pause();
        }
    }

    @TargetClass(className = "org.graalvm.compiler.nodes.PauseNode")
    static final class Target_PauseNode {
        @Alias
        public static native void pause();
    }
}
