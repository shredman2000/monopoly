import * as THREE from 'three';

/**
 * Game piece constructor
 */
export class GamePiece {

    constructor(color) {
        this.mesh = new THREE.Mesh(new THREE.SphereGeometry(.1, 32,32), new THREE.MeshBasicMaterial({ color }));

        this.mesh.position.set(0,.4,0);
    }

    /**
     * 
     * @returns mesh
     */
    getObject3D() {
        return this.mesh;
    }

    /**
     * 
     * @param {*} positionVector 
     */
    moveTo(positionVector) {
        this.mesh.position.copy(positionVector);
    }


}