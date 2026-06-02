package app.payload;

import java.io.Serializable;
import java.util.List;


public class FetchDataResponsePayload implements Serializable {
  List<AuctionDTO> liveAuction;

  public FetchDataResponsePayload(List<AuctionDTO> liveAuction) {
    this.liveAuction = liveAuction;
  }

  public List<AuctionDTO> getLiveAuction() {
    return liveAuction;
  }
}
