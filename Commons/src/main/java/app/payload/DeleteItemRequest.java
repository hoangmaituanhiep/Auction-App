package app.payload;

public class DeleteItemRequest {
  private int aucID;
  private int itemID;
  public DeleteItemRequest(int aucID, int itemID) {
    this.aucID = aucID;
    this.itemID = itemID;
  }

  public int getAucID() {
    return aucID;
  }

  public int getItemID() {
    return itemID;
  }
}
