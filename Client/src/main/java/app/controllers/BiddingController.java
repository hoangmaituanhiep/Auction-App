package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import app.payload.BidItemRespondPayload;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class BiddingController {
    private int ItemId;

    private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
    @FXML
    private ImageView itemImage;
    @FXML
    private AnchorPane bidHistory;
    @FXML
    private Label updateCurrentPrice;
    @FXML
    private TextField placeBid;
    @FXML
    private Button submitBidButton;

    private MainWebController mainWebController;
    private NetworkClient networkClient;

    public int getItemId () {
        return ItemId;
    }

    public void setItemId(int ItemId) {
        this.ItemId = ItemId;
    }

    @FXML
    public void initialize() {
        mainWebController = MainWebController.getInstance();
        networkClient = NetworkClient.getInstance();

        networkClient.addUIListener(Message.NEW_PRICE_RESPOND, packet -> {
            BidItemRespondPayload respone = (BidItemRespondPayload) packet.getPayload();
            if(respone.isSuccess()) {
                try {
                    updateCurrentPrice.setText(String.format("Current highest price: %d", placeBid.getText()));
                } catch (Exception e) {
                    logger.error("Error while bidding: ", e);
                }
            }
        });
    }

    @FXML
    public void bidAction() {
        logger.info("New price is bidded");

        BidItemRequestPayload payload = new BidItemRequestPayload(ItemId, Double.parseDouble(placeBid.getText()));
        PacketMessage mesage = new PacketMessage(Message.NEW_PRICE_REQUEST, payload);

        try{
            networkClient.sendPacket(mesage);
            logger.info("New price is sent.");
        } catch(IOException e){
            logger.error("ERROR: {}", e.getMessage());
        }
    }
}
