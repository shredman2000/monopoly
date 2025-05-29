
package com.monopoly.backend.models;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "player_states")
public class PlayerState {
    @Id
    @GeneratedValue
    private Long id;

    private Boolean canRoll = false;
    private Boolean inPostMove = false;

    private String username;
    private int position = 0;
    private int money = 1500; // starting money, change later
    private String color;

    @ManyToOne
    @JoinColumn(name = "game_id")
    @JsonBackReference
    private Game game;


    /*
    @ElementCollection
    List<String> ownedProperties;
    */

    public PlayerState() {}


    public PlayerState(String username, Game game) {
        this.username = username;
        this.game = game;
    }
    public Boolean getCanRoll() {
        return canRoll;
    }
    public void setCanRoll(Boolean canRoll) {
        this.canRoll = canRoll;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getInPostMove() {
        return inPostMove;
    }

    public void setInPostMove(Boolean inPostMove) {
        this.inPostMove = inPostMove;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerState that = (PlayerState) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}