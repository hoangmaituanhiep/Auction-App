package app;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private InetSocketAddress socketAddress;
    private ArrayList<Integer> registeredAutions;
    private int highBidNumbers;
    private Socket socket;

    Client (Socket socket) {
        this.socket=socket;
        this.socketAddress=new InetSocketAddress(socket.getInetAddress(), socket.getPort());

        highBidNumbers = 0;
    }

    public InetSocketAddress getInetSocketAddress() {
        return socketAddress;
    }
    public void set(InetSocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
    public ArrayList<Integer> getRegisteredAutions() {
        return registeredAutions;
    }
    public void setRegisteredAutions(ArrayList<Integer> registerAutions) {
        this.registeredAutions = registerAutions;
    }
    public int getHighBidNumbers() {
        return highBidNumbers;
    }
    public void setHighBidNumbers(int highBidNumbers) {
        this.highBidNumbers = highBidNumbers;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket=socket;
    }
}
