This is a custom Java thread pool implementation used to solve the classic
readers-writers concurrency problem.

All self-written code is inside the `src/main/java/org/apd/threadpool` and
`src/main/java/org/apd/executor/worker` directories and
`src/main/java/org/apd/executor/TaskExecutor.java`.

A performance analysis of the solution is available inside `README.md.old`.

## Implementation details

The thread pool is implemented inside `src/main/java/org/apd/threadpool`.

The readers-writers problem is solved using 3 methods, implemented inside
`src/main/java/org/apd/executor/worker`:
1. A readers-preferred method, where the readers are given data access priority
and the writers may experience starvation
2. A writers-preferred method, implemented using split binary semaphores
3. A second writers-preferred method, implemented using Java monitors

Everything is put together inside the function `ExecuteWork()` defined in
`src/main/java/org/apd/executor/TaskExecutor.java`. A factory design pattern,
implemented inside `src/main/java/org/apd/executor/worker/WorkerFactory.java`,
is used to facilitate the instantiation of the classes which represent the 3
aforementioned solutions.
