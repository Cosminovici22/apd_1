package org.apd.executor.worker;

import org.apd.storage.SharedDatabase;

import java.util.concurrent.Semaphore;

final class ReaderPreferredWorker extends Worker {
    private final int[] readerCounts;
    private final Semaphore[] writerLocks;
    private final Object[] readerLocks;

    public ReaderPreferredWorker(SharedDatabase sharedDatabase) {
        super(sharedDatabase);
        readerCounts = new int[sharedDatabase.getSize()];
        writerLocks = new Semaphore[sharedDatabase.getSize()];
        readerLocks = new Object[sharedDatabase.getSize()];

        for (var i = 0; i < writerLocks.length; i++) {
            readerCounts[i] = 0;
            writerLocks[i] = new Semaphore(1);
            readerLocks[i] = new Object();
        }
    }

    @Override
    public void write(int index, String data) {
        var writerLock = writerLocks[index];

        try {
            writerLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        results.add(sharedDatabase.addData(index, data));

        writerLock.release();
    }

    @Override
    public void read(int index) {
        var writerLock = writerLocks[index];
        var readerLock = readerLocks[index];

        synchronized (readerLock) {
            readerCounts[index]++;
            if (readerCounts[index] == 1) {
                try {
                    writerLock.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        results.add(sharedDatabase.getData(index));

        synchronized (readerLock) {
            readerCounts[index]--;
            if (readerCounts[index] == 0) {
                writerLock.release();
            }
        }
    }
}
