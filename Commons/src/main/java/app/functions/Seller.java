package app.functions;

import java.util.HashMap;
import java.util.Map;

public class Seller extends User {
  Map<String, Item> list_item = new HashMap<>();

  public Seller() {
    super();
  }

  public Seller(User other) {
    super(other);
  }

  public void setStartingPrice(String id, double startingPrice) {
    if (list_item.containsKey(id)) {
      list_item.get(id).setStartingPrice(startingPrice);
    }
  }

  public void setMaxPrice(String id, double maxPrice) {
    if (list_item.containsKey(id)) {
      list_item.get(id).setMaxPrice(maxPrice);
    }
  }

  public void addSellingItem(String id, Item item) {
    list_item.put(id, item);
  }

  public void deleteSellingItem(String id) {
    if (list_item.containsKey(id)) {
      list_item.remove(id);
    }
  }

  public String toString() {
    return "#seller:" + getUserName();
  }
}

