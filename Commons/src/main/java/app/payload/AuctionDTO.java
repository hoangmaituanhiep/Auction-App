package app.payload;

import java.io.Serializable;
import java.util.List;

import app.functions.Auction;

public class AuctionDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int auctionId;
    private String name;
    private String duration;
    private double step;
    private String status;
    private List<Integer> itemIds; 

    public AuctionDTO(int auctionId, String name, String duration, double step, String status, List<Integer> itemIds) {
      this.auctionId = auctionId;
      this.name = name;
      this.duration = duration;
      this.step = step;
      this.status = status;
      this.itemIds = itemIds;
    }

    public int getAuctionId() {
      return auctionId;
    }

    public String getName() {
      return name;
    }

    public String getDuration() {
      return duration;
    }

    public double getStep() {
      return step;
    }

    public String getStatus() {
      return status;
    }

    public List<Integer> getItemIds() {
      return itemIds;
    }
}