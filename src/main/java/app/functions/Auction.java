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

    public Auction(String auctionId, Item item, Seller seller, double startingPrice, double step,
            LocalDateTime starTime, LocalDateTime endTime) {
        this.auctionId = auctionId;
        this.item = item;
        this.seller = seller;
        this.startingPrice = startingPrice;
        this.step = step;
        this.startTime = starTime;
        this.endTime = endTime;
        this.currentHighestPrice = startingPrice;
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
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getCurrentHighestPrice() {
        return currentHighestPrice;
    }

    public void setCurrentHighestPrice(double currentHighestPrice) {
        this.currentHighestPrice = currentHighestPrice;
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
