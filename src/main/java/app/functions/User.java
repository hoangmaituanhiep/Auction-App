package app.functions;

abstract class User extends Entity {
    protected String userName;
    protected String password;

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getPassword() {return password;}
    public void setPassword(String password) { this.password = password;}
}

class Bidder extends User {

    public <T extends Item> String getItem_Info(T item) {
        return item.toString();
    }
    public <T extends Item> void Autioned(T item, double newPrice) {
        if (newPrice > item.getCurrent_Price()) {
            item.setNewPrice(newPrice);
            System.out.println("Done!!!");
        } else { System.out.println("Absolutely failure.");}
    }
}
class Admin extends User {

}
class Seller extends User {
    public void setStartingPrice(double StartingPrice) {

    }

}
