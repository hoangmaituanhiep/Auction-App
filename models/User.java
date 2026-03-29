package models;
interface Auctioning {
    void participate();
}


public class User {
    protected String userName;
    protected String password;

    public void register(String userName, String password) {
        if (userName.equals(null) || password.equals(null)){
            System.out.println("Invalid information");
        }
        else {
            this.userName = userName;
            this.password = password;
        }
    }
}

class Bidder extends User {

}
class Admin extends User {

}
class Seller extends User {
    
}
