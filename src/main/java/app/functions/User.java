package app.functions;

abstract class User extends Entity {
    protected String userName;
    protected String password;

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getPassword() {return password;}
    public void setPassword(String password) { this.password = password;}

    abstract void register(String userName, String password);
    abstract void signIn(String userName, String passWord);
}

class Bidder extends User {
    User new_bidder = new Bidder();
    public void register(String userName, String passWord) {
        if (new_bidder.getUserName() == null && new_bidder.getPassword() == null) {
            new_bidder.setUserName(userName);
            new_bidder.setPassword(passWord);
        } else if ( ) {// Nếu tên tài khoản đã sử dụng ở trong database
            System.out.println("Please use other UserName.")
        }
    }
    public void signIn(String userName, String passWord) {
        if () { // có tài khoản trong database chưa
            System.out.println("Account does not exist. Please register first.")
        } else {
            if (new_bidder.getPassword() != passWord) {
                System.out.println("Incorrect password.")
            } else () {} // tham gia vào server
        }
    }

}
class Admin extends User {
    User admin = new Admin();
    public void register(String userName, String passWord) {
        if (admin.getUserName() == null && admin.getPassword() == null) {
            admin.setUserName(userName);
            admin.setPassword(passWord);
        } else if ( ) {// Nếu tên tài khoản đã sử dụng ở trong database
            System.out.println("Please use other UserName.")
        }
    }
    public void signIn(String userName, String passWord) {
        if () { // có tài khoản trong database chưa
            System.out.println("Account does not exist. Please register first.")
        } else {
            if (admin.getPassword() != passWord) {
                System.out.println("Incorrect password.")
            } else () {} // tham gia vào server
        }
    }

}
class Seller extends User {


}
