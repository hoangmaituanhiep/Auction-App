package app.functions;

import java.util.HashMap;
import java.util.Map;

abstract class User extends Entity {
    protected String userName;

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
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
    private Map<String, User> listUser;

    Admin(String userName) {
        this.userName = userName;
        listUser = new HashMap<>();
    }
    Admin(String username, Map<String, User> listUser) {
        this.userName=username;
        if (listUser.isEmpty()) {
            this.listUser = new HashMap<>();
        }
        else {
            this.listUser = listUser;
        }
    }

    public Map<String, User> getListUser(){
        return listUser;
    }
    public User findUser(String userName) {
        return listUser.get(userName);
    }
}
class Seller extends User {
    Map<String, Item> list_item = new HashMap<>();
    public void setStartingPrice(String id,double StartingPrice) {
        list_item.get(id).setStartingPrice(StartingPrice);
    }

    public void setMaxPrice(String id, double MaxPrice) {
        list_item.get(id).setMaxPrice(MaxPrice);
    }

    public void addSellingItem(String id, Item item) {
        list_item.put(id, item);
    }

    public void deleteSellingItem(String id) {
        if (list_item.containsKey(id)) {
            list_item.remove(id);
        }
    }
}
