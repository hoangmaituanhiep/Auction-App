package app.functions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Auction {
    private final String auctionId;
    private Item item;
    private Seller seller;
    private double startingPrice;
    private double step;
    private double currentHighestPrice;
    private Bidder currentWinner;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private List<BidTransaction> bidHistory;

    public Auction(String auctionId, Item item, Seller seller, double step,
            LocalDateTime starTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.item = item;
        this.seller = seller;
        this.step = step;
        this.startTime = starTime;
        this.endTime = endTime;
        this.currentWinner = null;
        this.status = "PENDING";
        this.bidHistory = new ArrayList<>();
    }

    public String getAuctionId() {
        return auctionId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public double getStartingPrice() {
        return item.getStartingPrice();
    }

    public void setStartingPrice(double startingPrice) {
        item.setStartingPrice(price);
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getCurrentHighestPrice() {
        return item.getCurent_Price();
    }

    public void setCurrentHighestPrice(double newPrice) {
        if (newPrice > currentHighestPrice) {
            item.setNewPrice(newPrice);
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

}
