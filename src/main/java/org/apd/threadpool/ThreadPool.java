package org.apd.threadpool;

import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public final class ThreadPool {
    private final PollingThread threads[];
    private final LinkedBlockingQueue<Runnable> tasks;
    private final Semaphore idleLock;

    public ThreadPool(int threadCount) {
        threads = new PollingThread[threadCount];
        tasks = new LinkedBlockingQueue<>();
        idleLock = new Semaphore(1);

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new PollingThread(this, tasks);
            threads[i].start();
        }
    }

    public void submit(Runnable task) {
        try {
            if (tasks.isEmpty()) {
                idleLock.acquire();
            }
            tasks.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void interruptAll() {
        for (var thread : threads) {
            thread.interrupt();
        }
    }

    public void awaitIdle() {
        try {
            idleLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        idleLock.release();
    }

    boolean isIdle() {
        return Arrays.stream(threads)
            .map(thread -> thread.isWorking())
            .reduce(tasks.isEmpty(), (acc, isWorking) -> acc && !isWorking);
    }

    Semaphore getIdleLock() {
        return idleLock;
    }
}
