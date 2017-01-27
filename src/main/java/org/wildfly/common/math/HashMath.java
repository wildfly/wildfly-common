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

package org.wildfly.common.math;

/**
 * Routines which are useful for hashcode computation, among other things.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class HashMath {

    private static final int PRESELECTED_PRIME = 1299827;

    private HashMath() {
    }

    /**
     * A hash function which combines an accumulated hash with a next hash such that {@code f(f(k, p2, b), p1, a) ≠ₙ f(f(k, p1, a), p2, b)}.
     * This function is suitable for object chains whose order affects the overall equality of the hash code.
     * <p>
     * The exact algorithm is not specified and is therefore subject to change and should not be relied upon for hash
     * codes that persist outside of the JVM process.
     *
     * @param accumulatedHash the accumulated hash code of the previous stage
     * @param prime a prime multiplier
     * @param nextHash the hash code of the next single item
     * @return the new accumulated hash code
     */
    public static int multiHashOrdered(int accumulatedHash, int prime, int nextHash) {
        return multiplyWrap(accumulatedHash, prime) + nextHash;
    }

    /**
     * A hash function which combines an accumulated hash with a next hash such that {@code f(f(k, p2, b), p1, a) = f(f(k, p1, a), p2, b)}.
     * This function is suitable for object chains whose order does not affect the overall equality of the hash code.
     * <p>
     * The exact algorithm is not specified and is therefore subject to change and should not be relied upon for hash
     * codes that persist outside of the JVM process.
     *
     * @param accumulatedHash the accumulated hash code of the previous stage
     * @param prime a prime multiplier
     * @param nextHash the hash code of the next single item
     * @return the new accumulated hash code
     */
    public static int multiHashUnordered(int accumulatedHash, int prime, int nextHash) {
        return multiplyWrap(nextHash, prime) + accumulatedHash;
    }

    /**
     * A hash function which combines an accumulated hash with a next hash such that {@code f(f(k, b), a) ≠ₙ f(f(k, a), b)}.
     * This function is suitable for object chains whose order affects the overall equality of the hash code.
     * <p>
     * The exact algorithm is not specified and is therefore subject to change and should not be relied upon for hash
     * codes that persist outside of the JVM process.
     *
     * @param accumulatedHash the accumulated hash code of the previous stage
     * @param nextHash the hash code of the next single item
     * @return the new accumulated hash code
     */
    public static int multiHashOrdered(int accumulatedHash, int nextHash) {
        return multiHashOrdered(accumulatedHash, PRESELECTED_PRIME, nextHash);
    }

    /**
     * A hash function which combines an accumulated hash with a next hash such that {@code f(f(k, b), a) = f(f(k, a), b)}.
     * This function is suitable for object chains whose order does not affect the overall equality of the hash code.
     * <p>
     * The exact algorithm is not specified and is therefore subject to change and should not be relied upon for hash
     * codes that persist outside of the JVM process.
     *
     * @param accumulatedHash the accumulated hash code of the previous stage
     * @param nextHash the hash code of the next single item
     * @return the new accumulated hash code
     */
    public static int multiHashUnordered(int accumulatedHash, int nextHash) {
        return multiHashUnordered(accumulatedHash, PRESELECTED_PRIME, nextHash);
    }

    /**
     * Multiply two unsigned integers together.  If the result overflows a 32-bit number, XOR the overflowed bits back into the result.
     * This operation is commutative, i.e. if we designate the {@code ⨰} symbol to represent this operation, then {@code a ⨰ b = b ⨰ a}.
     * This operation is <em>not</em> associative, i.e. {@code (a ⨰ b) ⨰ c ≠ₙ a ⨰ (b ⨰ c)} (the symbol {@code ≠ₙ} meaning "not necessarily equal to"),
     * therefore this operation is suitable for ordered combinatorial hash functions.
     *
     * @param a the first number to multiply
     * @param b the second number to multiply
     * @return the wrapped multiply result
     */
    public static int multiplyWrap(int a, int b) {
        long r1 = (long) a * b;
        return (int) r1 ^ (int) (r1 >>> 32);
    }
}
