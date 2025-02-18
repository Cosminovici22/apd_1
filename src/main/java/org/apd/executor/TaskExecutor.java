package org.apd.executor;

import org.apd.executor.worker.WorkerFactory;
import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;
import org.apd.threadpool.ThreadPool;

import java.util.List;

/* DO NOT MODIFY THE METHODS SIGNATURES */
public class TaskExecutor {
    private final SharedDatabase sharedDatabase;

    public TaskExecutor(int storageSize, int blockSize, long readDuration, long writeDuration) {
        sharedDatabase = new SharedDatabase(storageSize, blockSize, readDuration, writeDuration);
    }

    public List<EntryResult> ExecuteWork(int numberOfThreads, List<StorageTask> tasks, LockType lockType) {
        var threadPool = new ThreadPool(numberOfThreads);
        var worker = WorkerFactory.createWorker(lockType, sharedDatabase);

        for (var task : tasks) {
            threadPool.submit(() -> {
                if (task.isWrite()) {
                    worker.write(task.index(), task.data());
                } else {
                    worker.read(task.index());
                }
            });
        }
        threadPool.awaitIdle();
        threadPool.interruptAll();

        return worker.getResults();
    }

    public List<EntryResult> ExecuteWorkSerial(List<StorageTask> tasks) {
        var results = tasks.stream().map(task -> {
            try {
                if (task.isWrite()) {
                    return sharedDatabase.addData(task.index(), task.data());
                } else {
                    return sharedDatabase.getData(task.index());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return results.stream().toList();
    }
}
