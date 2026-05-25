import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void run() throws IOException {
        int port=8010;
        ServerSocket socket=new ServerSocket(port);
        socket.setSoTimeout(10000);
        while (true) {
            System.out.println("Server is listening on port:"+port);
            Socket acceptedConnection=socket.accept();// thread pause here and wait until a client connect
            System.out.println("Connection accepted from client "+acceptedConnection.getRemoteSocketAddress());
            PrintWriter toClient=new PrintWriter(acceptedConnection.getOutputStream(),true);
            BufferedReader fromClient=new BufferedReader(new InputStreamReader(acceptedConnection.getInputStream()));
            toClient.println("Hello from the Server");
            toClient.close();
            fromClient.close();
            acceptedConnection.close();
        }
    }

    public static void main(String[] args) {
        Server server=new Server();
        try {
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
