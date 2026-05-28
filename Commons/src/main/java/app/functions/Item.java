package app.functions;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;

public abstract class Item extends Entity {
  private static final long serialVersionUID = 1L;
  private String name;
  private double startingPrice;
  private double current_Price;
  private double maxPrice;
  private final ObservableList<BidTransaction> bidTransactions = FXCollections.observableArrayList();


  private String detail = "Seller is too lazy to write anything here.";
  private transient Image image;
  String imagePath;

  public Item(String name, String detail, double startingPrice) {
    this.name = name;
    this.detail = detail;
    this.startingPrice = startingPrice;
    this.current_Price = startingPrice;
  }

  public int getId() {
    return id;
  }

  public void addId(int id) {
    this.id = id;
  }

  public void setStartingPrice(double price) {
    this.startingPrice = price;
    this.current_Price = price;
  }

  public void setNewPrice(double NewPrice) {
    this.current_Price = NewPrice;
  }

  public void writeDetail(String detail) {
    this.detail = detail;
  }

  public double getStartingPrice() {
    return startingPrice;
  }

  public String getName() {
    return name;
  }

  public double getCurrentPrice() {
    return current_Price;
  }

  public double getMaxPrice() {
    return maxPrice;
  }

  public void setMaxPrice(double Price) {
    this.maxPrice = Price;
  }

  public String getDetail() {
    return detail +
        "\nStarting price: " + startingPrice +
        "\nCurrent price: " + current_Price;
  }

  public void addHistory(BidTransaction bidTransaction) {
    Platform.runLater(() -> {
      bidTransactions.add(bidTransaction);
    });
  }

  public ObservableList<BidTransaction> getHistory() {
    return bidTransactions;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public abstract String toString();
}
