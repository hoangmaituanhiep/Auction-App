package app.functions;

import javafx.beans.property.*;

public class BidTransaction {
  private final StringProperty username;
  private final DoubleProperty price;

  public BidTransaction(String username, double price) {
    this.username = new SimpleStringProperty(username);
    this.price = new SimpleDoubleProperty(price);
  }

  public StringProperty getUserName() {
    return username;
  }

  public DoubleProperty getPrice() {
    return price;
  }
}
