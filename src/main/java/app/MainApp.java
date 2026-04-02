package app;
<<<<<<< HEAD
public class MainApp {
    
=======

import java.io.IOException;

public class MainApp {
    public static void main(String[] args) throws IOException {
        int port = 0;//default

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);//Initiate a port on running
            }
            catch(NumberFormatException e) {
                System.err.println("Cannot format argument:" + args[0]);
                System.exit(1);
            }
        }
        else if (args.length > 1) {
            System.err.println("Too many arguments given.");//Only need one port
            System.exit(1);
        }
    }
>>>>>>> heip
}
