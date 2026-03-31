package main.java.app;
interface Auctioning {
    void participate();
}


public class User {
    protected String userName;
    protected String password;

    public void register(String userName, String password) {
        if (userName.equals(null) || password.equals(null)){
            System.out.println("Invalid information!");
        }
        else {
            this.userName = userName;
            this.password = password;
        }
    }

    public void signIn(String userName, String password) {
        if (userName.equals(null) || password.equals(null)) {
            System.out.println("Please register first!");
        }
        if (this.userName.equals(userName) &&
        this.password.equals(password)) {
            
        }
    }
}

class Bidder extends User {

}
class Admin extends User {

}
class Seller extends User {

}
