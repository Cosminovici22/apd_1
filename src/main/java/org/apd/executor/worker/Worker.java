package org.apd.executor.worker;

import org.apd.storage.EntryResult;
import org.apd.storage.SharedDatabase;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

public abstract class Worker {
    protected final SharedDatabase sharedDatabase;
    protected final List<EntryResult> results;

    public Worker(SharedDatabase sharedDatabase) {
        this.sharedDatabase = sharedDatabase;
        results = Collections.synchronizedList(new ArrayList<>());
    }

    public final List<EntryResult> getResults() {
        return results;
    }

    public abstract void write(int index, String data);
    public abstract void read(int index);
}
