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
    public void register(String userName, String passWord) {
        if (super.getUserName() == null && super.getPassword() == null) {
            super.setUserName(userName);
            super.setPassword(passWord);
        } else if ( ) {// Nếu tên tài khoản đã sử dụng ở trong database
            System.out.println("Please use other UserName.");
        }
    }
    public void signIn(String userName, String passWord) {
        if () { // có tài khoản trong database chưa
            System.out.println("Account does not exist. Please register first.");
        } else {
            if (super.getPassword() != passWord) {
                System.out.println("Incorrect password.");
            } else () {} // tham gia vào server
        }
    }
    public <T extends Item> String getItem_Info(T item) {
        return item.toString;
    }
    public <T extends Item> void Autioned(T item, double newPrice) {
        if (newPrice > item.getCurrent_Price) {
            item.setNewPrice(newPrice);
            System.out.println("Done!!!");
        } else { System.out.println("Absolutely failure.")}
    }
}
class Admin extends User {
    public void register(String userName, String passWord) {
        if (super.getUserName() == null && super.getPassword() == null) {
            super.setUserName(userName);
            super.setPassword(passWord);
        } else if ( ) {// Nếu tên tài khoản đã sử dụng ở trong database
            System.out.println("Please use other UserName.");
        }
    }
    public void signIn(String userName, String passWord) {
        if () { // có tài khoản trong database chưa
            System.out.println("Account does not exist. Please register first.");
        } else {
            if (super.getPassword() != passWord) {
                System.out.println("Incorrect password.");
            } else () {} // tham gia vào server
        }
    }

}
class Seller extends User {
    public void register(String userName, String passWord) {
        if (super.getUserName() == null && super.getPassword() == null) {
            super.setUserName(userName);
            super.setPassword(passWord);
        } else if ( ) {// Nếu tên tài khoản đã sử dụng ở trong database
            System.out.println("Please use other UserName.");
        }
    }
    public void signIn(String userName, String passWord) {
        if () { // có tài khoản trong database chưa
            System.out.println("Account does not exist. Please register first.");
        } else {
            if (super.getPassword() != passWord) {
                System.out.println("Incorrect password.");
            } else () {} // tham gia vào server
        }
    }
    public void setStartingPrice(double StartingPrice) {

    }

}
