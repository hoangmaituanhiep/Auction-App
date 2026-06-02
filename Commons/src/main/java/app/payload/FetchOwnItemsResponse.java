package app.payload;

import java.io.Serializable;
import java.util.List;

import app.functions.Item;

public class FetchOwnItemsResponse implements Serializable{
  private List<Item> items;

  public FetchOwnItemsResponse(List<Item> items) {
    this.items = items;
  }

  public List<Item> getItems() {
    return items;
  }

}
