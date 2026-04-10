package app.functions;

import java.time.LocalDateTime;
import java.util.*;

public class Auction {
    private final String auctionId;
    private Seller seller;
    private double step;
    private double currentHighestPrice;
    private Bidder currentWinner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Map<String, Item> auctionItem;

    public Auction(String auctionId, Seller seller, double step,
            LocalDateTime starTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.seller = seller;
        this.step = step;
        this.startTime = starTime;
        this.endTime = endTime;
        this.currentWinner = null;
        this.status = "PENDING";
        this.auctionItem = new HashMap<String, Item>();
    }

    public Item getItem(String id) {
        return auctionItem.get(id);
    }

    public String getAuctionId() {
        return auctionId;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getCurrentHighestPrice(String id) {
        return getItem(id).getCurrent_Price();
    }

    public void setCurrentHighestPrice(String id, double newPrice) {
        if (newPrice > currentHighestPrice) {
            getItem(id).setNewPrice(newPrice);
        }
    }

    public Bidder getCurrentWinner() {
        return currentWinner;
    }

    public void setCurrentWinner(Bidder currentWinner) {
        this.currentWinner = currentWinner;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void add(String id, Item item) {
        auctionItem.put(id, item);
    }

    public Map<String, Item> getAuctionItem() {
        return auctionItem;
    }
}
