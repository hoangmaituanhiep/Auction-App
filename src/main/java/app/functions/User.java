package app.functions;

import java.util.HashMap;
import java.util.Map;

import app.controllers.MainWebController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class User extends Entity {
  private static User instance;
  protected String userName;
  protected Auction auction;
  protected String email;

  protected ObservableList<Item> itemsList;
  protected Map<Integer, Item> itemsMap;

  public User() {
    itemsList = FXCollections.observableArrayList();
    itemsMap = new HashMap<>();
  }

  public static User getInstance() {
    // Just need one user object for each guys access the app
    if (instance == null) {
      instance = new User() {
      };
    }
    return instance;
  }

  public void setItems(ObservableList<Item> initialItems) {
    itemsList.setAll(initialItems);
    itemsMap.clear();
    for (Item item : initialItems) {
      itemsMap.put(item.getId(), item);
    }
  }

  public void addItem(Item item) {
    itemsList.add(item);
    itemsMap.put(item.getId(), item);
  }

  public int getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void participate(Auction auction) {
    this.auction = auction;
  }
}

class Bidder extends User {

  public <T extends Item> String getItem_Info(T item) {
    return item.toString();
  }

  public void Autioned(int itemId, double newPrice) {
    Item item = itemsList.get(itemId);
    if (newPrice > item.getCurrent_Price()) {
      item.setNewPrice(newPrice);
      System.out.println("Done!!!");
    } else {
      System.out.println("Absolutely failure.");
    }
  }

  public String toString() {
    return "#bidder:" + getUserName();
  }
}

class Admin extends User {
  private Map<String, User> listUser;

  Admin(String userName) {
    this.userName = userName;
    listUser = new HashMap<>();
  }

  Admin(String username, Map<String, User> listUser) {
    this.userName = username;
    if (listUser.isEmpty()) {
      this.listUser = new HashMap<>();
    } else {
      this.listUser = listUser;
    }
  }

  public void addUser(User user) {
    listUser.put(user.getUserName(), user);
  }

  public Map<String, User> getListUser() {
    return listUser;
  }

  public User findUser(String userName) {
    return listUser.get(userName);
  }

  public String toString() {
    return "#admin:" + getUserName();
  }
}

class Seller extends User {
  Map<String, Item> list_item = new HashMap<>();

  public void setStartingPrice(String id, double startingPrice) {
    if (list_item.containsKey(id)) {
      list_item.get(id).setStartingPrice(startingPrice);
    }
  }

  public void setMaxPrice(String id, double maxPrice) {
    if (list_item.containsKey(id)) {
      list_item.get(id).setMaxPrice(maxPrice);
    }
  }

  public void addSellingItem(String id, Item item) {
    list_item.put(id, item);
  }

  public void deleteSellingItem(String id) {
    if (list_item.containsKey(id)) {
      list_item.remove(id);
    }
  }

  public void sellAll() {
    if (auction != null) {
      auction.add(list_item);
      list_item.clear();
    }
  }

  public String toString() {
    return "#seller:" + getUserName();
  }
}
