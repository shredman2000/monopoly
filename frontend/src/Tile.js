import * as THREE from 'three';

export class Tile {
  constructor(name, position, rotation = 0, width = 1, height = 1) {
    this.name = name;
    this.position = position;
    this.rotation = rotation;
    this.width = width;
    this.height = height;

    const geometry = new THREE.BoxGeometry(width, height, 0.2);
    const material = new THREE.MeshBasicMaterial({ color: 0xdcdcdc });
    this.mesh = new THREE.Mesh(geometry, material);

    // ✅ Create outline using geometry, not mesh
    const edges = new THREE.EdgesGeometry(geometry);
    const lineMaterial = new THREE.LineBasicMaterial({ color: 0x000000 });
    const outline = new THREE.LineSegments(edges, lineMaterial);
    this.mesh.add(outline);

    this.mesh.position.set(position.x, position.y, position.z);
    this.mesh.rotation.z = rotation;
    this.mesh.userData = { name };
  }

  getObject3D() {
    return this.mesh;
  }
}
