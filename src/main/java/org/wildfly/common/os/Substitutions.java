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
