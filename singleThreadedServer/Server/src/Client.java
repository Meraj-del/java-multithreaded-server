import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    public void run(){
        int port=8010;
        try {
            InetAddress addr=InetAddress.getByName("localhost");
            Socket socket=new Socket(addr,port);
            PrintWriter toSocket=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader fromSocket=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            toSocket.println("Hello from the client");
            String line=fromSocket.readLine();
            System.out.println("Response from the client: "+line);
            toSocket.close();
            fromSocket.close();
            socket.close();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client=new Client();
        client.run();
    }
}
