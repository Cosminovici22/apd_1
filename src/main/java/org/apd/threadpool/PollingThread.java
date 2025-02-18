package org.apd.threadpool;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.LinkedBlockingQueue;

final class PollingThread extends Thread {
    private final ThreadPool threadPool;
    private final LinkedBlockingQueue<Runnable> tasks;
    private final AtomicBoolean isWorking;

    PollingThread(ThreadPool threadPool, LinkedBlockingQueue<Runnable> tasks) {
        this.threadPool = threadPool;
        this.tasks = tasks;
        isWorking = new AtomicBoolean(false);
    }

    boolean isWorking() {
        return isWorking.get();
    }

    @Override
    public void run() {
        while (true) {
            try {
                var task = tasks.take();

                isWorking.set(true);
                task.run();
                isWorking.set(false);

                if (threadPool.isIdle()) {
                    threadPool.getIdleLock().release();
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
