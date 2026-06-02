package app.payload;

import java.io.Serializable;

public class BidItemRespondPayload implements Serializable{
  private boolean isSuccess;
  private double newPrice;
  private String error;
  private int Id;

  public BidItemRespondPayload(int Id, double newPrice, boolean isSuccess) {
    this.Id = Id;
    this.newPrice = newPrice;
    this.isSuccess = isSuccess;
  }

  public BidItemRespondPayload(int Id, boolean isSuccess, String error) {
    this.Id = Id;
    this.error = error;
    this.isSuccess = isSuccess;
  }

  public boolean isSuccess() {
    return isSuccess;
  }
  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }
  public String getError() {
    return error;
  }
  public void setNewPrice(double price) {
    this.newPrice = price;
  }
  public double getNewPrice() {
    return newPrice;
  }
  public void setError(String error) {
    this.error = error;
  }
  public int getId() {
    return Id;
  }
}
