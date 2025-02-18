package org.apd.executor.worker;

import org.apd.executor.LockType;
import org.apd.storage.SharedDatabase;

public final class WorkerFactory {
    private WorkerFactory() { }

    public static Worker createWorker(LockType lockType, SharedDatabase sharedDatabase) {
        return switch (lockType) {
            case LockType.ReaderPreferred -> new ReaderPreferredWorker(sharedDatabase);
            case LockType.WriterPreferred1 -> new WriterPreferredWorker1(sharedDatabase);
            case LockType.WriterPreferred2 -> new WriterPreferredWorker2(sharedDatabase);
            default -> throw new IllegalArgumentException("Unrecognized lock type");
        };
    }
}
