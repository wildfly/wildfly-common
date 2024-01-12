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

package org.wildfly.common.cpu;

/**
 * Provides general information about the processors on this host (Java 9 version).
 *
 * @deprecated Use {@link io.smallrye.common.cpu.ProcessorInfo} instead.
 */
@Deprecated(forRemoval = true)
public class ProcessorInfo {
    private ProcessorInfo() {
    }

    /**
     * {@return the number of available processors}
     * @deprecated Use {@link io.smallrye.common.cpu.ProcessorInfo#availableProcessors()} instead.
     */
    @Deprecated(forRemoval = true)
    public static int availableProcessors() {
        return io.smallrye.common.cpu.ProcessorInfo.availableProcessors();
    }
}
