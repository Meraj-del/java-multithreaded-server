Java Multithreaded Server

A progression of Java server implementations demonstrating the evolution from single-threaded blocking I/O to a production-safe thread pool architecture — load tested with Apache JMeter at 60,000 concurrent connections.

The Problem This Solves
A single-threaded server handles one client at a time. While processing one connection, every other client waits. Under any real load this collapses immediately.
This project implements and compares three solutions:
ImplementationConcurrency ModelProduction SafeSingle-threadedOne client at a time, blocksNoThread-per-connectionNew thread per client, unboundedNoThread poolFixed ExecutorService, boundedYes

Implementations
1. Single-Threaded Server
SingleThreaded/Server.java
Accepts one connection, processes it completely, then accepts the next. All other clients block on serverSocket.accept().
Client 1 → Server processes → done
Client 2 → waiting...
Client 3 → waiting...
Problem: Under 100 concurrent clients, 99 are waiting. Under 1000, catastrophic.

2. Thread-Per-Connection Server
Multithreaded/Server.java
Spawns a new Thread for every accepted connection. Clients are no longer blocked by each other.
javaSocket acceptedSocket = serverSocket.accept();
Thread thread = new Thread(() -> server.getConsumer().accept(acceptedSocket));
thread.start();
Uses Consumer<Socket> as a functional interface — the server's response logic is passed as a lambda.
Problem: No upper bound on threads. Under 10,000 concurrent clients, 10,000 threads are created. JVM runs out of memory. Server crashes.

3. Thread Pool Server (Production Safe)
ThreadPool/Server.java
Fixed-size ExecutorService created with Executors.newFixedThreadPool(poolSize). Incoming connections are submitted to the pool — if all threads are busy, new connections queue instead of spawning unlimited threads.
javaprivate final ExecutorService threadPool;

public Server(int poolSize) {
    this.threadPool = Executors.newFixedThreadPool(poolSize);
}

// In main:
server.threadPool.execute(() -> server.handleClientSocket(clientSocket));
Result: Bounded memory usage regardless of concurrent connections. Graceful degradation under overload instead of crash.

Load Test Results — Apache JMeter
Tested against the Thread Pool Server (poolSize = 100).
MetricResultTotal concurrent threads60,000Ramp-up period60 secondsTotal samples processed75,562Average response time21msThroughput54,319 requests/minuteDeviation59ms
The server handled 75,562 requests with an average response time of 21ms under 60,000 simulated concurrent users — without crashing, running out of memory, or requiring unbounded thread creation.

Key Concepts Demonstrated
Socket programming — ServerSocket listens on a port, Socket represents each client connection. PrintWriter and BufferedReader wrap the socket streams for text I/O.
Runnable vs Consumer — the thread-per-connection implementation uses Consumer<Socket> as the handler. This demonstrates Java functional interfaces — any lambda matching void accept(Socket s) works.
ExecutorService — Executors.newFixedThreadPool(n) creates a pool of n worker threads. .execute(Runnable) submits tasks. .shutdown() in finally ensures clean termination.
setSoTimeout() — prevents the server from blocking forever on accept(). After the timeout the socket throws SocketTimeoutException — the server can clean up gracefully instead of hanging.
Why thread pools beat unbounded threads — each JVM thread consumes ~1MB of stack memory by default. 10,000 threads = ~10GB RAM. A thread pool of 100 handles the same load with 100MB, queuing excess requests instead of crashing.

How to Run
Prerequisites

Java 21
Any IDE (IntelliJ recommended) or javac

Run Single-Threaded
bash# Terminal 1 — start server
javac SingleThreaded/Server.java
java -cp . SingleThreaded.Server

# Terminal 2 — connect one client
javac SingleThreaded/Client.java
java -cp . SingleThreaded.Client
Run Thread Pool Server
bash# Terminal 1 — start server
javac ThreadPool/Thread/Server.java
java -cp . Thread.Server

# Terminal 2 — run multithreaded client (100 concurrent connections)
javac Multithreaded/Client.java
java -cp . Multithreaded.Client
Load Test with JMeter

Open apache-jmeter-5.6.3/bin/jmeter.bat
Create Thread Group — set threads and ramp-up period
Add TCP Sampler → Server: localhost, Port: 8010
Add Graph Results or View Results in Table listener
Start the server, then run the test


Project Structure
MutlithreadedServer/
├── src/
│   └── Mutithreaded/
│       ├── Client.java     ← load test client (100 concurrent threads)
│       └── Server.java     ← thread-per-connection implementation

ThreadPool/
├── src/
│   └── Thread/
│       └── Server.java     ← ExecutorService thread pool implementation

SingleThreaded/
├── src/
│   ├── Server.java         ← blocking single-threaded implementation
│   └── Client.java         ← single connection client

Author
Md Meraj · Java Backend Developer
GitHub

Learning Journey
This project was built to understand Java concurrency and socket programming before applying these concepts in production distributed systems.
What this project practices:

Java socket programming — ServerSocket, Socket, streams
Thread, Runnable, Consumer functional interface
ExecutorService and thread pool management
Load testing with Apache JMeter
Understanding memory implications of unbounded thread creation

What came next:
After understanding multithreading fundamentals here, I applied these concepts
in a production-grade fintech microservices backend — where Spring Boot manages
thread pools internally, Kafka consumers run on dedicated threads, and Redis
handles distributed locking across concurrent requests.
→ Expense Tracker Microservices Backend
