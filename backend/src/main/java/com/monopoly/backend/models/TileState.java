package com.monopoly.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tile_states")
public class TileState {

    @Id
    private String tileName;

    private int price;

    private boolean owned;

    private String ownerUsername;

    private int houseCount = 0; // hotel = 4 houses?

    private boolean mortgaged = false;

    private String type; //property, railroad, utility, tax, go....
    

    private int houseCost;
    private int rent0House;
    private int rent1House;
    private int rent2House;
    private int rent3House;
    private int rentHotel;
    private int costToMortgage;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    public TileState() {}

    //constructor for non ownable tiles [GO, jail, income tax, free parking, luxury tax, community chest, chance]
    public TileState(String tileName, String type, Game game) {
        this.tileName = tileName;
        this.type = type;
        this.game = game;
    }

    // constructor for ownable tiles
    public TileState(String tileName, String type, int price, Game game, int houseCost, int rent0House, 
        int rent1House, int rent2House, int rent3House, int rentHotel, int costToMortgage) {
        this.tileName = tileName;
        this.type = type;
        this.price = price;
        this.game = game;
        this.owned = false;
        this.ownerUsername = null;
        this.houseCost = houseCost;
        this.rent0House = rent0House;
        this.rent1House = rent1House;
        this.rent2House = rent2House;
        this.rent3House = rent3House;
        this.rentHotel = rentHotel;
        this.costToMortgage = costToMortgage;
        this.mortgaged = false;
        this.houseCount = 0;
    }  


    public void setTileName(String tileName) {
        this.tileName = tileName;
    }
    public String getTileName() {
        return tileName;
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }
    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public int getHouseCount() {
        return houseCount;
    }
    public void setHouseCount(int houseCount) {
        this.houseCount = houseCount;
    }

    public boolean isMortgaged() {
        return mortgaged;
    }
    public void setMortgaged(boolean mortgaged) {
        this.mortgaged = mortgaged;
    }

    public boolean isOwned() {
        return owned;
    }
    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public Game getGame() {
        return game;
    }
    public void setGame(Game game) {
        this.game = game;
    }
    public int getHouseCost() {
    return houseCost;
    }

    public void setHouseCost(int houseCost) {
        this.houseCost = houseCost;
    }

    public int getRent0House() {
        return rent0House;
    }

    public void setRent0House(int rent0House) {
        this.rent0House = rent0House;
    }

    public int getRent1House() {
        return rent1House;
    }

    public void setRent1House(int rent1House) {
        this.rent1House = rent1House;
    }

    public int getRent2House() {
        return rent2House;
    }

    public void setRent2House(int rent2House) {
        this.rent2House = rent2House;
    }

    public int getRent3House() {
        return rent3House;
    }

    public void setRent3House(int rent3House) {
        this.rent3House = rent3House;
    }

    public int getRentHotel() {
        return rentHotel;
    }

    public void setRentHotel(int rentHotel) {
        this.rentHotel = rentHotel;
    }

    public int getCostToMortgage() {
        return costToMortgage;
    }

    public void setCostToMortgage(int costToMortgage) {
        this.costToMortgage = costToMortgage;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

}