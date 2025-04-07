import * as THREE from 'three';
import { Tile } from './Tile';

// 👇 This is the full list of 40 tiles with color
const tileData = [
  { name: "GO", color: null },
  { name: "Mediterranean Avenue", color: "#8B4513" },
  { name: "Community Chest", color: null },
  { name: "Baltic Avenue", color: "#8B4513" },
  { name: "Income Tax", color: null },
  { name: "Reading Railroad", color: "black" },
  { name: "Oriental Avenue", color: "#ADD8E6" },
  { name: "Chance", color: null },
  { name: "Vermont Avenue", color: "#ADD8E6" },
  { name: "Connecticut Avenue", color: "#ADD8E6" },
  { name: "Jail / Just Visiting", color: null },

  { name: "St. Charles Place", color: "#FF00FF" },
  { name: "Electric Company", color: null },
  { name: "States Avenue", color: "#FF00FF" },
  { name: "Virginia Avenue", color: "#FF00FF" },
  { name: "Pennsylvania Railroad", color: "black" },
  { name: "St. James Place", color: "#FFA500" },
  { name: "Community Chest", color: null },
  { name: "Tennessee Avenue", color: "#FFA500" },
  { name: "New York Avenue", color: "#FFA500" },
  { name: "Free Parking", color: null },

  { name: "Kentucky Avenue", color: "#FF0000" },
  { name: "Chance", color: null },
  { name: "Indiana Avenue", color: "#FF0000" },
  { name: "Illinois Avenue", color: "#FF0000" },
  { name: "B&O Railroad", color: "black" },
  { name: "Atlantic Avenue", color: "#FFFF00" },
  { name: "Ventnor Avenue", color: "#FFFF00" },
  { name: "Water Works", color: null },
  { name: "Marvin Gardens", color: "#FFFF00" },
  { name: "Go to Jail", color: null },

  { name: "Pacific Avenue", color: "#008000" },
  { name: "North Carolina Avenue", color: "#008000" },
  { name: "Community Chest", color: null },
  { name: "Pennsylvania Avenue", color: "#008000" },
  { name: "Short Line", color: "black" },
  { name: "Chance", color: null },
  { name: "Park Place", color: "#0000FF" },
  { name: "Luxury Tax", color: null },
  { name: "Boardwalk", color: "#0000FF" }
];

export class BoardObject {
  constructor() {
    this.group = new THREE.Group();
    this.tiles = [];

    const board = new THREE.Mesh(
      new THREE.BoxGeometry(11, 11, 0.05),
      new THREE.MeshBasicMaterial({ color: 0xffffff })
    );
    this.group.add(board);

    this.generateBoardTiles();
  }

  generateBoardTiles() {
    const tileWidth = 1;
    const tileHeight = 1;
    const startX = -5;
    const startY = -5;
  
    let tileIndex = 0;
  
    // ✅ Bottom row (GO → Jail corner)
    for (let i = 0; i < 11; i++) {
      this.addTile(tileData[tileIndex++], {
        x: startX + (10 - i),
        y: startY,
        z: 0.1
      }, 0);
    }
  
    // ✅ Left column (bottom to top)
    for (let i = 1; i < 10; i++) {
      this.addTile(tileData[tileIndex++], {
        x: startX,
        y: startY + i,
        z: 0.1
      }, -Math.PI / 2);
    }
  
    // ✅ Top row (left to right)
    for (let i = 0; i < 11; i++) {
      this.addTile(tileData[tileIndex++], {
        x: startX + i,
        y: startY + 10,
        z: 0.1
      }, Math.PI);
    }
  
    // ✅ Right column (top to bottom)
    for (let i = 1; i < 10; i++) {
      this.addTile(tileData[tileIndex++], {
        x: startX + 10,
        y: startY + 10 - i,
        z: 0.1
      }, Math.PI / 2);
    }
  }
  

  addTile(tileInfo, position, rotation) {
    const tile = new Tile(tileInfo, position, rotation, 1, 1);
    this.tiles.push(tile);
    this.group.add(tile.getObject3D());
  }

  getObject3D() {
    return this.group;
  }

  getTiles() {
    return this.tiles;
  }
}
