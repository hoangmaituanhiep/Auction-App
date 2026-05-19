package app.payload;

public class BidItemRequestPayload {
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
