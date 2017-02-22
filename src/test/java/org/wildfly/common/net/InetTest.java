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

package org.wildfly.common.net;

import static org.junit.Assert.*;
import static org.wildfly.common.net.Inet.*;

import java.net.InetAddress;

import org.junit.Test;

/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class InetTest {

    @Test
    public void testRepresentation1() throws Exception {
        assertEquals("::1", toOptimalString(InetAddress.getByName("0:0::1")));
        assertEquals("::", toOptimalString(InetAddress.getByName("0:0:0:0:0:0:0:0")));
        assertEquals("1::", toOptimalString(InetAddress.getByName("1:0:0:0:0:0:0:0")));
        assertEquals("1:1::1:1", toOptimalString(InetAddress.getByName("1:1:0:0:0:0:1:1")));
        assertEquals("1:1::1:1", toOptimalString(getInet6Address(1, 1, 0, 0, 0, 0, 1, 1)));
        assertEquals("1:2:3:4:5:6:7:8", toOptimalString(InetAddress.getByName("1:2:3:4:5:6:7:8")));
        assertEquals("9:a:b:cc:dd:0:eee:ffff", toOptimalString(InetAddress.getByName("0009:000A:000B:00CC:00DD:0000:0EEE:FFFF")));
        assertEquals("1:2:3:0:5:6:7:8", toOptimalString(InetAddress.getByName("1:2:3:0:5:6:7:8")));
        assertEquals("1:0:3:0:5:6:7:8", toOptimalString(InetAddress.getByName("1:0:3:0:5:6:7:8")));
        assertEquals("1:0:3::6:7:8", toOptimalString(InetAddress.getByName("1:0:3:0:0:6:7:8")));
        assertEquals("1::4:0:0:7:8", toOptimalString(InetAddress.getByName("1:0:0:4:0:0:7:8")));
        assertEquals("::ffff:0:127.0.0.1", toOptimalString(InetAddress.getByName("0::ffff:0:127.0.0.1")));
        assertEquals("::ffff:127.0.0.1", toOptimalString(toInet6Address(getInet4Address(127,0,0,1))));
    }
}
