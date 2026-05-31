package app.functions;

import java.io.Serializable;

public class BidTransaction implements Serializable{
  private String username;
  private double price;

  public BidTransaction(String username, double price) {
    this.username = username;
    this.price = price;
  }

  public String getUserName() {
    return username;
  }

  public double getPrice() {
    return price;
  }
}
