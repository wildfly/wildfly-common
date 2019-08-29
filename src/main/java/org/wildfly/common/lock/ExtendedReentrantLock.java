package org.wildfly.common.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 */
@SuppressWarnings("serial")
class ExtendedReentrantLock extends ReentrantLock implements ExtendedLock {
    ExtendedReentrantLock(final boolean fair) {
        super(fair);
    }

    ExtendedReentrantLock() {
        super();
    }
}
