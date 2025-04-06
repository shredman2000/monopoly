import * as THREE from 'three';
import { Tile } from './Tile';



const tileNames = [
    "GO", "Mediterranean Avenue", "Community Chest", "Baltic Avenue", "Income Tax",
    "Reading Railroad", "Oriental Avenue", "Chance", "Vermont Avenue", "Connecticut Avenue", "Jail",
    "St. Charles Place", "Electric Company", "States Avenue", "Virginia Avenue", "Pennsylvania Railroad",
    "St. James Place", "Community Chest", "Tennessee Avenue", "New York Avenue", "Free Parking",
    "Kentucky Avenue", "Chance", "Indiana Avenue", "Illinois Avenue", "B&O Railroad",
    "Atlantic Avenue", "Ventnor Avenue", "Water Works", "Marvin Gardens", "Go To Jail",
    "Pacific Avenue", "North Carolina Avenue", "Community Chest", "Pennsylvania Avenue", "Short Line",
    "Chance", "Park Place", "Luxury Tax", "Boardwalk"
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

    for (let i = 0; i < 11; i++) {
    this.addTile(tileNames[tileIndex++], {
        x: startX + i,
        y: startY,
        z: 0.1
    }, 0);
    }

    for (let i = 1; i < 10; i++) {
    this.addTile(tileNames[tileIndex++], {
        x: startX + 10,
        y: startY + i,
        z: 0.1
    }, Math.PI / 2);
    }

    for (let i = 0; i < 11; i++) {
    this.addTile(tileNames[tileIndex++], {
        x: startX + 10 - i,
        y: startY + 10,
        z: 0.1
    }, Math.PI);
    }

    for (let i = 1; i < 10; i++) {
    this.addTile(tileNames[tileIndex++], {
        x: startX,
        y: startY + 10 - i,
        z: 0.1
    }, -Math.PI / 2);
    }
}

// ✅ This fixes your crash
addTile(name, position, rotation) {
    const tile = new Tile(name, position, rotation, 1, 1);
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
