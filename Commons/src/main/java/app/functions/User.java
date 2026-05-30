package app.functions;

import java.util.HashMap;
import java.util.Map;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public abstract class User extends Entity {
  protected static User instance;
  protected String userName;
  protected Auction auction;
  protected String email;

  protected ObservableList<Item> itemsList;
  protected Map<Integer, Item> itemsMap;

  public User() {
    itemsList = FXCollections.observableArrayList();
    itemsMap = new HashMap<>();
  }

  public User(User other) {
    this.id = other.id;
    this.userName = other.userName;
    this.auction = other.auction;
    this.email = other.email;
    this.itemsList = other.itemsList;
    this.itemsMap = other.itemsMap;
  }

  public Bidder asBidder() {
    if (this instanceof Bidder) return (Bidder) this;
    Bidder newRole = new Bidder(this);
    if (this == instance) instance = newRole;
    return newRole;
  }

  public Seller asSeller() {
    if (this instanceof Seller) return (Seller) this;
    Seller newRole = new Seller(this);
    if (this == instance) instance = newRole;
    return newRole;
  }

  public static User getInstance() {
    // Just need one user object for each guys access the app
    if (instance == null) {
      instance = new Bidder();
    }
    return instance;
  }

  public static User createNewUser(String username) {
    User newUser;
    if ("admin1".equals(username)) {
      newUser = new Admin();
    } else {
      newUser = new Bidder();
      newUser.setUserName(username);
    }
    return newUser;
  }

  public void addItem(Item item) {
    itemsList.add(item);
    itemsMap.put(item.getId(), item);
  }

  public ObservableList<Item> getItemsList() {
    return itemsList;
  }

  public void addItemId(int id) {
    itemsList.getLast().addId(id);
  }

  public Item getLastItem() {
    return itemsList.getLast();
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
    this.itemsList.clear();
    this.itemsMap.clear();
  }

  public void existAuction() {
    this.auction = null;
    this.itemsList.clear();
    this.itemsMap.clear();
  }

  public Auction getCurrentAuction() {
    return auction;
  }
}