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
        tiles.add(new TileState("GO", "go", game));
        //BROWNS
        tiles.add(new TileState("Mediterranean Avenue","property", 60, game, 50, 2, 10, 30, 90, 160, 30));
        tiles.add(new TileState("Community Chest", "community_chest", game));
        tiles.add(new TileState("Baltic Avenue","property", 60, game, 50, 4, 20, 60, 180, 320, 30));
        
        
        tiles.add(new TileState("Income Tax", "tax", game));
        

        //reading railroad
        tiles.add(new TileState("Reading Railroad","railroad", 200, game, 0, 25, 50, 100, 200, 0, 100));

        //LIGHT BLUES
        tiles.add(new TileState("Oriental Avenue","property", 100, game, 50, 6, 30, 90, 270, 400, 50));
        tiles.add(new TileState("Chance", "chance", game));
        tiles.add(new TileState("Vermont Avenue", "property", 100, game, 50, 6, 30, 90, 270, 400, 50));
        tiles.add(new TileState("Connecticut Avenue","property", 120, game, 50, 8, 40, 100, 300, 450, 60));

        tiles.add(new TileState("Jail", "jail", game));

        //PINKS
        tiles.add(new TileState("St. Charles Place","property", 140, game, 100, 10, 50, 150, 450, 625, 70));
        tiles.add(new TileState("Electric Company", "utility", game));
        tiles.add(new TileState("States Avenue","property", 140, game, 100, 10, 50, 150, 450, 625, 70));
        tiles.add(new TileState("Virginia Avenue", "property",160, game, 100, 12, 60, 180, 500, 700, 80));

        tiles.add(new TileState("Pennsylvania Railroad", "railroad", 200, game, 0, 25, 50, 100, 200, 0, 100));

        //ORANGES
        tiles.add(new TileState("St. James Place", "property",180, game, 100, 14, 70, 200, 550, 750, 90));
        //tiles.add(new TileState("Community Chest", "community_chest", game));
        tiles.add(new TileState("Tennessee Avenue","property", 180, game, 100, 14, 70, 200, 550, 750, 90));
        tiles.add(new TileState("New York Avenue","property", 200, game, 100, 16, 80, 220, 600, 800, 100));

        tiles.add(new TileState("Free Parking", "free_parking", game));

        //REDS
        tiles.add(new TileState("Kentucky Avenue", "property",220, game, 150, 18, 90, 250, 700, 875, 110));
        //iles.add(new TileState("Chance", "chance", game));
        tiles.add(new TileState("Indiana Avenue","property", 220, game, 150, 18, 90, 250, 700, 875, 110));
        tiles.add(new TileState("Illinois Avenue","property", 240, game, 150, 20, 100, 300, 750, 925, 120));

        tiles.add(new TileState("B&O Railroad", "railroad", 200, game, 0, 25, 50, 100, 200, 0, 100));

        //YELLOWS
        tiles.add(new TileState("Atlantic Avenue","property", 260, game, 150, 22, 110, 330, 800, 975, 130));
        tiles.add(new TileState("Ventnor Avenue","property", 260, game, 150, 22, 110, 330, 800, 975, 130));
        tiles.add(new TileState("Water Works", "utility", game));

        tiles.add(new TileState("Marvin Gardens", "property",280, game, 150, 24, 120, 360, 850, 1025, 140));

        tiles.add(new TileState("Go to Jail", "go_to_jail", game));

        //GREENS
        tiles.add(new TileState("Pacific Avenue", "property",300, game, 200, 26, 130, 390, 900, 1100, 150));
        tiles.add(new TileState("North Carolina Avenue", "property",300, game, 200, 26, 130, 390, 900, 1100, 150));
        //tiles.add(new TileState("Community Chest", "community_chest", game));
        tiles.add(new TileState("Pennsylvania Avenue","property", 320, game, 200, 28, 150, 450, 1000, 1200, 160));

        tiles.add(new TileState("Short Line", "railroad", 200, game, 0, 25, 50, 100, 200, 0, 100));
        //tiles.add(new TileState("Chance", "chance", game));

        //DARK BLUES
        tiles.add(new TileState("Park Place", "property",350, game, 200, 35, 175, 500, 1100, 1300, 175));
        tiles.add(new TileState("Luxury Tax", "tax", game));
        tiles.add(new TileState("Boardwalk","property", 400, game, 200, 50, 200, 600, 1400, 1700, 200));


        return tiles;
    }


}