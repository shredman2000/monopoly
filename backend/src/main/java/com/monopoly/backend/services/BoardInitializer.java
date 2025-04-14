package com.monopoly.backend.services;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.monopoly.backend.models.Game;
import com.monopoly.backend.models.TileState;

@Service
public class BoardInitializer { 
    public List<TileState> createTiles(Game game) {
        List<TileState> tiles = new ArrayList<>();
        //go
        tiles.add(new TileState("GO", "go",0, game));
        //BROWNS
        tiles.add(new TileState("Mediterranean Avenue","property",1, 60, game, 50, 2, 10, 30, 90, 160, 250, 30));
        tiles.add(new TileState("Community Chest", "community_chest",2, game));
        tiles.add(new TileState("Baltic Avenue","property",3, 60, game, 50, 4, 20, 60, 180, 320, 450, 30));

        tiles.add(new TileState("Income Tax", "tax",4, game));

        //reading railroad
        tiles.add(new TileState("Reading Railroad", "railroad", 5, 200, game, 25, 50, 100, 200, 100));

        //LIGHT BLUES
        tiles.add(new TileState("Oriental Avenue","property",6, 100, game, 50, 6, 30, 90, 270, 400, 550, 50));
        tiles.add(new TileState("Chance", "chance",7, game));
        tiles.add(new TileState("Vermont Avenue", "property",8, 100, game, 50, 6, 30, 90, 270, 400, 550, 50));
        tiles.add(new TileState("Connecticut Avenue","property",9, 120, game, 50, 8, 40, 100, 300, 450, 600, 60));

        tiles.add(new TileState("Jail", "jail",10, game));

        //PINKS
        tiles.add(new TileState("St. Charles Place","property",11, 140, game, 100, 10, 50, 150, 450, 625, 750, 70));
        tiles.add(new TileState("Electric Company", "utility",12, game));
        tiles.add(new TileState("States Avenue","property",13, 140, game, 100, 10, 50, 150, 450, 625, 750, 70));
        tiles.add(new TileState("Virginia Avenue", "property",14,160, game, 100, 12, 60, 180, 500, 700, 900, 80));

        tiles.add(new TileState("Pennsylvania Railroad", "railroad", 15, 200, game, 25, 50, 100, 200, 100));

        //ORANGES
        tiles.add(new TileState("St. James Place", "property",16,180, game, 100, 14, 70, 200, 550, 750, 950, 90));
        tiles.add(new TileState("Community Chest", "community_chest",17, game));
        tiles.add(new TileState("Tennessee Avenue","property",18, 180, game, 100, 14, 70, 200, 550, 750, 950, 90));
        tiles.add(new TileState("New York Avenue","property",19, 200, game, 100, 16, 80, 220, 600, 800, 1000, 100));

        tiles.add(new TileState("Free Parking", "free_parking",20, game));

        //REDS
        tiles.add(new TileState("Kentucky Avenue", "property",21,220, game, 150, 18, 90, 250, 700, 875, 1050, 110));
        tiles.add(new TileState("Chance", "chance",22, game));
        tiles.add(new TileState("Indiana Avenue","property",23, 220, game, 150, 18, 90, 250, 700, 875, 1050, 110));
        tiles.add(new TileState("Illinois Avenue","property",24, 240, game, 150, 20, 100, 300, 750, 925, 1100, 120));

        tiles.add(new TileState("B&O Railroad", "railroad", 25, 200, game, 25, 50, 100, 200, 100));

        //YELLOWS
        tiles.add(new TileState("Atlantic Avenue","property",26, 260, game, 150, 22, 110, 330, 800, 975, 1150, 130));
        tiles.add(new TileState("Ventnor Avenue","property",27, 260, game, 150, 22, 110, 330, 800, 975, 1150, 130));
        tiles.add(new TileState("Water Works", "utility",28, game));

        tiles.add(new TileState("Marvin Gardens", "property",29,280, game, 150, 24, 120, 360, 850, 1025, 1200, 140));

        tiles.add(new TileState("Go to Jail", "go_to_jail", 30,game));

        //GREENS
        tiles.add(new TileState("Pacific Avenue", "property",31,300, game, 200, 26, 130, 390, 900, 1100, 1275, 150));
        tiles.add(new TileState("North Carolina Avenue", "property", 32,300, game, 200, 26, 130, 390, 900, 1100, 1275, 150));
        tiles.add(new TileState("Community Chest", "community_chest",33, game));
        tiles.add(new TileState("Pennsylvania Avenue","property",34, 320, game, 200, 28, 150, 450, 1000, 1200, 1400, 160));

        tiles.add(new TileState("Short Line", "railroad", 35, 200, game, 25, 50, 100, 200, 100));
        tiles.add(new TileState("Chance", "chance",36, game));

        //DARK BLUES
        tiles.add(new TileState("Park Place", "property",37,350, game, 200, 35, 175, 500, 1100, 1300, 1500, 175));
        tiles.add(new TileState("Luxury Tax", "tax",38, game));
        tiles.add(new TileState("Boardwalk","property",39, 400, game, 200, 50, 200, 600, 1400, 1700, 2000, 200));
        return tiles;
    }


}