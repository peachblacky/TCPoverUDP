import client.Client;
import server.Server;

public class Main {

    public static void main(String[] args) {
        Client TCPClient = new Client();
        Server TCPServer = new Server();
        TCPServer.start();
        TCPClient.start();
    }
}
