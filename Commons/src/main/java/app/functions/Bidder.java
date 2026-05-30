package app.functions;

public class Bidder extends User {

  public Bidder() {
    super();
  }

  public Bidder(User other) {
    super(other);
  }

  public <T extends Item> String getItem_Info(T item) {
    return item.toString();
  }

  public void Autioned(int itemId, double newPrice) {
    Item item = itemsList.get(itemId);
    if (newPrice > item.getCurrentPrice()) {
      item.setNewPrice(newPrice);
      System.out.println("Done!!!");
    } else {
      System.out.println("Absolutely failure.");
    }
  }

  public String toString() {
    return "#bidder:" + getUserName();
  }
}
