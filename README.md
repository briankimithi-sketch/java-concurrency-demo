# Java Concurrency Demo

A small, runnable Java 17 portfolio project demonstrating `Runnable`, `Callable`, `Future`, and `ExecutorService` with a realistic order-processing example.

## Learning goals

- Run work asynchronously with `Runnable`.
- Return a typed result from a task with `Callable<T>`.
- Retrieve results, wait with a timeout, and observe task failures with `Future<T>`.
- Manage a fixed pool of worker threads with `ExecutorService`.
- Write automated tests for concurrent behavior and input validation.
- Shut down executors cleanly.

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer
- Git (optional, for publishing to GitHub)

Check your tools:

```bash
java -version
mvn -version
git --version
```

## Run the demonstration

From the project root:

```bash
mvn clean compile
mvn exec:java
```

Example output (thread names and order may vary):

```text
[pool-1-thread-1] Processing ORD-1001
Total for ORD-1001: KSh 2500.00
Total for ORD-1002: KSh 1498.50
```

## Run tests

```bash
mvn test
```

The tests cover order total calculations, input validation, a `Runnable` processing an order, a `Callable` returning a value through `Future`, and exception propagation through `Future.get()`.

## Project structure

```text
java-concurrency-demo/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    ├── main/java/dev/yobi/concurrency/
    │   ├── ConcurrencyDemo.java
    │   ├── Order.java
    │   ├── OrderProcessingTask.java
    │   └── OrderTotalTask.java
    └── test/java/dev/yobi/concurrency/
        ├── OrderTest.java
        ├── OrderProcessingTaskTest.java
        └── OrderTotalTaskTest.java
```

## Concepts demonstrated

### `Runnable`

`Runnable` represents work that does not return a value. Its `run()` method has no result and cannot declare checked exceptions. In this project, `OrderProcessingTask` performs an action on an order.

### `Callable<T>`

`Callable<T>` represents work that returns a value of type `T`. Its `call()` method can throw checked exceptions. `OrderTotalTask` returns a `BigDecimal` order total.

### `Future<T>`

Submitting a `Callable<T>` returns a `Future<T>`. Calling `get()` waits for completion and returns the result. `get(timeout, unit)` limits how long the caller waits. A task failure is surfaced as an `ExecutionException`, whose cause is the original exception.

### `ExecutorService`

`ExecutorService` manages task execution and worker threads. A fixed thread pool reuses a bounded number of worker threads rather than creating a new thread for every task. Shut the executor down when work is complete.

## Push to GitHub

Create an empty repository on GitHub named `java-concurrency-demo`, without adding a README or license there (this project already has a README). Then run:

```bash
git init
git add .
git commit -m "Add Java concurrency demo with tests"
git branch -M main
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/java-concurrency-demo.git
git push -u origin main
```

Replace `YOUR_GITHUB_USERNAME` with your GitHub username.

## Possible next improvements

- Add `invokeAll()` and `invokeAny()` examples.
- Compare sequential and concurrent execution time.
- Add cancellation with `Future.cancel(true)`.
- Add a Spring Boot endpoint that submits background jobs.
