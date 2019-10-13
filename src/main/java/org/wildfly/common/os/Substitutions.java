package org.wildfly.common.os;

import java.io.File;

import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.graalvm.nativeimage.ProcessProperties;

final class Substitutions {

    @TargetClass(className = "org.wildfly.common.os.GetProcessInfoAction")
    static final class Target_org_wildfly_common_os_GetProcessInfoAction {
        @Substitute
        public Object[] run() {
            return new Object[] { Long.valueOf(ProcessProperties.getProcessID() & 0xffff_ffffL), ProcessUtils.getProcessName() };
        }
    }

    static final class ProcessUtils {
       static String getProcessName() {
           String name = System.getProperty("jboss.process.name");
           if (name == null) {
               String exeName = ProcessProperties.getExecutableName();
               if (! exeName.isEmpty()) {
                   int idx = exeName.lastIndexOf(File.separatorChar);
                   name = idx == -1 ? exeName : idx == exeName.length() - 1 ? null : exeName.substring(idx + 1);
               }
           }
           if (name == null) {
               name = "<unknown>";
           }
           return name;
       }
   }
}
