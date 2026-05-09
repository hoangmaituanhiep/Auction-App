package app.functions;

import java.time.*;

import javafx.scene.image.Image;

public abstract class Item extends Entity {
  private String name;
  private double startingPrice;
  private double current_Price;
  private double maxPrice;
  private int duration;
  

  private String detail = "Seller is too lazy to write anything here.";
  private Image image;

  public Item(String name, String detail, int duration, double startingPrice) {
    this.name = name;
    this.detail = detail;
    this.duration = duration;
    this.startingPrice = startingPrice;
  }

  public int getId() {
    return id;
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
        "\nStrating price: " + startingPrice +
        "\nCurrent highest price: " + current_Price;
  }

  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public int getDuration() {
    return duration;
  }

  public abstract String toString();
}
