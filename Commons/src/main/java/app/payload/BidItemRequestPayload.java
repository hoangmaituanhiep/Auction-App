package app.payload;

import java.io.Serializable;

public class BidItemRequestPayload implements Serializable {
  private double price;
  int Id;
  public BidItemRequestPayload(int Id, double price) {
    this.Id = Id;
    this.price = price;
  }

  public int getId() {
    return Id;
  }

  public double getPrice() {
    return price;
  }
}
