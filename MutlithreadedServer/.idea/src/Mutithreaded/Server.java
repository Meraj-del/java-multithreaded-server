package Mutithreaded;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class Server {

    public Consumer<Socket> getConsumer(){ // this accept the socket and perform opretaion and no return
        return (clientSocket) -> {
            try
            {
                PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true); // due to this autoFlush it force to sent the message immediately
                toClient.println("Hello from the server");
                toClient.close();
                clientSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        };
    }

   public static void main(String[] args) {
      int port=8010;
      Server server = new Server();
      try{
          ServerSocket serverSocket=new ServerSocket(port); //server start listening
          serverSocket.setSoTimeout(10000);
          System.out.println("Server is listining on port "+port);
          while (true){
              Socket acceptedSocket=serverSocket.accept();
              Thread thread=new Thread(()->server.getConsumer().accept(acceptedSocket));
              thread.start();
          }
      }catch (IOException e){
          e.printStackTrace();
      }
   }
}
