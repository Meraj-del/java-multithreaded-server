package Thread;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private  final ExecutorService threadPool;

    public Server(int poolSize) {
        this.threadPool = Executors.newFixedThreadPool(poolSize);
    }

    public void handleClientSocket(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                PrintWriter toSocket = new PrintWriter(socket.getOutputStream(), true)
        ) {
            toSocket.println("Hello World from Server " + socket.getInetAddress());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        int port = 8010;
        int poolSize = 100;
        Server server = new Server(poolSize);

        try{
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(70000);
            System.out.println("Server is listening on port " + port);
            while(true){
                Socket clientSocket=serverSocket.accept();

                server.threadPool.execute(()->server.handleClientSocket(clientSocket));
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            server.threadPool.shutdown();
        }

    }
}
