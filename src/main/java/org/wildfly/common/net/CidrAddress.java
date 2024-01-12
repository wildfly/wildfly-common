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

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * A Classless Inter-Domain Routing address.  This is the combination of an IP address and a netmask.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 * @deprecated Use {@link io.smallrye.common.net.CidrAddress} instead.
 */
@Deprecated(forRemoval = true)
public final class CidrAddress implements Serializable, Comparable<CidrAddress> {
    private static final long serialVersionUID = - 6548529324373774149L;

    /**
     * The CIDR address representing all IPv4 addresses.
     *
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#INET4_ANY_CIDR} instead.
     */
    @Deprecated(forRemoval = true)
    public static final CidrAddress INET4_ANY_CIDR = new CidrAddress(io.smallrye.common.net.CidrAddress.INET4_ANY_CIDR);

    /**
     * The CIDR address representing all IPv6 addresses.
     *
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#INET6_ANY_CIDR} instead.
     */
    @Deprecated(forRemoval = true)
    public static final CidrAddress INET6_ANY_CIDR = new CidrAddress(io.smallrye.common.net.CidrAddress.INET6_ANY_CIDR);

    final io.smallrye.common.net.CidrAddress cidrAddress;

    CidrAddress(final io.smallrye.common.net.CidrAddress cidrAddress) {
        this.cidrAddress = cidrAddress;
    }

    /**
     * Create a new CIDR address.
     *
     * @param networkAddress the network address (must not be {@code null})
     * @param netmaskBits the netmask bits (0-32 for IPv4, or 0-128 for IPv6)
     * @return the CIDR address (not {@code null})
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#create(InetAddress, int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static CidrAddress create(InetAddress networkAddress, int netmaskBits) {
        return new CidrAddress(io.smallrye.common.net.CidrAddress.create(networkAddress, netmaskBits));
    }

    /**
     * Create a new CIDR address.
     *
     * @param addressBytes the network address bytes (must not be {@code null}, must be 4 bytes for IPv4 or 16 bytes for IPv6)
     * @param netmaskBits the netmask bits (0-32 for IPv4, or 0-128 for IPv6)
     * @return the CIDR address (not {@code null})
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#create(byte[], int)} instead.
     */
    @Deprecated(forRemoval = true)
    public static CidrAddress create(byte[] addressBytes, int netmaskBits) {
        return new CidrAddress(io.smallrye.common.net.CidrAddress.create(addressBytes, netmaskBits));
    }

    /**
     * Determine if this CIDR address matches the given address.
     *
     * @param address the address to test
     * @return {@code true} if the address matches, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(InetAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(InetAddress address) {
        return cidrAddress.matches(address);
    }

    /**
     * Determine if this CIDR address matches the given address bytes.
     *
     * @param bytes the address bytes to test
     * @return {@code true} if the address bytes match, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(byte[])} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(byte[] bytes) {
        return cidrAddress.matches(bytes);
    }

    /**
     * Determine if this CIDR address matches the given address bytes.
     *
     * @param bytes the address bytes to test
     * @param scopeId the scope ID, or 0 to match no scope
     * @return {@code true} if the address bytes match, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(byte[], int)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(byte[] bytes, int scopeId) {
        return cidrAddress.matches(bytes, scopeId);
    }

    /**
     * Determine if this CIDR address matches the given address.
     *
     * @param address the address to test
     * @return {@code true} if the address matches, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(Inet4Address)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(Inet4Address address) {
        return cidrAddress.matches(address);
    }

    /**
     * Determine if this CIDR address matches the given address.
     *
     * @param address the address to test
     * @return {@code true} if the address matches, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(Inet6Address)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(Inet6Address address) {
        return cidrAddress.matches(address);
    }

    /**
     * Determine if this CIDR address matches the given CIDR address.  This will be true only when the given CIDR
     * block is wholly enclosed by this one.
     *
     * @param address the address to test
     * @return {@code true} if the given block is enclosed by this one, {@code false} otherwise
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#matches(io.smallrye.common.net.CidrAddress)} instead.
     */
    @Deprecated(forRemoval = true)
    public boolean matches(CidrAddress address) {
        return cidrAddress.matches(address.cidrAddress);
    }

    /**
     * Get the network address.  The returned address has a resolved name consisting of the most compact valid string
     * representation of the network of this CIDR address.
     *
     * @return the network address (not {@code null})
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#getNetworkAddress()} instead.
     */
    @Deprecated(forRemoval = true)
    public InetAddress getNetworkAddress() {
        return cidrAddress.getNetworkAddress();
    }

    /**
     * Get the broadcast address for this CIDR block.  If the block has no broadcast address (either because it is IPv6
     * or it is too small) then {@code null} is returned.
     *
     * @return the broadcast address for this CIDR block, or {@code null} if there is none
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#getBroadcastAddress()} instead.
     */
    @Deprecated(forRemoval = true)
    public Inet4Address getBroadcastAddress() {
        return cidrAddress.getBroadcastAddress();
    }

    /**
     * Get the netmask bits.  This will be in the range 0-32 for IPv4 addresses, and 0-128 for IPv6 addresses.
     *
     * @return the netmask bits
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#getNetmaskBits()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getNetmaskBits() {
        return cidrAddress.getNetmaskBits();
    }

    /**
     * Get the match address scope ID (if it is an IPv6 address).
     *
     * @return the scope ID, or 0 if there is none or the address is an IPv4 address
     * @deprecated Use {@link io.smallrye.common.net.CidrAddress#getScopeId()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getScopeId() {
        return cidrAddress.getScopeId();
    }

    public int compareTo(final CidrAddress other) {
        return cidrAddress.compareTo(other.cidrAddress);
    }

    public int compareAddressBytesTo(final byte[] otherBytes, final int otherNetmaskBits, final int scopeId) {
        return cidrAddress.compareAddressBytesTo(otherBytes, otherNetmaskBits, scopeId);
    }

    public boolean equals(final Object obj) {
        return obj instanceof CidrAddress && equals((CidrAddress) obj);
    }

    public boolean equals(final CidrAddress obj) {
        return obj == this || obj != null && cidrAddress.equals(obj.cidrAddress);
    }

    public int hashCode() {
        return cidrAddress.hashCode();
    }

    public String toString() {
        return cidrAddress.toString();
    }

    Object writeReplace() {
        return new Ser(cidrAddress.getNetworkAddress().getAddress(), getNetmaskBits());
    }

    static final class Ser implements Serializable {
        private static final long serialVersionUID = 6367919693596329038L;

        final byte[] b;
        final int m;

        Ser(final byte[] b, final int m) {
            this.b = b;
            this.m = m;
        }

        Object readResolve() {
            return create(b, m);
        }
    }
}
