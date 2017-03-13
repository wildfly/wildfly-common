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

import static java.security.AccessController.doPrivileged;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.wildfly.common.Assert;
import org.wildfly.common._private.CommonMessages;

/**
 * Utilities relating to Internet protocol (a.k.a. "INET" or "IP") address manipulation.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class Inet {
    private Inet() {}

    /**
     * The "any" address for IPv4.
     */
    public static final Inet4Address INET4_ANY = getInet4Address(0, 0, 0, 0);

    /**
     * The traditional loopback address for IPv4.
     */
    public static final Inet4Address INET4_LOOPBACK = getInet4Address(127, 0, 0, 1);

    /**
     * The broadcast-all address for IPv4.
     */
    public static final Inet4Address INET4_BROADCAST = getInet4Address(255, 255, 255, 255);

    /**
     * The "any" address for IPv6.
     */
    public static final Inet6Address INET6_ANY = getInet6Address(0, 0, 0, 0, 0, 0, 0, 0);

    /**
     * The loopback address for IPv6.
     */
    public static final Inet6Address INET6_LOOPBACK = getInet6Address(0, 0, 0, 0, 0, 0, 0, 1);

    /**
     * Get the optimal string representation of an IP address.  For IPv6 addresses, this representation will be
     * more compact that the default.
     *
     * @param inetAddress the address (must not be {@code null})
     * @return the string representation (not {@code null})
     */
    public static String toOptimalString(InetAddress inetAddress) {
        Assert.checkNotNullParam("inetAddress", inetAddress);
        return inetAddress instanceof Inet6Address ? toOptimalStringV6(inetAddress.getAddress()) : inetAddress.getHostAddress();
    }

    /**
     * Get the optimal string representation of the bytes of an IP address.
     *
     * @param addressBytes the address bytes (must not be {@code null})
     * @return the string representation (not {@code null})
     */
    public static String toOptimalString(byte[] addressBytes) {
        Assert.checkNotNullParam("addressBytes", addressBytes);
        if (addressBytes.length == 4) {
            return (addressBytes[0] & 0xff) + "." + (addressBytes[1] & 0xff) + "." + (addressBytes[2] & 0xff) + "." + (addressBytes[3] & 0xff);
        } else if (addressBytes.length == 16) {
            return toOptimalStringV6(addressBytes);
        } else {
            throw CommonMessages.msg.invalidAddressBytes(addressBytes.length);
        }
    }

    /**
     * Get the IPv6 equivalent of the given address.  If the address is IPv4 then it is returned as a compatibility
     * address.
     *
     * @param inetAddress the address to convert (must not be {@code null})
     * @return the converted address (not {@code null})
     */
    public static Inet6Address toInet6Address(InetAddress inetAddress) {
        if (inetAddress instanceof Inet6Address) {
            return (Inet6Address) inetAddress;
        } else {
            assert inetAddress instanceof Inet4Address;
            final byte[] addr = new byte[16];
            addr[10] = addr[11] = (byte) 0xff;
            System.arraycopy(inetAddress.getAddress(), 0, addr, 12, 4);
            // get unresolved host name
            try {
                return Inet6Address.getByAddress(getHostNameIfResolved(inetAddress), addr, 0);
            } catch (UnknownHostException e) {
                // not possible
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Get the host name of the given address, if it is resolved.  Otherwise, return {@code null}.
     *
     * @param inetAddress the address to check (must not be {@code null})
     * @return the host name, or {@code null} if the address has no host name and is unresolved
     */
    public static String getHostNameIfResolved(InetAddress inetAddress) {
        final String hostString = new InetSocketAddress(inetAddress, 0).getHostString();
        final String toString = inetAddress.toString();
        final int slash = toString.lastIndexOf('/');
        if (slash == 0) {
            // it might be unresolved or it might explicitly be ""
            return hostString.isEmpty() ? "" : null;
        }
        return hostString;
    }

    /**
     * Get an IPv4 address from four integer segments.  Each segment must be between 0 and 255.
     *
     * @param s1 the first segment
     * @param s2 the second segment
     * @param s3 the third segment
     * @param s4 the fourth segment
     * @return the address (not {@code null})
     */
    public static Inet4Address getInet4Address(int s1, int s2, int s3, int s4) {
        byte[] bytes = new byte[4];
        Assert.checkMinimumParameter("s1", 0, s1);
        Assert.checkMaximumParameter("s1", 255, s1);
        Assert.checkMinimumParameter("s2", 0, s2);
        Assert.checkMaximumParameter("s2", 255, s2);
        Assert.checkMinimumParameter("s3", 0, s3);
        Assert.checkMaximumParameter("s3", 255, s3);
        Assert.checkMinimumParameter("s4", 0, s4);
        Assert.checkMaximumParameter("s4", 255, s4);
        bytes[0] = (byte) s1;
        bytes[1] = (byte) s2;
        bytes[2] = (byte) s3;
        bytes[3] = (byte) s4;
        try {
            return (Inet4Address) InetAddress.getByAddress(s1 + "." + s2 + "." + s3 + "." + s4, bytes);
        } catch (UnknownHostException e) {
            // not possible
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get an IPv6 address from eight integer segments.  Each segment must be between 0 and 65535 ({@code 0xffff}).
     *
     * @param s1 the first segment
     * @param s2 the second segment
     * @param s3 the third segment
     * @param s4 the fourth segment
     * @param s5 the fifth segment
     * @param s6 the sixth segment
     * @param s7 the seventh segment
     * @param s8 the eighth segment
     * @return the address (not {@code null})
     */
    public static Inet6Address getInet6Address(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
        byte[] bytes = new byte[16];
        Assert.checkMinimumParameter("s1", 0, s1);
        Assert.checkMaximumParameter("s1", 0xffff, s1);
        Assert.checkMinimumParameter("s2", 0, s2);
        Assert.checkMaximumParameter("s2", 0xffff, s2);
        Assert.checkMinimumParameter("s3", 0, s3);
        Assert.checkMaximumParameter("s3", 0xffff, s3);
        Assert.checkMinimumParameter("s4", 0, s4);
        Assert.checkMaximumParameter("s4", 0xffff, s4);
        Assert.checkMinimumParameter("s5", 0, s5);
        Assert.checkMaximumParameter("s5", 0xffff, s5);
        Assert.checkMinimumParameter("s6", 0, s6);
        Assert.checkMaximumParameter("s6", 0xffff, s6);
        Assert.checkMinimumParameter("s7", 0, s7);
        Assert.checkMaximumParameter("s7", 0xffff, s7);
        Assert.checkMinimumParameter("s8", 0, s8);
        Assert.checkMaximumParameter("s8", 0xffff, s8);
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
        try {
            return Inet6Address.getByAddress(toOptimalStringV6(bytes), bytes, 0);
        } catch (UnknownHostException e) {
            // not possible
            throw new IllegalStateException(e);
        }
    }

    /**
     * Checks whether given String is a valid IP address.
     *
     * @param address address textual representation
     * @return {@code true} if {@code address} is a valid IP address, {@code false} otherwise
     */
    public static boolean isInetAddress(String address) {
        return isInet4Address(address) || isInet6Address(address);
    }

    /**
     * Parse an IP address into an {@code InetAddress} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     */
    public static InetAddress parseInetAddress(String address) {
        // not a great heuristic but good enough
        if (address.charAt(0) == '[' || address.indexOf(':') != -1) {
            return parseInet6Address(address);
        } else {
            return parseInet4Address(address);
        }
    }

    /**
     * Converts IP address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IP address, the method returns {@code null}.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     */
    public static byte[] parseInetAddressToBytes(String address) {
        byte[] res = parseInet4AddressToBytes(address);
        if (res == null) res = parseInet6AddressToBytes(address);
        return res;
    }

    /**
     * Checks whether given String is a valid IPv6 address.
     *
     * @param address address textual representation
     * @return {@code true} if {@code address} is a valid IPv6 address, {@code false} otherwise
     */
    public static boolean isInet6Address(String address) {
        return parseInet6AddressToBytes(address) != null;
    }

    /**
     * Parse an IPv6 address into an {@code Inet6Address} object.
     *
     * @param string the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     */
    public static Inet6Address parseInet6Address(String string) {
        final byte[] bytes = parseInet6AddressToBytes(string);
        if (bytes == null) {
            return null;
        }
        int scopeId = 0;
        Inet6Address address;
        try {
            address = Inet6Address.getByAddress(string, bytes, 0);
        } catch (UnknownHostException e) {
            // not possible
            throw new IllegalStateException(e);
        }
        final int pctIdx = string.indexOf('%');
        if (pctIdx != -1) {
            scopeId = getScopeId(string.substring(pctIdx + 1), address);
            if (scopeId == 0) {
                // address not valid after all...
                return null;
            }
            try {
                address = Inet6Address.getByAddress(string, bytes, scopeId);
            } catch (UnknownHostException e) {
                // not possible
                throw new IllegalStateException(e);
            }
        }
        return address;
    }

    /**
     * Checks whether given String is a valid IPv4 address.
     *
     * @param address address textual representation
     * @return {@code true} if {@code address} is a valid IPv4 address, {@code false} otherwise
     */
    public static boolean isInet4Address(String address) {
        return parseInet4AddressToBytes(address) != null;
    }

    /**
     * Parse an IPv4 address into an {@code Inet4Address} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     */
    public static Inet4Address parseInet4Address(String address) {
        final byte[] bytes = parseInet4AddressToBytes(address);
        if (bytes == null) {
            return null;
        }
        try {
            return (Inet4Address) Inet4Address.getByAddress(address, bytes);
        } catch (UnknownHostException e) {
            // not possible
            throw new IllegalStateException(e);
        }
    }

    /**
     * Parse an IP address into an {@code InetAddress} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     */
    public static InetAddress parseInetAddress(String address) {
        // simple heuristic
        if (address.indexOf(':') != -1) {
            return parseInet6Address(address);
        } else {
            return parseInet4Address(address);
        }
    }

    /**
     * Converts IPv6 address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IPv6 address, the method returns {@code null}.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     */
    public static byte[] parseInet6AddressToBytes(String address) {
        if (address == null || address.isEmpty()) {
            return null;
        }

        // remove brackets if present
        if (address.startsWith("[") && address.endsWith("]")) {
            address = address.substring(1, address.length() - 1);
        }

        final int pctIdx = address.indexOf('%');
        if (pctIdx != -1) {
            address = address.substring(0, pctIdx);
        }

        String[] segments = address.split(":", 10);

        // there can be minimum of 2 and maximum of 8 colons, which makes 3 respectively 9 segments
        if (segments.length > 9 || segments.length < 3) {
            return null;
        }
        // if the first segment is empty, the second one must be too - "::<address end>"
        if (segments[0].length() == 0 && segments[1].length() != 0) {
            return null;
        }
        // if the last segment is empty, the segment before it must be too - "<address beginning>::"
        if (segments[segments.length - 1].length() == 0 && segments[segments.length - 2].length() != 0) {
            return null;
        }

        // validate segments
        for (int i = 0; i < segments.length; i++) {
            for (int charIdx = 0; charIdx < segments[i].length(); charIdx++) {
                char c = segments[i].charAt(charIdx);
                if (c == '.' && i != segments.length - 1) {
                    return null; // "." is allowed in the last segment only
                } else if (c != '.' && c != ':' && Character.digit(c, 16) == -1) {
                    return null; // not ".", ":" or a digit
                }
            }
        }

        // look for an empty segment - "::"
        int emptyIndex = -1;
        for (int i = 0; i < segments.length - 1; i++) {
            if (segments[i].length() == 0) {
                if (emptyIndex > 0) {
                    return null; // more than one occurrence of "::", invalid address
                } else if (emptyIndex != 0) { // don't rewrite skipIndex=0, when address starts with "::"
                    emptyIndex = i;
                }
            }
        }

        boolean containsIPv4 = segments[segments.length - 1].contains(".");
        int totalSegments = containsIPv4 ? 7 : 8; // if the last segment contains IPv4 notation ("::ffff:192.0.0.1"), the address only has 7 segments
        if (emptyIndex == -1 && segments.length != totalSegments) {
            return null; // no substitution but incorrect number of segments
        }

        int skipIndex;
        int skippedSegments;
        boolean isDefaultRoute = segments.length == 3
                && segments[0].isEmpty() && segments[1].isEmpty() && segments[2].isEmpty(); // is address just "::"?
        if (isDefaultRoute) {
            skipIndex = 0;
            skippedSegments = 8;
        } else if (segments[0].isEmpty() || segments[segments.length - 1].isEmpty()) {
            // "::" is at the beginning or end of the address
            skipIndex = emptyIndex;
            skippedSegments = totalSegments - segments.length + 2;
        } else if (emptyIndex > -1) {
            // "::" somewhere in the middle
            skipIndex = emptyIndex;
            skippedSegments = totalSegments - segments.length + 1;
        } else {
            // no substitution
            skipIndex = 0;
            skippedSegments = 0;
        }

        ByteBuffer bytes = ByteBuffer.allocate(16);

        try {
            // convert segments before "::"
            for (int i = 0; i < skipIndex; i++) {
                bytes.putShort(parseHexadecimal(segments[i]));
            }
            // fill "0" characters into expanded segments
            for (int i = skipIndex; i < skipIndex + skippedSegments; i++) {
                bytes.putShort((short) 0);
            }
            // convert segments after "::"
            for (int i = skipIndex + skippedSegments; i < totalSegments; i++) {
                int segmentIdx = segments.length - (totalSegments - i);
                if (containsIPv4 && i == totalSegments - 1) {
                    // we are at the last segment and it contains IPv4 address
                    String[] ipV4Segments = segments[segmentIdx].split("\\.");
                    if (ipV4Segments.length != 4) {
                        return null; // incorrect number of segments in IPv4
                    }
                    for (int idxV4 = 0; idxV4 < 4; idxV4++) {
                        bytes.put(parseDecimal(ipV4Segments[idxV4]));
                    }
                } else {
                    bytes.putShort(parseHexadecimal(segments[segmentIdx]));
                }
            }

            return bytes.array();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts IPv4 address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IPv4 address, the method returns {@code null}.
     * <p>
     * This only supports decimal notation.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     */
    public static byte[] parseInet4AddressToBytes(String address) {
        String[] segments = address.split("\\.", 5);
        if (segments.length != 4) {
            return null; // require 4 segments
        }
        // validate segments
        for (int i = 0; i < segments.length; i++) {
            if (segments[i].length() < 1) {
                return null; // empty segment
            }
            for (int cidx = 0; cidx < segments[i].length(); cidx++) {
                if (Character.digit(segments[i].charAt(cidx), 10) < 0) {
                    return null; // not a digit
                }
            }
        }

        byte[] bytes = new byte[4];
        try {
            for (int i = 0; i < segments.length; i++) {
                bytes[i] = parseDecimal(segments[i]);
            }
            return bytes;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Converts an IP address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IP address, the method returns {@code null}.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     */
    public static byte[] parseInetAddressToBytes(String address) {
        // simple heuristic
        if (address.indexOf(':') != -1) {
            return parseInet6AddressToBytes(address);
        } else {
            return parseInet4AddressToBytes(address);
        }
    }

    /**
     * Get the scope ID of the given address (if it is an IPv6 address).
     *
     * @return the scope ID, or 0 if there is none or the address is an IPv4 address
     */
    public static int getScopeId(InetAddress address) {
        return address instanceof Inet6Address ? ((Inet6Address) address).getScopeId() : 0;
    }

    private static final Pattern NUMERIC = Pattern.compile("\\d+");

    /**
     * Attempt to get the scope ID of the given string.  If the string is numeric then the number is parsed
     * and returned as-is.  If the scope is a string, then a search for the matching network interface will occur.
     *
     * @param scopeName the scope number or name as a string (must not be {@code null})
     * @return the scope ID, or 0 if no matching scope could be found
     */
    public static int getScopeId(String scopeName) {
        return getScopeId(scopeName, null);
    }

    /**
     * Attempt to get the scope ID of the given string.  If the string is numeric then the number is parsed
     * and returned as-is.  If the scope is a string, then a search for the matching network interface will occur.
     *
     * @param scopeName the scope number or name as a string (must not be {@code null})
     * @param compareWith the address to compare with, to ensure that the wrong local scope is not selected (may be {@code null})
     * @return the scope ID, or 0 if no matching scope could be found
     */
    public static int getScopeId(String scopeName, InetAddress compareWith) {
        Assert.checkNotNullParam("scopeName", scopeName);
        if (NUMERIC.matcher(scopeName).matches()) try {
            return Integer.parseInt(scopeName);
        } catch (NumberFormatException ignored) {
            return 0;
        }
        final NetworkInterface ni = findInterfaceWithScopeId(scopeName);
        if (ni == null) return 0;
        return getScopeId(ni, compareWith);
    }

    public static NetworkInterface findInterfaceWithScopeId(String scopeName) {
        final Enumeration<NetworkInterface> enumeration;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException ignored) {
            return null;
        }
        while (enumeration.hasMoreElements()) {
            final NetworkInterface net = enumeration.nextElement();
            if (net.getName().equals(scopeName)) {
                return net;
            }
        }
        return null;
    }

    public static int getScopeId(NetworkInterface networkInterface) {
        return getScopeId(networkInterface, null);
    }

    public static int getScopeId(NetworkInterface networkInterface, InetAddress compareWith) {
        Assert.checkNotNullParam("networkInterface", networkInterface);
        Inet6Address cw6 = compareWith instanceof Inet6Address ? (Inet6Address) compareWith : null;
        Inet6Address address = doPrivileged((PrivilegedAction<Inet6Address>) () -> {
            final Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                final InetAddress a = addresses.nextElement();
                if (a instanceof Inet6Address) {
                    final Inet6Address a6 = (Inet6Address) a;
                    if (cw6 == null ||
                        a6.isLinkLocalAddress() == cw6.isLinkLocalAddress() &&
                        a6.isSiteLocalAddress() == cw6.isSiteLocalAddress()
                    ) {
                        return a6;
                    }
                }
            }
            return null;
        });
        return address == null ? 0 : address.getScopeId();
    }

    private static byte parseDecimal(String number) {
        int i = Integer.parseInt(number);
        if (i < 0 || i > 255) {
            throw new NumberFormatException();
        }
        return (byte) i;
    }

    private static short parseHexadecimal(String hexNumber) {
        int i = Integer.parseInt(hexNumber, 16);
        if (i > 0xffff) {
            throw new NumberFormatException();
        }
        return (short) i;
    }

    private static String toOptimalStringV6(final byte[] bytes) {
        final int[] segments = new int[8];
        for (int i = 0; i < 8; i ++) {
            segments[i] = (bytes[i << 1] & 0xff) << 8 | bytes[(i << 1) + 1] & 0xff;
        }
        // now loop through the segments and add them as optimally as possible
        final StringBuilder b = new StringBuilder();
        for (int i = 0; i < 8; i ++) {
            if (segments[i] == 0) {
                if (i == 7) {
                    b.append('0');
                } else {
                    // possible to collapse it
                    i++;
                    if (segments[i] == 0) {
                        // yup
                        b.append(':').append(':');
                        for (i++; i < 8; i++) {
                            if (segments[i] == 0xffff && b.length() == 2) {
                                b.append("ffff");
                                if (i == 5) {
                                    // it's an IPv4 compat address.
                                    b.append(':').append(bytes[12] & 0xff).append('.').append(bytes[13] & 0xff).append('.').append(bytes[14] & 0xff).append('.').append(bytes[15] & 0xff);
                                    i = 8;
                                } else if (i == 4 && segments[5] == 0) {
                                    // it's a SIIT address.
                                    b.append(":0:").append(bytes[12] & 0xff).append('.').append(bytes[13] & 0xff).append('.').append(bytes[14] & 0xff).append('.').append(bytes[15] & 0xff);
                                    i = 8;
                                } else {
                                    // finally break and do the rest normally
                                    for (i++; i < 8; i++) {
                                        b.append(':').append(Integer.toHexString(segments[i]));
                                    }
                                }
                            } else if (segments[i] != 0) {
                                // finally break and do the rest normally
                                b.append(Integer.toHexString(segments[i]));
                                for (i++; i < 8; i++) {
                                    b.append(':').append(Integer.toHexString(segments[i]));
                                }
                            }
                        }
                    } else {
                        // no, just a single 0 in isolation doesn't get collapsed
                        if (i > 1) b.append(':');
                        b.append('0').append(':').append(Integer.toHexString(segments[i]));
                    }
                }
            } else {
                if (i > 0) b.append(':');
                b.append(Integer.toHexString(segments[i]));
            }
        }
        return b.toString();
    }
}
