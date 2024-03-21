package org.wildfly.common.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class SpinLockTest {

    static final int POOL_SIZE = 16;
    static final int SPIN_COUNT = 20480;

    static ThreadPoolExecutor executor;

    @BeforeClass
    public static void initialize() {
        executor = new ThreadPoolExecutor(POOL_SIZE, POOL_SIZE, Long.MAX_VALUE, TimeUnit.DAYS, new LinkedBlockingQueue<>());
        executor.prestartAllCoreThreads();
    }

    @Test
    public void ensureLockWorks() {
        SpinLock lock = new SpinLock();
        int[] holder = new int[1];
        CountDownLatch latch = new CountDownLatch(POOL_SIZE);
        for (int i = 0; i < POOL_SIZE; i ++) {
            executor.execute(() -> {
                LockSupport.parkNanos(100_000L);
                for (int j = 0; j < SPIN_COUNT; j ++) {
                    lock.lock();
                    try {
                        holder[0] ++;
                    } finally {
                        lock.unlock();
                    }
                }
                latch.countDown();
            });
        }
        for (;;) try {
            latch.await();
            break;
        } catch (InterruptedException ignored) {
        }
        Assert.assertEquals("Wrong final count", POOL_SIZE * SPIN_COUNT, holder[0]);
    }


    @AfterClass
    public static void shutdown() {
        ThreadPoolExecutor executor = SpinLockTest.executor;
        if (executor != null) {
            executor.shutdownNow();
            for (;;) try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
                break;
            } catch (InterruptedException ignored) {
            }
        }
    }
}
