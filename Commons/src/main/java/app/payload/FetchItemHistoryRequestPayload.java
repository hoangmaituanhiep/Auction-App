package app.payload;

import java.io.Serializable;

public class FetchItemHistoryRequestPayload implements Serializable {
  private final int itemId;

  public FetchItemHistoryRequestPayload(int itemId) {
    this.itemId = itemId;
  }

  public int getItemId() {
    return itemId;
  }
}
