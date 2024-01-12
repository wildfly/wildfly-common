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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ProtocolFamily;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

/**
 * Utilities relating to Internet protocol (a.k.a. "INET" or "IP") address manipulation.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 *
 * @deprecated Use {@link io.smallrye.common.net.Inet} instead.
 */
@Deprecated(forRemoval = true)
public final class Inet {
    private Inet() {}

    /**
     * The "any" address for IPv4.
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#INET4_ANY} instead.
     */
    @Deprecated(forRemoval = true)
    public static final Inet4Address INET4_ANY = io.smallrye.common.net.Inet.INET4_ANY;

    /**
     * The traditional loopback address for IPv4.
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#INET4_LOOPBACK} instead.
     */
    @Deprecated(forRemoval = true)
    public static final Inet4Address INET4_LOOPBACK = io.smallrye.common.net.Inet.INET4_LOOPBACK;

    /**
     * The broadcast-all address for IPv4.
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#INET4_BROADCAST} instead.
     */
    @Deprecated(forRemoval = true)
    public static final Inet4Address INET4_BROADCAST = io.smallrye.common.net.Inet.INET4_BROADCAST;

    /**
     * The "any" address for IPv6.
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#INET6_ANY} instead.
     */
    @Deprecated(forRemoval = true)
    public static final Inet6Address INET6_ANY = io.smallrye.common.net.Inet.INET6_ANY;

    /**
     * The loopback address for IPv6.
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#INET6_LOOPBACK} instead.
     */
    @Deprecated(forRemoval = true)
    public static final Inet6Address INET6_LOOPBACK = io.smallrye.common.net.Inet.INET6_LOOPBACK;

    /**
     * Get the optimal string representation of an IP address.  For IPv6 addresses, this representation will be
     * more compact that the default.
     *
     * @param inetAddress the address (must not be {@code null})
     * @return the string representation (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#toOptimalString(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static String toOptimalString(InetAddress inetAddress) {
        return io.smallrye.common.net.Inet.toOptimalString(inetAddress);
    }

    /**
     * Get the optimal string representation of the bytes of an IP address.
     *
     * @param addressBytes the address bytes (must not be {@code null})
     * @return the string representation (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#toOptimalString(byte[])} instead.
     */
    @Deprecated(forRemoval = true)
    public static String toOptimalString(byte[] addressBytes) {
        return io.smallrye.common.net.Inet.toOptimalString(addressBytes);
    }

    /**
     * Get a string representation of the given address which is suitable for use as the host component of a URL.
     *
     * @param inetAddress the address (must not be {@code null})
     * @param useHostNameIfPresent {@code true} to preserve the host name string in the address, {@code false} to always give
     *     an IP address string
     * @return the string representation (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#toURLString(InetAddress, boolean)} instead.
     */
    @Deprecated(forRemoval = true)
    public static String toURLString(InetAddress inetAddress, boolean useHostNameIfPresent) {
        return io.smallrye.common.net.Inet.toURLString(inetAddress, useHostNameIfPresent);
    }

    /**
     * Get a string representation of the given address bytes which is suitable for use as the host component of a URL.
     *
     * @param addressBytes the address bytes (must not be {@code null})
     * @return the string representation (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#toURLString(byte[])} instead.
     */
    @Deprecated(forRemoval = true)
    public static String toURLString(byte[] addressBytes) {
        return io.smallrye.common.net.Inet.toURLString(addressBytes);
    }

    /**
     * Get the IPv6 equivalent of the given address.  If the address is IPv4 then it is returned as a compatibility
     * address.
     *
     * @param inetAddress the address to convert (must not be {@code null})
     * @return the converted address (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#toInet6Address(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address toInet6Address(InetAddress inetAddress) {
        return io.smallrye.common.net.Inet.toInet6Address(inetAddress);
    }

    /**
     * Get the host name of the given address, if it is resolved.  Otherwise, return {@code null}.
     *
     * @param inetAddress the address to check (must not be {@code null})
     * @return the host name, or {@code null} if the address has no host name and is unresolved
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getHostNameIfResolved(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getHostNameIfResolved(InetAddress inetAddress) {
        return io.smallrye.common.net.Inet.getHostNameIfResolved(inetAddress);
    }

    /**
     * Get the host name of the given address, if it is resolved.  Otherwise, return {@code null}.
     *
     * @param socketAddress the socket address to check (must not be {@code null})
     * @return the host name, or {@code null} if the address has no host name and is unresolved
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getHostNameIfResolved(InetSocketAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static String getHostNameIfResolved(InetSocketAddress socketAddress) {
        return io.smallrye.common.net.Inet.getHostNameIfResolved(socketAddress);
    }

    /**
     * Get a resolved socket address from the given URI.
     *
     * @param uri the URI (must not be {@code null})
     * @param defaultPort the default port to use if none is given (must be in the range {@code 1 ≤ n ≤ 65535}
     * @param addressType the class of the {@code InetAddress} to search for (must not be {@code null})
     * @return the socket address, or {@code null} if the URI does not have a host component
     * @throws UnknownHostException if address resolution failed
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolved(URI, int, Class)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetSocketAddress getResolved(URI uri, int defaultPort, Class<? extends InetAddress> addressType) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolved(uri, defaultPort, addressType);
    }

    /**
     * Get the resolved socket address from the given URI.
     *
     * @param uri the URI (must not be {@code null})
     * @param defaultPort the default port to use if none is given (must be in the range {@code 1 ≤ n ≤ 65535}
     * @return the socket address, or {@code null} if the URI does not have a host component
     * @throws UnknownHostException if address resolution failed
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolved(URI, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetSocketAddress getResolved(URI uri, int defaultPort) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolved(uri, defaultPort);
    }

    /**
     * Get an Internet address for a URI destination, resolving the host name if necessary.
     *
     * @param uri the destination URI
     * @param <T> the type of the {@code InetAddress} to search for
     * @return the address, or {@code null} if no authority is present in the URI
     * @throws UnknownHostException if the URI host was existent but could not be resolved to a valid address
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolvedInetAddress(URI, Class)} instead.
     */
    @Deprecated(forRemoval = true)
    public static <T extends InetAddress> T getResolvedInetAddress(URI uri, Class<T> addressType) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolvedInetAddress(uri, addressType);
    }

    /**
     * Get an Internet address for a URI destination, resolving the host name if necessary.
     *
     * @param uri the destination URI
     * @return the address, or {@code null} if no authority is present in the URI
     * @throws UnknownHostException if the URI host was existent but could not be resolved to a valid address
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolvedInetAddress(URI)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetAddress getResolvedInetAddress(URI uri) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolvedInetAddress(uri);
    }

    /**
     * Get a copy of the given socket address, but with a resolved address component.
     *
     * @param address the (possibly unresolved) address (must not be {@code null})
     * @return the resolved address (not {@code null})
     * @throws UnknownHostException if address resolution failed
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolved(InetSocketAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetSocketAddress getResolved(InetSocketAddress address) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolved(address);
    }

    /**
     * Get a copy of the given socket address, but with a resolved address component of the given type.
     *
     * @param address the (possibly unresolved) address (must not be {@code null})
     * @param addressType the class of the {@code InetAddress} to search for (must not be {@code null})
     * @return the resolved address (not {@code null})
     * @throws UnknownHostException if address resolution failed, or if no addresses of the given type were found, or
     *     if the given address was already resolved but is not of the given address type
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getResolved(InetSocketAddress, Class)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetSocketAddress getResolved(InetSocketAddress address, Class<? extends InetAddress> addressType) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getResolved(address, addressType);
    }

    /**
     * Resolve the given host name, returning the first answer with the given address type.
     *
     * @param hostName the host name to resolve (must not be {@code null})
     * @param addressType the class of the {@code InetAddress} to search for (must not be {@code null})
     * @param <T> the type of the {@code InetAddress} to search for
     * @return the resolved address (not {@code null})
     * @throws UnknownHostException if address resolution failed or if no addresses of the given type were found
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getAddressByNameAndType(String, Class)} instead.
     */
    @Deprecated(forRemoval = true)
    public static <T extends InetAddress> T getAddressByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getAddressByNameAndType(hostName, addressType);
    }

    /**
     * Resolve the given host name, returning all answers with the given address type.
     *
     * @param hostName the host name to resolve (must not be {@code null})
     * @param addressType the class of the {@code InetAddress} to search for (must not be {@code null})
     * @param <T> the type of the {@code InetAddress} to search for
     * @return the resolved addresses (not {@code null})
     * @throws UnknownHostException if address resolution failed or if no addresses of the given type were found
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getAllAddressesByNameAndType(String, Class)} instead.
     */
    @Deprecated(forRemoval = true)
    public static <T extends InetAddress> T[] getAllAddressesByNameAndType(String hostName, Class<T> addressType) throws UnknownHostException {
        return io.smallrye.common.net.Inet.getAllAddressesByNameAndType(hostName, addressType);
    }

    /**
     * Get an IPv4 address from four integer segments.  Each segment must be between 0 and 255.
     *
     * @param s1 the first segment
     * @param s2 the second segment
     * @param s3 the third segment
     * @param s4 the fourth segment
     * @return the address (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getInet4Address(int, int, int, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet4Address getInet4Address(int s1, int s2, int s3, int s4) {
        return io.smallrye.common.net.Inet.getInet4Address(s1, s2, s3, s4);
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
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getInet6Address(int, int, int, int, int, int, int, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address getInet6Address(int s1, int s2, int s3, int s4, int s5, int s6, int s7, int s8) {
        return io.smallrye.common.net.Inet.getInet6Address(s1, s2, s3, s4, s5, s6, s7, s8);
    }

    /**
     * Checks whether given String is a valid IPv6 address.
     *
     * @param address address textual representation
     * @return {@code true} if {@code address} is a valid IPv6 address, {@code false} otherwise
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#isInet6Address(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static boolean isInet6Address(String address) {
        return io.smallrye.common.net.Inet.isInet6Address(address);
    }

    /**
     * Parse an IPv6 address into an {@code Inet6Address} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet6Address(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address parseInet6Address(String address) {
        return io.smallrye.common.net.Inet.parseInet6Address(address);
    }

    /**
     * Parse an IPv6 address into an {@code Inet6Address} object.
     *
     * @param address the address to parse (must not be {@code null})
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet6Address(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address parseInet6Address(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInet6Address(address, hostName);
    }

    /**
     * Parse an IPv6 address into an {@code Inet6Address} object, throwing an exception on failure.
     *
     * @param address the address to parse
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet6AddressOrFail(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address parseInet6AddressOrFail(String address) {
        return io.smallrye.common.net.Inet.parseInet6AddressOrFail(address);
    }

    /**
     * Parse an IPv6 address into an {@code Inet6Address} object.
     *
     * @param address the address to parse (must not be {@code null})
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet6AddressOrFail(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet6Address parseInet6AddressOrFail(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInet6AddressOrFail(address, hostName);
    }

    /**
     * Checks whether given String is a valid IPv4 address.
     *
     * @param address address textual representation
     * @return {@code true} if {@code address} is a valid IPv4 address, {@code false} otherwise
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#isInet4Address(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static boolean isInet4Address(String address) {
        return io.smallrye.common.net.Inet.isInet4Address(address);
    }

    /**
     * Parse an IPv4 address into an {@code Inet4Address} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet4Address(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet4Address parseInet4Address(String address) {
        return io.smallrye.common.net.Inet.parseInet4Address(address);
    }

    /**
     * Parse an IPv4 address into an {@code Inet4Address} object.
     *
     * @param address the address to parse
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet4Address(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet4Address parseInet4Address(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInet4Address(address, hostName);
    }

    /**
     * Parse an IPv4 address into an {@code Inet4Address} object, throwing an exception on failure.
     *
     * @param address the address to parse
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet4AddressOrFail(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet4Address parseInet4AddressOrFail(String address) {
        return io.smallrye.common.net.Inet.parseInet4AddressOrFail(address);
    }

    /**
     * Parse an IPv4 address into an {@code Inet4Address} object.
     *
     * @param address the address to parse (must not be {@code null})
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet4AddressOrFail(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static Inet4Address parseInet4AddressOrFail(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInet4AddressOrFail(address, hostName);
    }

    /**
     * Parse an IP address into an {@code InetAddress} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInetAddress(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetAddress parseInetAddress(String address) {
        return io.smallrye.common.net.Inet.parseInetAddress(address);
    }

    /**
     * Parse an IP address into an {@code InetAddress} object.
     *
     * @param address the address to parse
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInetAddress(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetAddress parseInetAddress(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInetAddress(address, hostName);
    }

    /**
     * Parse an IP address into an {@code InetAddress} object, throwing an exception on failure.
     *
     * @param address the address to parse
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInetAddressOrFail(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetAddress parseInetAddressOrFail(String address) {
        return io.smallrye.common.net.Inet.parseInetAddressOrFail(address);
    }

    /**
     * Parse an IP address into an {@code InetAddress} object.
     *
     * @param address the address to parse (must not be {@code null})
     * @param hostName the host name to use in the resultant object, or {@code null} to use the string representation of
     *      the address
     * @return the parsed address (not {@code null})
     * @throws IllegalArgumentException if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInetAddressOrFail(String, String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static InetAddress parseInetAddressOrFail(String address, String hostName) {
        return io.smallrye.common.net.Inet.parseInetAddressOrFail(address, hostName);
    }

    /**
     * Parse a CIDR address into a {@code CidrAddress} object.
     *
     * @param address the address to parse
     * @return the parsed address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseCidrAddress(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static CidrAddress parseCidrAddress(String address) {
        return new CidrAddress(io.smallrye.common.net.Inet.parseCidrAddress(address));
    }

    /**
     * Converts IPv6 address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IPv6 address, the method returns {@code null}.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet6AddressToBytes(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static byte[] parseInet6AddressToBytes(String address) {
        return io.smallrye.common.net.Inet.parseInet6AddressToBytes(address);
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
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInet4AddressToBytes(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static byte[] parseInet4AddressToBytes(String address) {
        return io.smallrye.common.net.Inet.parseInet4AddressToBytes(address);
    }

    /**
     * Converts an IP address from textual representation to bytes.
     * <p>
     * If given string doesn't represent valid IP address, the method returns {@code null}.
     *
     * @param address address textual representation
     * @return byte array representing the address, or {@code null} if the address is not valid
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#parseInetAddressToBytes(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static byte[] parseInetAddressToBytes(String address) {
        return io.smallrye.common.net.Inet.parseInetAddressToBytes(address);
    }

    /**
     * Get the scope ID of the given address (if it is an IPv6 address).
     *
     * @return the scope ID, or 0 if there is none or the address is an IPv4 address
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getScopeId(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getScopeId(InetAddress address) {
        return io.smallrye.common.net.Inet.getScopeId(address);
    }

    /**
     * Attempt to get the scope ID of the given string.  If the string is numeric then the number is parsed
     * and returned as-is.  If the scope is a string, then a search for the matching network interface will occur.
     *
     * @param scopeName the scope number or name as a string (must not be {@code null})
     * @return the scope ID, or 0 if no matching scope could be found
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getScopeId(String)} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getScopeId(String scopeName) {
        return io.smallrye.common.net.Inet.getScopeId(scopeName);
    }

    /**
     * Attempt to get the scope ID of the given string.  If the string is numeric then the number is parsed
     * and returned as-is.  If the scope is a string, then a search for the matching network interface will occur.
     *
     * @param scopeName the scope number or name as a string (must not be {@code null})
     * @param compareWith the address to compare with, to ensure that the wrong local scope is not selected (may be {@code null})
     * @return the scope ID, or 0 if no matching scope could be found
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getScopeId(String, InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static int getScopeId(String scopeName, InetAddress compareWith) {
        return io.smallrye.common.net.Inet.getScopeId(scopeName, compareWith);
    }

    @Deprecated(forRemoval = true)
    public static NetworkInterface findInterfaceWithScopeId(String scopeName) {
        return io.smallrye.common.net.Inet.findInterfaceWithScopeId(scopeName);
    }

    @Deprecated(forRemoval = true)
    public static int getScopeId(NetworkInterface networkInterface) {
        return io.smallrye.common.net.Inet.getScopeId(networkInterface);
    }

    @Deprecated(forRemoval = true)
    public static int getScopeId(NetworkInterface networkInterface, InetAddress compareWith) {
        return io.smallrye.common.net.Inet.getScopeId(networkInterface, compareWith);
    }

    /**
     * Extract a base URI from the given scheme and socket address.  The address is not resolved.
     *
     * @param scheme the URI scheme
     * @param socketAddress the host socket address
     * @param defaultPort the protocol default port, or -1 if there is none
     * @return the URI instance
     * @throws URISyntaxException if the URI failed to be constructed for some reason
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getURIFromAddress(String, InetSocketAddress, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static URI getURIFromAddress(String scheme, InetSocketAddress socketAddress, int defaultPort) throws URISyntaxException {
        return io.smallrye.common.net.Inet.getURIFromAddress(scheme, socketAddress, defaultPort);
    }

    /**
     * Get the protocol family of the given Internet address.
     *
     * @param inetAddress the address (must not be {@code null})
     * @return the protocol family (not {@code null})
     *
     * @deprecated Use {@link io.smallrye.common.net.Inet#getProtocolFamily(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public static ProtocolFamily getProtocolFamily(InetAddress inetAddress) {
        return io.smallrye.common.net.Inet.getProtocolFamily(inetAddress);
    }
}
