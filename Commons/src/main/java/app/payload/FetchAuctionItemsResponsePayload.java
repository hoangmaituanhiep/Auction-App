package app.payload;

import java.io.Serializable;
import java.util.List;

import app.functions.Item;

public class FetchAuctionItemsResponsePayload implements Serializable {
  private final List<Item> items;

  public FetchAuctionItemsResponsePayload(List<Item> items) {
    this.items = items;
  }

  public List<Item> getItems() {
    return items;
  }
}