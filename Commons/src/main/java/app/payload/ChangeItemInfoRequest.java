package app.payload;

public class ChangeItemInfoRequest {
  private int auctionId;
  public int getAuctionId() {
    return auctionId;
  }

  private int itemID;
  private String itemName;
  private String itemDetail;
  public ChangeItemInfoRequest(int itemID, String itemName, String itemDetail, int auctionId) {
    this.itemID = itemID;
    this.itemName = itemName;
    this.itemDetail = itemDetail;
    this.auctionId = auctionId;
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
