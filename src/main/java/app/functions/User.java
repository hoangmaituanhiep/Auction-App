package app.functions;

import java.util.HashMap;
import java.util.Map;

abstract class User extends Entity {
    protected String userName;
    protected String password;
    private Auction pendingAuction;

    public String getUserName() {return userName;}
    public void setUserName(String userName) {this.userName = userName;}
    public String getPassword() {return password;}
    public void setPassword(String password) { this.password = password;}
    public void participate(Auction auction) {this.pendingAuction = auction;}
    public Auction getPendingAuction() {return this.pendingAuction;}

    public String getDetail(String id) {
        return this.getPendingAuction().getItem(id).getDetail();
    }
}

class Bidder extends User {

    public boolean setHigherPrice(String id, double newPrice) {
        return this.getPendingAuction().setCurrentHighestPrice(id, newPrice);
    }
}
class Admin extends User {

}
class Seller<T extends Item> extends User {
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

    public void writeDetail(String id, String detail) {
        list_item.get(id).writeDetail(detail);
    }

    public void sellAll() {
        this.getPendingAuction().getAll(list_item);
        list_item.clear();
    }
}
