package app.payload;

import java.io.Serializable;

public class RegisterClientPayload implements Serializable{
    private int auctionId;

    public int getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(int auctionId) {
        this.auctionId = auctionId;
    }

    public RegisterClientPayload(int auctionId) {
        this.auctionId = auctionId;
    }
}
