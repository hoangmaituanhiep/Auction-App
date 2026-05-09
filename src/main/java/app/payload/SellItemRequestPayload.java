package app.payload;

import app.functions.Item;

public class SellItemRequestPayload {
  Item item;
  public SellItemRequestPayload (Item item) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }
}
