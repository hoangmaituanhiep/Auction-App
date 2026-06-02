package app.functions;

import java.io.Serializable;

public class BidTransaction implements Serializable{
  private String username;
  private double price;
  private String timestamp;

  public BidTransaction(String username, double price) {
    this.username = username;
    this.price = price;
  }

  public BidTransaction(String username, double price, String timestamp) {
    this.username = username;
    this.price = price;
    this.timestamp = timestamp;
  }

  public String getUserName() {
    return username;
  }

  public double getPrice() {
    return price;
  }

  public String getTimestamp() {
    return timestamp;
  }
}
