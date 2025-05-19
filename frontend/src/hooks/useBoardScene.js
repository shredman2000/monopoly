import { useEffect, useRef } from 'react';
import * as THREE from 'three';
import { BoardObject } from '../BoardObject';

export default function useBoardScene(mountRef) {
  const sceneRef = useRef(null);
  const cameraRef = useRef(null);
  const boardRef = useRef(null);

  useEffect(() => {
    if (!mountRef.current) return;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(
      90,
      mountRef.current.clientWidth / mountRef.current.clientHeight,
      0.1,
      1000
    );
    const renderer = new THREE.WebGLRenderer({ antialias: true });
    const board = new BoardObject();

    camera.position.set(0, 15, 10);
    camera.lookAt(0, 0, 0);

    renderer.setSize(mountRef.current.clientWidth, mountRef.current.clientHeight);
    mountRef.current.appendChild(renderer.domElement);

    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(5, 10, 5);
    scene.add(light);

    board.getObject3D().rotation.x = -Math.PI / 2;
    scene.add(board.getObject3D());
    board.computeTileWorldPositions(scene);

    sceneRef.current = scene;
    cameraRef.current = camera;
    boardRef.current = board;

    const clock = new THREE.Clock();
    const animate = () => {
      requestAnimationFrame(animate);
      renderer.render(scene, camera);
    };
    animate();

    return () => {
      if (renderer.domElement && renderer.domElement.parentNode) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, [mountRef]);

  return { sceneRef, cameraRef, boardRef };
}
