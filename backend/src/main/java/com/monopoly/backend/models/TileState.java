package com.monopoly.backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;



@Entity
public class TileState {

    @Id
    String tileName;

    String ownerUsername;

    int houseCount; // hotel = 4 houses?

    boolean mortgaged;
}