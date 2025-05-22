package com.monopoly.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.monopoly.backend.models.Game;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "trade_states")
@JsonIgnoreProperties({"game"})
public class TradeState {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "game_id", referencedColumnName = "gameId")
    private Game game;

    private String player1;
    private int moneyOffered1;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "trade_tiles_offered1",
        joinColumns = @JoinColumn(name = "trade_id"),
        inverseJoinColumns = @JoinColumn(name = "tile_id")
    )
    private List<TileState> tilesOffered1;

    private String player2;
    private int moneyOffered2;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "trade_tiles_offered2",
        joinColumns = @JoinColumn(name = "trade_id"),
        inverseJoinColumns = @JoinColumn(name = "tile_id")
    )
    private List<TileState> tilesOffered2;

    private String currentOfferingPlayer;

    private boolean player1Confirmed;
    private boolean player2Confirmed;

    private boolean tradeSent;

    public TradeState() {}

    public TradeState(Game game, String player1, String player2, String currentOfferingPlayer) {
        this.game = game;
        this.player1 = player1;
        this.moneyOffered1 = 0;

        this.player2 = player2;
        this.moneyOffered2 = 0;

        this.currentOfferingPlayer = currentOfferingPlayer;
        this.player1Confirmed = false;
        this.player2Confirmed = false;
        this.tradeSent = false;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    public Long getId() {
        return id;
    }
    public Game getGame() {
        return game;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setMoneyOffered1(int moneyOffered1) {
        this.moneyOffered1 = moneyOffered1;
    }

    public int getMoneyOffered1() {
        return moneyOffered1;
    }

    public void setTilesOffered1(List<TileState> tilesOffered1) {
        this.tilesOffered1 = tilesOffered1;
    }

    public List<TileState> getTilesOffered1() {
        return tilesOffered1;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
    }

    public String getPlayer2() {
        return player2;
    }

    public void setMoneyOffered2(int moneyOffered2) {
        this.moneyOffered2 = moneyOffered2;
    }

    public int getMoneyOffered2() {
        return moneyOffered2;
    }

    public void setTilesOffered2(List<TileState> tilesOffered2) {
        this.tilesOffered2 = tilesOffered2;
    }

    public List<TileState> getTilesOffered2() {
        return tilesOffered2;
    }

    public void setCurrentOfferingPlayer(String currentOfferingPlayer) {
        this.currentOfferingPlayer = currentOfferingPlayer;
    }

    public String getCurrentOfferingPlayer() {
        return currentOfferingPlayer;
    }

    public void addTileOffered1(TileState tile) {
        if (tilesOffered1 == null) {
            tilesOffered1 = new java.util.ArrayList<>();
        }
        if (!tilesOffered1.contains(tile)) {
            tilesOffered1.add(tile);
        }
    }

    public void addTileOffered2(TileState tile) {
        if (tilesOffered2 == null) {
            tilesOffered2 = new java.util.ArrayList<>();
        }
        if (!tilesOffered2.contains(tile)) {
            tilesOffered2.add(tile);
        }
    }

    public void removeTileOffered1(TileState tile) {
        if (tilesOffered1 != null) {
            tilesOffered1.remove(tile);
        }
    }

    public void removeTileOffered2(TileState tile) {
        if (tilesOffered2 != null) {
            tilesOffered2.remove(tile);
        }
    }

    public void setTradeSent(Boolean tradeSent) {
        this.tradeSent = tradeSent;
    }
    public Boolean getTradeSent() {
        return tradeSent;
    }


}
