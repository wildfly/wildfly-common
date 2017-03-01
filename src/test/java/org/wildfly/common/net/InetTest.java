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
import java.net.UnknownHostException;

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

    @Test
    public void testUnresolved() throws Exception {
        assertEquals("foo bar", getHostNameIfResolved(InetAddress.getByAddress("foo bar", new byte[]{ 127, 0, 0, 1})));
        assertEquals("", getHostNameIfResolved(InetAddress.getByAddress("", new byte[]{ 127, 0, 0, 1})));
        assertNull(getHostNameIfResolved(InetAddress.getByAddress(new byte[]{ 127, 0, 0, 1})));
    }

    @Test
    public void testToAddressBytes() throws Exception {
        checkAddressToBytes("::");
        checkAddressToBytes("::1");
        checkAddressToBytes("::7:8");
        checkAddressToBytes("1::");
        checkAddressToBytes("1:2::");
        checkAddressToBytes("1:2::7:8");
        checkAddressToBytes("1:2:3:4:5::7:8");
        checkAddressToBytes("1:2:3:4:5:6:7:8");
        checkAddressToBytes("ff1:ff2:ff3:ff4:ff5:ff6:ff7:ff8");
        checkAddressToBytes("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff");
        checkAddressToBytes("000F:00EF:0DEF:CDEF::");

        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0xffff, 0x0a00, 1), parseInet6AddressToBytes("::ffff:10.0.0.1"));
        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0, 0x7f00, 1), parseInet6AddressToBytes("::127.0.0.1"));
        assertArrayEquals(bytes(0, 0, 0, 0, 0, 0, 0x7f00, 1), parseInet6AddressToBytes("0:0:0:0:0:0:127.0.0.1"));

        assertNull(parseInet6AddressToBytes("1:2:3:4:5:6:7"));
        assertNull(parseInet6AddressToBytes("1:2:3:4:5:6:7:8:9"));
        assertNull(parseInet6AddressToBytes("1:2:3:4:5:6:7:1.1.1.1"));
        assertNull(parseInet6AddressToBytes("1:2:3:4:5:1.1.1.1"));
        assertNull(parseInet6AddressToBytes("1:2::5::8"));
        assertNull(parseInet6AddressToBytes("1:2:::8"));
        assertNull(parseInet6AddressToBytes("1:2:::10000"));
        assertNull(parseInet6AddressToBytes("1:2:::x"));
        assertNull(parseInet6AddressToBytes("::1.2.3"));
        assertNull(parseInet6AddressToBytes("::1.2.3.4.5"));
        assertNull(parseInet6AddressToBytes("::1.2.3.256"));
    }

    private void checkAddressToBytes(String ipv6) throws UnknownHostException {
        byte[] bytes = parseInet6AddressToBytes(ipv6);
        assertNotNull(bytes);
        assertArrayEquals(InetAddress.getByName(ipv6).getAddress(), bytes);
    }

    private byte[] bytes(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
        byte[] bytes = new byte[16];
        bytes[0] = (byte) (s1 >> 8);
        bytes[1] = (byte) s1;
        bytes[2] = (byte) (s2 >> 8);
        bytes[3] = (byte) s2;
        bytes[4] = (byte) (s3 >> 8);
        bytes[5] = (byte) s3;
        bytes[6] = (byte) (s4 >> 8);
        bytes[7] = (byte) s4;
        bytes[8] = (byte) (s5 >> 8);
        bytes[9] = (byte) s5;
        bytes[10] = (byte) (s6 >> 8);
        bytes[11] = (byte) s6;
        bytes[12] = (byte) (s7 >> 8);
        bytes[13] = (byte) s7;
        bytes[14] = (byte) (s8 >> 8);
        bytes[15] = (byte) s8;
        return bytes;
    }
}
