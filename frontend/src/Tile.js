import * as THREE from 'three';
import { Text } from 'troika-three-text';

export class Tile {
  constructor({ name, color }, position, rotation = 0, width = 1, height = 1) {
    this.name = name;
    this.color = color;

    // Base tile
    const baseMaterial = new THREE.MeshBasicMaterial({ color: 0xdcdcdc });
    this.mesh = new THREE.Mesh(
      new THREE.BoxGeometry(width, height, 0.2),
      baseMaterial
    );
    this.mesh.position.set(position.x, position.y, position.z);
    this.mesh.rotation.z = rotation;
    this.mesh.userData = { name };

    // Optional color strip
    if (color) {
      const strip = new THREE.Mesh(
        new THREE.PlaneGeometry(width, 0.2),
        new THREE.MeshBasicMaterial({ color })
      );
      strip.position.set(0, height / 2 - 0.1, 0.11);
      //strip.rotation.x = -Math.PI / 2;
      this.mesh.add(strip);
    }

    // Add name as static text
    const text = new Text();
    text.text = name;
    text.fontSize = 0.15;
    text.maxWidth = 0.9;
    text.color = 0x000000;
    text.anchorX = 'center';
    text.anchorY = 'middle';
    text.position.z = 0.21;
    text.sync();

    this.mesh.add(text);

    // Outline
    const edges = new THREE.EdgesGeometry(new THREE.BoxGeometry(width, height, 0.2));
    const lineMaterial = new THREE.LineBasicMaterial({ color: 0x000000 });
    const outline = new THREE.LineSegments(edges, lineMaterial);
    this.mesh.add(outline);
  }

  getObject3D() {
    return this.mesh;
  }
}
