package com.monopoly.backend.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "auction_states")
public class AuctionState {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String tileName;
    private String seller;
    private int currentBid;
    private String currentBidder;    
    private int bidderIndex;
    private String auctionStarter;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> activeBidders = new ArrayList<>();
    
    
    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "gameId")
    private Game game;

    public AuctionState() {}

    public AuctionState(String tileName, List<String> activeBidders, String auctionStarter) {
        this.tileName = tileName;
        this.activeBidders = activeBidders;
        this.currentBid = 10;
        this.bidderIndex = 0;
        this.currentBidder = activeBidders.get(0);
        this.auctionStarter = auctionStarter;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Long getId() {
        return id;
    }
    public String getTileName() {
        return tileName;
    }

    public int getCurrentBid() {
        return currentBid;
    }

    public String getCurrentBidder() {
        return currentBidder;
    }

    public List<String> getActiveBidders() {
        return activeBidders;
    }

    public int getBidderIndex() {
        return bidderIndex;
    }

    public void advanceTurn() {
        if (activeBidders.size() <= 1) {return;}
        bidderIndex = (bidderIndex + 1) % activeBidders.size();
        currentBidder = activeBidders.get(bidderIndex);
    }
    public void makeBid(String bidder, int amount) {
        if (bidder.equals(currentBidder) && amount > currentBid) {
            currentBid = amount;
        }
    }
    public void removeBidder(String bidder) {
        activeBidders.remove(bidder);
        if (activeBidders.size() == 1) {
            currentBidder = activeBidders.get(0);
        } 
        else {
            bidderIndex %= activeBidders.size();
            currentBidder = activeBidders.get(bidderIndex);
        }
    }
    public String getAuctionStarter() {
        return auctionStarter;
    }
    public void setAuctionStarter(String auctionStarter) {
        this.auctionStarter = auctionStarter;
    }

}