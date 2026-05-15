package app.payload;

import java.io.Serializable;

public class SellItemRespondPayload implements Serializable {
  private boolean isSuccess;
  private String error;
  private int itemId;

  public boolean isSuccess() {
    return isSuccess;
  }

  public void setSuccess(boolean isSuccess) {
    this.isSuccess = isSuccess;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public int getItemId(){
    return itemId;
  }

  public void setItemId(int id) {
    this.itemId = id;
  }

  public SellItemRespondPayload(boolean isSuccess, int itemId) {
    this.isSuccess = isSuccess;
    this.itemId = itemId;
  }

  public SellItemRespondPayload(boolean isSuccess, String error) {
    this.isSuccess = isSuccess;
    this.error = error;
  }
}
