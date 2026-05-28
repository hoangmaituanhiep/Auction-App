package app.controllers;

import org.slf4j.LoggerFactory;

import java.io.IOException;

import org.slf4j.Logger;

import app.functions.User;
import app.functions.Item;
import app.functions.BidTransaction;
import app.NetworkClient;
import app.packets.Message;
import app.packets.PacketMessage;
import app.payload.BidItemRequestPayload;
import app.payload.BidItemRespondPayload;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class BiddingController {
    private Item item;

    private static final Logger logger = LoggerFactory.getLogger(MainWebController.class);
    @FXML
    private ImageView itemImage;
    @FXML
    private TableView<BidTransaction> tableBidHistory;
    @FXML
    private TableColumn<BidTransaction, String> nameBidder;
    @FXML
    private TableColumn<BidTransaction, Double> bidPrice;
    @FXML
    private Label updateCurrentPrice;
    @FXML
    private TextField placeBid;
    @FXML
    private Button submitBidButton;

    private MainWebController mainWebController;
    private NetworkClient networkClient;
    private User user;

    public int getItemId () {
        return item.getId();
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @FXML
    public void initialize() {
        mainWebController = MainWebController.getInstance();
        networkClient = NetworkClient.getInstance();
        user = User.getInstance();

        nameBidder.setCellValueFactory(cellData -> cellData.getValue().getUserName());
        bidPrice.setCellValueFactory(cellData -> cellData.getValue().getPrice().asObject());

        tableBidHistory.setItems(item.getHistory());

        networkClient.addUIListener(Message.NEW_PRICE_RESPOND, packet -> {
            BidItemRespondPayload respone = (BidItemRespondPayload) packet.getPayload();
            if(respone.isSuccess()) {
                try {
                    updateCurrentPrice.setText(String.format("Current highest price: %d", placeBid.getText()));
                    item.addHistory(new BidTransaction(user.getUserName(), Double.parseDouble(placeBid.getText()))); 
                } catch (Exception e) {
                    logger.error("Error while bidding: ", e);
                }
            }
        });
    }

    @FXML
    public void bidAction() {
        logger.info("New price is bidded");

        BidItemRequestPayload payload = new BidItemRequestPayload(this.getItemId(), user.getUserName(), Double.parseDouble(placeBid.getText()));
        PacketMessage mesage = new PacketMessage(Message.NEW_PRICE_REQUEST, payload);

        try{
            networkClient.sendPacket(mesage);
            logger.info("New price is sent.");
        } catch(IOException e){
            logger.error("ERROR: {}", e.getMessage());
        }
    }
}
