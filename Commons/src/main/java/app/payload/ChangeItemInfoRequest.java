package app.payload;

public class ChangeItemInfoRequest {
  private int itemID;
  private String itemName;
  private String itemDetail;
  public ChangeItemInfoRequest(int itemID, String itemName, String itemDetail) {
    this.itemID = itemID;
    this.itemName = itemName;
    this.itemDetail = itemDetail;
  }

  public int getItemId() {
    return itemID;
  } 

  public String getItemName() {
    return itemName;
  }

  public String getItemDetail() {
    return itemDetail;
  }
}
