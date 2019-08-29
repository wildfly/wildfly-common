package org.wildfly.common.lock;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import sun.misc.Unsafe;

/**
 */
final class JDKSpecific {
    private JDKSpecific() {}

    static final Unsafe unsafe;

    static {
        unsafe = AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
            public Unsafe run() {
                try {
                    final Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    return (Unsafe) field.get(null);
                } catch (IllegalAccessException e) {
                    throw new IllegalAccessError(e.getMessage());
                } catch (NoSuchFieldException e) {
                    throw new NoSuchFieldError(e.getMessage());
                }
            }
        });
    }

    static void onSpinWait() {
        // n/a
    }
}
