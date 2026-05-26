package app.payload;

import java.io.Serializable;

public class BidItemRequestPayload implements Serializable {
  private double price;
  private int Id;
  private String username;
  public BidItemRequestPayload(int Id, String username, double price) {
    this.Id = Id;
    this.price = price;
    this.username = username;
  }

  public int getId() {
    return Id;
  }

  public double getPrice() {
    return price;
  }

  public String getUserName() {
    return username;
  }
}
