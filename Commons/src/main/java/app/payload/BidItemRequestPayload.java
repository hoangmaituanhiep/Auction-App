package app.payload;

public class BidItemRequestPayload {
  private double price;
  public BidItemRequestPayload(double price) {
    this.price = price;
  }

  public double getPrice() {
    return price;
  }
}
