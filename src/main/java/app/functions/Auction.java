package app.functions;

import java.time.LocalDateTime;
import java.util.*;

public class Auction {
    private final String auctionId;
    private double step;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Map<String, Item> auctionItem;
    private Map<String, User> onlineUser;

    public Auction(String auctionId, Seller seller, double step,
            LocalDateTime starTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.step = step;
        this.startTime = starTime;
        this.endTime = endTime;
        this.status = "PENDING";
        this.auctionItem = new HashMap<String, Item>();
        this.onlineUser = new HashMap<>();
    }

    public void setAuctionItem(Map<String, Item> auctionItem) {
        this.auctionItem = auctionItem;
    }

    public Map<String, User> getOnlineUser() {
        return onlineUser;
    }

    public Item getItem(String id) {
        return auctionItem.get(id);
    }

    public String getAuctionId() {
        return auctionId;
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
