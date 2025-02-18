package org.apd.executor.worker;

import org.apd.storage.SharedDatabase;

import java.util.concurrent.Semaphore;

final class WriterPreferredWorker1 extends Worker {
    private final int writerCounts[], readerCounts[];
    private final Semaphore[] writerSemaphores, readerSemaphores;
    private final Semaphore[] enterLocks;

    public WriterPreferredWorker1(SharedDatabase sharedDatabase) {
        super(sharedDatabase);
        writerCounts = new int[sharedDatabase.getSize()];
        readerCounts = new int[sharedDatabase.getSize()];
        writerSemaphores = new Semaphore[sharedDatabase.getSize()];
        readerSemaphores = new Semaphore[sharedDatabase.getSize()];
        enterLocks = new Semaphore[sharedDatabase.getSize()];

        for (var i = 0; i < enterLocks.length; i++) {
            writerCounts[i] = readerCounts[i] = 0;
            writerSemaphores[i] = new Semaphore(0);
            readerSemaphores[i] = new Semaphore(0);
            enterLocks[i] = new Semaphore(1);
        }
    }

    @Override
    public void write(int index, String data) {
        var writerSemaphore = writerSemaphores[index];
        var readerSemaphore = readerSemaphores[index];
        var enterLock = enterLocks[index];

        try {
            enterLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (writerCounts[index] > 0 || readerCounts[index] > 0) {
            enterLock.release();
            try {
                writerSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writerCounts[index]++;
        enterLock.release();

        results.add(sharedDatabase.addData(index, data));

        try {
            enterLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        writerCounts[index]--;
        if (writerSemaphore.hasQueuedThreads()) {
            writerSemaphore.release();
        } else if (readerSemaphore.hasQueuedThreads()) {
            readerSemaphore.release();
        } else {
            enterLock.release();
        }
    }

    @Override
    public void read(int index) {
        var writerSemaphore = writerSemaphores[index];
        var readerSemaphore = readerSemaphores[index];
        var enterLock = enterLocks[index];

        try {
            enterLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (writerCounts[index] > 0 || writerSemaphore.hasQueuedThreads()) {
            enterLock.release();
            try {
                readerSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readerCounts[index]++;

        if (readerSemaphore.hasQueuedThreads()) {
            readerSemaphore.release();
        } else {
            enterLock.release();
        }

        results.add(sharedDatabase.getData(index));

        try {
            enterLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        readerCounts[index]--;
        if (writerSemaphore.hasQueuedThreads() && readerCounts[index] == 0) {
            writerSemaphore.release();
        } else if (!writerSemaphore.hasQueuedThreads() || readerCounts[index] > 0) {
            enterLock.release();
        }
    }
}
