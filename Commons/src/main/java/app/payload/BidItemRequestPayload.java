package app.payload;

import java.io.Serializable;

public class BidItemRequestPayload implements Serializable {
  private double price;
  public BidItemRequestPayload(double price) {
    this.price = price;
  }

  public double getPrice() {
    return price;
  }
}
