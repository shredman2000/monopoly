import React, { useEffect, useRef } from 'react';
import * as THREE from 'three';
import { FlyControls } from 'three/examples/jsm/controls/FlyControls.js';
import { BoardObject } from './BoardObject';

function Scene() {
  const mountRef = useRef(null);

  useEffect(() => {
    // Scene setup
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(90, mountRef.current.clientWidth / mountRef.current.clientHeight, 0.1, 1000);
    camera.position.z = 5;


    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(mountRef.current.clientWidth, mountRef.current.clientHeight);
    mountRef.current.appendChild(renderer.domElement);

    const controls = new FlyControls(camera, renderer.domElement);
    controls.movementSpeed = 10;
    controls.rollSpeed = Math.PI / 24;
    controls.autoForward = false;
    controls.dragToLook = false;

   

    const board = new BoardObject();
    board.getObject3D().rotation.x = -Math.PI / 2;
    scene.add(board.getObject3D());

    const clock = new THREE.Clock();

    const animate = () => {
    requestAnimationFrame(animate);
    const delta = clock.getDelta();
    controls.update(delta);
    renderer.render(scene, camera);
    };
    animate();

     // ✅ Cleanup (safely check before using ref)
     return () => {
      if (mountRef.current && renderer.domElement.parentNode) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, []);

  return (
    <div
      style={{ width: '100vw', height: '100vh', overflow: 'hidden' }}
      ref={mountRef}
    />
  );
}

export default Scene;
