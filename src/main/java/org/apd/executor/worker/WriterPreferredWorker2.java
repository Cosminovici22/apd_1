package org.apd.executor.worker;

import org.apd.storage.SharedDatabase;

final class WriterPreferredWorker2 extends Worker {
    private final int readerCounts[];
    private final int waitingWriterCounts[];
    private final Object[] enterLocks;

    public WriterPreferredWorker2(SharedDatabase sharedDatabase) {
        super(sharedDatabase);
        readerCounts = new int[sharedDatabase.getSize()];
        waitingWriterCounts = new int[sharedDatabase.getSize()];
        enterLocks = new Object[sharedDatabase.getSize()];

        for (var i = 0; i < enterLocks.length; i++) {
            readerCounts[i] = 0;
            waitingWriterCounts[i] = 0;
            enterLocks[i] = new Object();
        }
    }

    @Override
    public void write(int index, String data) {
        var enterLock = enterLocks[index];

        synchronized (enterLock) {
            waitingWriterCounts[index]++;
            while (readerCounts[index] > 0) {
                try {
                    enterLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            waitingWriterCounts[index]--;

            results.add(sharedDatabase.addData(index, data));

            enterLock.notifyAll();
        }
    }

    @Override
    public void read(int index) {
        var enterLock = enterLocks[index];

        synchronized (enterLock) {
            while (waitingWriterCounts[index] > 0) {
                try {
                    enterLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            readerCounts[index]++;
        }

        results.add(sharedDatabase.getData(index));

        synchronized (enterLock) {
            readerCounts[index]--;
            enterLock.notifyAll();
        }
    }
}
