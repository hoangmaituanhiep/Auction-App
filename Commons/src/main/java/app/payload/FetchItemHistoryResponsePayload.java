package app.payload;

import java.io.Serializable;
import java.util.List;

import app.functions.BidTransaction;

public class FetchItemHistoryResponsePayload implements Serializable {
  private final List<BidTransaction> history;

  public FetchItemHistoryResponsePayload(List<BidTransaction> history) {
    this.history = history;
  }

  public List<BidTransaction> getHistory() {
    return history;
  }
}
