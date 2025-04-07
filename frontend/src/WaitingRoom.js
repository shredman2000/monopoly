// frontend/src/WaitingRoom.js
import React, { useEffect, useRef } from 'react';
import * as THREE from 'three';
import { useLocation, useNavigate } from 'react-router-dom';
import { BoardObject } from './BoardObject';

function WaitingRoom() {
  const mountRef = useRef(null);
  const location = useLocation();
  const navigate = useNavigate();
  const { gameId } = location.state || {};

  // Dummy list of players (replace with actual fetch later)
  const players = ['Player1', 'Player2'];

  useEffect(() => {
    const width = mountRef.current.clientWidth;
    const height = mountRef.current.clientHeight;

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, width / height, 0.1, 1000);
    camera.position.set(10, 10, 10);

    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(width, height);
    mountRef.current.appendChild(renderer.domElement);

    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(5, 10, 7.5);
    scene.add(light);

    const board = new BoardObject();
    board.getObject3D().rotation.x = -Math.PI / 2;
    scene.add(board.getObject3D());

    const clock = new THREE.Clock();

    const animate = () => {
      requestAnimationFrame(animate);
      const elapsed = clock.getElapsedTime();
      const radius = 15;
      camera.position.x = Math.cos(elapsed * 0.2) * radius;
      camera.position.z = Math.sin(elapsed * 0.2) * radius;
      camera.lookAt(0, 0, 0);
      renderer.render(scene, camera);
    };
    animate();

    return () => {
      if (mountRef.current) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, []);

  const handleStartGame = () => {
    navigate('/scene', { state: { gameId } });
  };

  return (
    <>
      <div ref={mountRef} style={{ width: '100vw', height: '100vh', position: 'absolute', top: 0, left: 0 }} />
      <div
        style={{
          position: 'absolute',
          top: '10%',
          left: '50%',
          transform: 'translateX(-50%)',
          background: 'rgba(255, 255, 255, 0.9)',
          padding: '2rem',
          borderRadius: '10px',
          textAlign: 'center',
          zIndex: 1,
        }}
      >
        <h2>Waiting Room</h2>
        <p>Game ID: <strong>{gameId}</strong></p>
        <h4>Joined Players:</h4>
        <ul>
          {players.map((p, i) => (
            <li key={i}>{p}</li>
          ))}
        </ul>
        <button onClick={handleStartGame}>Start Game</button>
      </div>
    </>
  );
}

export default WaitingRoom;
