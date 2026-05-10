package app.functions;

import java.time.*;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.scene.image.Image;

public abstract class Item extends Entity implements Serializable, CountdownDurations {
  private static final long serialVersionUID = 1L;
  private String name;
  private double startingPrice;
  private double current_Price;
  private double maxPrice;
  private int duration;
  ExecutorService executorService = Executors.newSingleThreadExecutor();
  

  private String detail = "Seller is too lazy to write anything here.";
  private transient Image image;

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

  public boolean startCountdown() {
    AtomicInteger duration_seconds = new AtomicInteger(duration * 60);
    Future<Boolean> future = executorService.submit(() -> {
      while (duration_seconds.get() > 0) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
        duration_seconds.decrementAndGet();
      }
      return true;
    });
    try {
      return future.get();
    } catch (Exception e) {
      ;
      return false;
    }
  }

  public abstract String toString();
}
