package org.wildfly.common.flags;

import static java.lang.Integer.bitCount;
import static java.lang.Integer.highestOneBit;
import static java.lang.Integer.lowestOneBit;
import static java.lang.Integer.numberOfTrailingZeros;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Consumer;

import org.wildfly.common.Assert;

/**
 * A base class for implementing value-like flags and flag sets, where flags and flag sets may be used without allocation
 * overhead.
 * <p>
 */
public abstract class Flags<E extends Enum<E>, This extends Flags<E, This>> extends AbstractSet<E> implements SortedSet<E> {
    final int bits;

    /**
     * Construct a new instance.  This constructor should only be called during initial array construction.
     *
     * @param bits the bits of this set
     */
    protected Flags(final int bits) {
        this.bits = bits;
    }

    /**
     * Get the set value of the given bit combination.  The bit combination may contain extraneous one-bits so
     * any bits beyond the bit of the last flag should be masked off if an array is used for lookup.
     *
     * @param bits the bit combination (possibly with extra bits)
     * @return the set instance
     */
    protected abstract This value(int bits);

    /**
     * Return {@code this}.
     *
     * @return {@code this}
     */
    protected abstract This this_();

    /**
     * Get the flag item with the given index.
     *
     * @param index the index
     * @return the flag
     */
    protected abstract E itemOf(int index);

    /**
     * Get the item (cast to the correct {@code enum} type), or {@code null} if it is not of the correct type.
     *
     * @param obj the object to cast
     * @return the cast object, or {@code null}
     */
    protected abstract E castItemOrNull(Object obj);

    /**
     * Cast the given object to this class, throwing an exception if the cast fails.
     *
     * @param obj the object to cast
     * @return the cast object
     */
    protected abstract This castThis(Object obj);

    /**
     * Get the size of the flag set.
     *
     * @return the flag set size
     */
    public final int size() {
        return bitCount(bits);
    }

    /**
     * Get the first flag in the set.
     *
     * @return the first flag
     */
    public final E first() {
        final int bits = this.bits;
        if (bits == 0) throw new NoSuchElementException();
        return itemOf(numberOfTrailingZeros(lowestOneBit(bits)));
    }

    /**
     * Get the last flag in the set.
     *
     * @return the last flag
     */
    public final E last() {
        final int bits = this.bits;
        if (bits == 0) throw new NoSuchElementException();
        return itemOf(numberOfTrailingZeros(highestOneBit(bits)));
    }

    /**
     * Get the {@code null} comparator, indicating that this set is always sorted in natural order.
     *
     * @return {@code null}
     */
    public final Comparator<? super E> comparator() {
        return null;
    }

    /**
     * Determine if this flag set is empty.
     *
     * @return {@code true} if the flag set is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return bits == 0;
    }

    /**
     * Get the subset of flags from this set, up to (but not including) the given element.
     *
     * @param toElement the "to" element (must not be {@code null})
     * @return the subset
     */
    public final This headSet(final E toElement) {
        Assert.checkNotNullParam("toElement", toElement);
        return value(bits & bitOf(toElement) - 1);
    }

    /**
     * Get the subset of flags from this set, starting from the given element.
     *
     * @param fromElement the "from" element (must not be {@code null})
     * @return the subset
     */
    public final This tailSet(final E fromElement) {
        Assert.checkNotNullParam("fromElement", fromElement);
        return value(bits & ~(bitOf(fromElement) - 1));
    }

    /**
     * Get the subset of flags, starting from {@code fromElement} up to (but not including) {@code toElement}.
     *
     * @param fromElement the "from" element (must not be {@code null})
     * @param toElement the "to" element (must not be {@code null})
     * @return the subset
     */
    public final This subSet(final E fromElement, final E toElement) {
        Assert.checkNotNullParam("fromElement", fromElement);
        Assert.checkNotNullParam("toElement", toElement);
        return value(bits & (bitOf(toElement) - 1) & ~(bitOf(fromElement) - 1));
    }

    /**
     * Get an {@code Object} array containing all the flag values of this set.
     *
     * @return the {@code Object} array
     */
    public final Object[] toArray() {
        int b = bits;
        final Object[] array = new Object[bitCount(b)];
        int idx = 0;
        while (bitCount(b) > 0) {
            final int lob = lowestOneBit(b);
            array[idx + 1] = itemOf(numberOfTrailingZeros(lob));
            b ^= lob;
        }
        return array;
    }

    /**
     * Get a typed array containing all the flag values of this set.
     *
     * @param array the array to populate or clone
     * @param <T> the element type
     * @return the populated array
     */
    @SuppressWarnings("unchecked")
    public final <T> T[] toArray(T[] array) {
        int b = bits;
        final int size = bitCount(b);
        if (size > array.length) {
            array = Arrays.copyOf(array, size);
        }
        int idx = 0;
        while (bitCount(b) > 0) {
            final int lob = lowestOneBit(b);
            array[idx + 1] = (T) itemOf(numberOfTrailingZeros(lob));
            b ^= lob;
        }
        return array;
    }

    /**
     * Determine if this flag set contains the given flag.
     *
     * @param flag the flag
     * @return {@code true} if the flag is contained by this set
     */
    public final boolean contains(E flag) {
        return flag != null && (bits & bitOf(flag)) != 0;
    }

    /**
     * Determine if this flag set contains the given object.
     *
     * @param o the object
     * @return {@code true} if the object is contained by this set
     */
    public final boolean contains(final Object o) {
        return contains(castItemOrNull(o));
    }

    /**
     * Determine if this flag set contains all of the objects in the given collection.
     *
     * @param c the collection
     * @return {@code true} if all of the collection's objects are contained by this set
     */
    public final boolean containsAll(final Collection<?> c) {
        if (c.getClass() == getClass()) {
            return containsAll(castThis(c));
        } else {
            for (Object o : c) {
                if (! contains(o)) return false;
            }
            return true;
        }
    }

    /**
     * Determine if this flag set contains all of the flags in the given flag set.
     *
     * @param other the flag set
     * @return {@code true} if all of the given set's flags are contained by this set
     */
    public final boolean containsAll(This other) {
        final int otherBits = other.bits;
        return (this.bits & otherBits) == otherBits;
    }

    /**
     * Determine if this flag set contains all of the given flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @return {@code true} if all of the given flags are contained by this set
     */
    public final boolean containsAll(E flag1, E flag2) {
        return contains(flag1) && contains(flag2);
    }

    /**
     * Determine if this flag set contains all of the given flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @param flag3 the third flag
     * @return {@code true} if all of the given flags are contained by this set
     */
    public final boolean containsAll(E flag1, E flag2, E flag3) {
        return containsAll(flag1, flag2) && contains(flag3);
    }

    /**
     * Determine if this flag set contains any of the flags in the given flag set.
     *
     * @param other the flag set
     * @return {@code true} if all of the given set's flags are contained by this set
     */
    public final boolean containsAny(This other) {
        return other != null && (bits & other.bits) != 0;
    }

    /**
     * Determine if this flag set contains any of the given flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @return {@code true} if any of the given flags are contained by this set
     */
    public final boolean containsAny(E flag1, E flag2) {
        return contains(flag1) || contains(flag2);
    }

    /**
     * Determine if this flag set contains any of the given flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @param flag3 the third flag
     * @return {@code true} if any of the given flags are contained by this set
     */
    public final boolean containsAny(E flag1, E flag2, E flag3) {
        return containsAny(flag1, flag2) || contains(flag3);
    }

    /**
     * Get the complement of this set.
     *
     * @return the complement of this set
     */
    public final This complement() {
        return value(~bits);
    }

    /**
     * Return a set which includes all of the flags in this set and the given additional flag.
     *
     * @param flag the additional flag
     * @return the combined set
     */
    public final This with(E flag) {
        return flag == null ? this_() : value(bits | bitOf(flag));
    }

    /**
     * Return a set which includes all of the flags in this set and the given additional flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @return the combined set
     */
    public final This with(E flag1, E flag2) {
        return with(flag1).with(flag2);
    }

    /**
     * Return a set which includes all of the flags in this set and the given additional flags.
     *
     * @param flag1 the first flag
     * @param flag2 the second flag
     * @param flag3 the third flag
     * @return the combined set
     */
    public final This with(E flag1, E flag2, E flag3) {
        return with(flag1, flag2).with(flag3);
    }

    /**
     * Return a set which includes all of the flags in this set and the given additional flags.
     *
     * @param flags the additional flags
     * @return the combined set
     */
    @SafeVarargs
    public final This with(E... flags) {
        if (flags == null) return this_();
        int b = bits;
        for (E flag : flags) {
            if (flag != null) b |= bitOf(flag);
        }
        return value(b);
    }

    /**
     * Return a set which includes all of the flags in this set and the given additional flags.
     *
     * @param other the additional flags
     * @return the combined set
     */
    public final This with(This other) {
        return other == null ? this_() : value(bits | other.bits);
    }

    /**
     * Return a set which includes all of the flags except for the given flag.
     *
     * @param flag the flag
     * @return the reduced set
     */
    public final This without(E flag) {
        return flag == null ? this_() : value(bits & ~bitOf(flag));
    }

    /**
     * Return a set which includes all of the flags except for the given flags.
     *
     * @param other the flags
     * @return the reduced set
     */
    public final This without(This other) {
        return other == null ? this_() : value(bits & ~other.bits);
    }

    /**
     * Determine if this flag set is equal to the given object.
     *
     * @param o the other object
     * @return {@code true} if the object is equal to this set, {@code false} otherwise
     */
    public final boolean equals(final Object o) {
        return o == this || o instanceof Set && equals((Set<?>) o);
    }

    /**
     * Determine if this flag set is equal to the given set.
     *
     * @param o the other set
     * @return {@code true} if the set is equal to this set, {@code false} otherwise
     */
    public final boolean equals(final Set<?> o) {
        return o == this || o.containsAll(this) && containsAll(o);
    }

    /**
     * Determine if this flag set is equal to the given flag set.
     *
     * @param o the other flag set
     * @return {@code true} if the flag set is equal to this set, {@code false} otherwise
     */
    public final boolean equals(final This o) {
        return o == this;
    }

    /**
     * Get the hash code of this flag set.
     *
     * @return the flag set hash code
     */
    public final int hashCode() {
        int hc = 0;
        int b = this.bits;
        while (b != 0) {
            int lob = lowestOneBit(b);
            hc += itemOf(numberOfTrailingZeros(lob)).hashCode();
            b ^= lob;
        }
        return hc;
    }

    /**
     * Iterate this set in order from first to last flag.
     *
     * @return the iterator
     */
    public final Iterator<E> iterator() {
        return new Iterator<E>() {
            int b = bits;

            public boolean hasNext() {
                return b != 0;
            }

            public E next() {
                int b = this.b;
                if (b == 0) throw new NoSuchElementException();
                final int lob = lowestOneBit(b);
                final E item = itemOf(numberOfTrailingZeros(lob));
                this.b = b ^ lob;
                return item;
            }
        };
    }

    /**
     * Iterate this set in order from last to first flag.
     *
     * @return the iterator
     */
    public final Iterator<E> descendingIterator() {
        return new Iterator<E>() {
            int b = bits;

            public boolean hasNext() {
                return b != 0;
            }

            public E next() {
                int b = this.b;
                if (b == 0) throw new NoSuchElementException();
                final int hob = highestOneBit(b);
                final E item = itemOf(numberOfTrailingZeros(hob));
                this.b = b ^ hob;
                return item;
            }
        };
    }

    /**
     * Apply the given action for every flag in this set.
     *
     * @param action the action to apply
     */
    public void forEach(final Consumer<? super E> action) {
        Assert.checkNotNullParam("action", action);
        int b = this.bits;
        while (b != 0) {
            int lob = lowestOneBit(b);
            action.accept(itemOf(numberOfTrailingZeros(lob)));
            b = b ^ lob;
        }
    }

    /**
     * Get a string representation of this flag set.
     *
     * @return the string representation
     */
    public final String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName()).append('[');
        int lob;
        int bits = this.bits;
        if (bits != 0) {
            lob = lowestOneBit(bits);
            buf.append(itemOf(numberOfTrailingZeros(lob)));
            bits ^= lob;
            while (bits != 0) {
                buf.append(' ');
                lob = lowestOneBit(bits);
                buf.append(itemOf(numberOfTrailingZeros(lob)));
                bits ^= lob;
            }
        }
        buf.append(']');
        return buf.toString();
    }

    private static int bitOf(Enum<?> item) {
        return 1 << item.ordinal();
    }
}
