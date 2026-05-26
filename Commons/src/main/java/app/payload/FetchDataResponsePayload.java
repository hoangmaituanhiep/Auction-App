package app.payload;

import java.io.Serializable;
import java.util.List;

import app.functions.Auction;

public class FetchDataResponsePayload implements Serializable {
  List<Auction> liveAuction;

  public FetchDataResponsePayload(List<Auction> liveAuction) {
    this.liveAuction = liveAuction;
  }

  public List<Auction> getLiveAuction() {
    return liveAuction;
  }
}
