# Java Multithreaded Server

A Java networking project demonstrating the evolution of server-side concurrency models, from a blocking single-threaded architecture to a production-safe thread pool implementation.

The project explores how different concurrency strategies impact scalability, resource utilization, and system stability under load. Each implementation is load tested and compared to illustrate the trade-offs between simplicity, performance, and reliability.

---

# Overview

Traditional blocking servers process one client at a time. As traffic increases, requests begin to queue, response times rise, and the application becomes unusable.

This project implements three server architectures to demonstrate how modern backend systems handle concurrent client connections efficiently.

| Implementation        | Concurrency Model     | Scalability | Production Ready |
| --------------------- | --------------------- | ----------- | ---------------- |
| Single Threaded       | One client at a time  | Poor        | No               |
| Thread Per Connection | One thread per client | Limited     | No               |
| Thread Pool           | Fixed worker threads  | High        | Yes              |

---

# Architecture Evolution

## 1. Single-Threaded Server

Location:

```text
SingleThreaded/Server.java
```

The server accepts a connection, processes it completely, and only then accepts the next connection.

```text
Client 1 → Processing
Client 2 → Waiting
Client 3 → Waiting
```

### Characteristics

* Simple implementation
* Blocking I/O
* No concurrency
* Very poor scalability

### Limitation

When multiple clients connect simultaneously, all requests are forced to wait for the currently executing request to complete.

---

## 2. Thread-Per-Connection Server

Location:

```text
Multithreaded/Server.java
```

Each accepted connection is assigned a dedicated Java thread.

Example:

```java
Socket acceptedSocket = serverSocket.accept();

Thread thread = new Thread(
    () -> server.getConsumer().accept(acceptedSocket)
);

thread.start();
```

### Characteristics

* Concurrent request handling
* Improved responsiveness
* Demonstrates Java threading fundamentals

### Concepts Used

* Thread
* Runnable
* Lambda Expressions
* Consumer<Socket>

### Limitation

The server creates a new thread for every incoming connection.

Under heavy traffic:

```text
10,000 Clients
     ↓
10,000 JVM Threads
     ↓
Excessive Memory Usage
     ↓
Potential OutOfMemoryError
```

This model improves concurrency but introduces serious scalability issues.

---

## 3. Thread Pool Server

Location:

```text
ThreadPool/Server.java
```

Instead of creating unlimited threads, the server maintains a fixed pool of worker threads using ExecutorService.

Example:

```java
private final ExecutorService threadPool;

public Server(int poolSize) {
    this.threadPool =
        Executors.newFixedThreadPool(poolSize);
}
```

Connection handling:

```java
threadPool.execute(
    () -> handleClientSocket(clientSocket)
);
```

### Characteristics

* Controlled concurrency
* Bounded resource usage
* Stable under heavy load
* Production-oriented design

### Benefits

* Prevents thread explosion
* Predictable memory consumption
* Better CPU utilization
* Graceful degradation under overload

This is the approach commonly used by modern backend frameworks and application servers.

---

# Load Testing

The Thread Pool implementation was tested using Apache JMeter.

### Test Configuration

| Parameter        | Value      |
| ---------------- | ---------- |
| Concurrent Users | 60,000     |
| Ramp-Up Period   | 60 Seconds |
| Thread Pool Size | 100        |

### Results

| Metric                | Result                 |
| --------------------- | ---------------------- |
| Total Samples         | 75,562                 |
| Average Response Time | 21 ms                  |
| Throughput            | 54,319 Requests/Minute |
| Standard Deviation    | 59 ms                  |

### Outcome

The server successfully handled more than 75,000 requests under simulated high concurrency while maintaining low response times and bounded memory usage.

No crashes occurred due to uncontrolled thread creation.

---

# Key Concepts Demonstrated

## Java Socket Programming

* ServerSocket
* Socket
* Input/Output Streams
* BufferedReader
* PrintWriter

---

## Java Concurrency

* Thread
* Runnable
* Lambda Expressions
* Functional Interfaces
* Consumer<T>

---

## Executor Framework

* ExecutorService
* Fixed Thread Pools
* Task Submission
* Graceful Shutdown

---

## Network Reliability

### Socket Timeout

```java
serverSocket.setSoTimeout(...)
```

Prevents the server from blocking indefinitely while waiting for connections.

---

## Scalability Trade-Offs

Approximate JVM thread memory usage:

```text
1 Thread ≈ 1 MB Stack Memory
```

Example:

```text
10,000 Threads
≈
10 GB Memory Consumption
```

Using a fixed thread pool:

```text
100 Threads
≈
100 MB Memory Consumption
```

The remaining requests are queued instead of creating unlimited threads.

---

# Running the Project

## Prerequisites

* Java 21
* Apache JMeter (Optional)
* IntelliJ IDEA or any Java IDE

---

## Run Single-Threaded Server

```bash
javac SingleThreaded/Server.java
java -cp . SingleThreaded.Server
```

Client:

```bash
javac SingleThreaded/Client.java
java -cp . SingleThreaded.Client
```

---

## Run Thread Pool Server

```bash
javac ThreadPool/Thread/Server.java
java -cp . Thread.Server
```

Client:

```bash
javac Multithreaded/Client.java
java -cp . Multithreaded.Client
```

---

# Load Testing with Apache JMeter

1. Open JMeter
2. Create a Thread Group
3. Configure desired concurrency level
4. Add TCP Sampler
5. Configure:

   * Host: localhost
   * Port: 8010
6. Add Graph Results listener
7. Start the server
8. Execute the test plan

---

# Project Structure

```text
Java-Multithreaded-Server/
│
├── SingleThreaded/
│   ├── Server.java
│   └── Client.java
│
├── Multithreaded/
│   ├── Server.java
│   └── Client.java
│
├── ThreadPool/
│   └── Server.java
│
└── README.md
```

---

# Skills Demonstrated

* Java Networking
* Socket Programming
* Concurrent Programming
* Multithreading
* Executor Framework
* Functional Interfaces
* Server Scalability
* Load Testing
* Performance Analysis
* Resource Management

---

# Learning Progression

This project represents the foundation of my backend engineering journey.

### What I Learned

* How servers handle client connections
* The limitations of blocking architectures
* Why uncontrolled thread creation is dangerous
* How thread pools improve scalability
* How to evaluate performance through load testing

### What Came Next

After understanding concurrency fundamentals and scalable server design, I moved into asynchronous event-driven systems using Apache Kafka.

Next Project:

**Event-Driven Course Platform**

Built with:

* Spring Boot
* Apache Kafka
* React
* Docker
* Prometheus
* Grafana
* Micrometer

Key focus:

* Kafka Producers
* Kafka Consumers
* Event-Driven Architecture
* Monitoring & Observability

This project served as the bridge between low-level Java concurrency and distributed backend systems.
