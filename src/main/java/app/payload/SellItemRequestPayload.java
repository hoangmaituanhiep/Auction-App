package app.payload;

import java.io.Serializable;

import app.functions.Item;

public class SellItemRequestPayload implements Serializable{
  Item item;
  public SellItemRequestPayload (Item item) {
    this.item = item;
  }

  public Item getItem() {
    return item;
  }
}
