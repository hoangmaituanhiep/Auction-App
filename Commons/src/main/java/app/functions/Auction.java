package app.functions;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.Client;
import javafx.beans.Observable;
import javafx.collections.ObservableList;

public class Auction {
  private static final Logger logger = LoggerFactory.getLogger(Auction.class);
  
  private int auctionId;
  private double step;
  private String duration;
  private String name;
  private String status;
  private List<Integer> itemId;
  private Map<String, User> onlineUser;
  private List<Client> clients;

  public Auction(String name, String duration) {
    this.name = name;
    this.duration = duration;
    this.status = "OPEN";
    this.itemId = new ArrayList<>();
    this.onlineUser = new HashMap<>();
    clients = new ArrayList<Client>();
  }

  // Auction ID
  public int getAuctionId() {
    return auctionId;
  }

  public void setAuctionId(int id) {
    auctionId = id;
  }

  public String getName() {
    return name;
  }
  
  public String getDuration() {
    return duration;
  }

  // Step
  public double getStep() {
    return step;
  }

  public void setStep(double step) {
    this.step = step;
  }

  // Status
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    switch (status) {
      case "OPEN":
        this.status = "OPEN";
        break;

      case "RUNNING":
        this.status = "RUNNING";
        break;

      case "FINISHED":
        this.status = "FINISHED";
        break;
      
      case "CANCELED":
        this.status = "CANCELED";
        break;
      
      default:
        logger.error("ERROR: Unrecognized status");
        break;
    }

  }

  // Auction Item
  public List<Integer> getItemId() {
    return itemId;
  }

  public void addNewItem(int id) {
    itemId.add(id);
  }


  public Map<String, User> getOnlineUser() {
    return onlineUser;
  }

  public void addOnlineUser(User user) {
    onlineUser.put(user.toString(), user);
  }

  public User getUser(String username) {
    if (onlineUser.containsKey(username)) {
      return onlineUser.get(username);
    }
    return null;
  }

  public List<Client> getClients() {
    return clients;
  }

  public void addClient(Client client) {
    clients.add(client);
  }
}
