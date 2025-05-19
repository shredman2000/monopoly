import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as THREE from 'three';
import { BoardObject } from './BoardObject';

function MainMenu() {
  const mountRef = useRef(null);
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [joinUsername, setJoinUsername] = useState('');
  const [gameId, setGameId] = useState('');

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
      if (mountRef.current && renderer.domElement.parentNode) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, []);

  const handleCreateGame = async () => {
    try {
      const response = await fetch('http://localhost:8080/games/createGame', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          numPlayers: 1,
          playerUsernames: [username],
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        alert(`Error: ${errorText}`);
        return;
      }

      const data = await response.json();
      navigate('/waiting', { state: { gameId: data.gameId, username } });
    } catch (error) {
      alert('Failed to create game');
      console.error(error);
    }
  };

  const handleJoinGame = async () => {
    try {
      const response = await fetch('http://localhost:8080/games/joinGame', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          username: joinUsername,
          gameId: gameId,
        }),
      });

      if (!response.ok) {
        const errorText = await response.text();
        alert(`Error: ${errorText}`);
        return;
      }

      navigate('/waiting', { state: { gameId, username: joinUsername } });
    } catch (error) {
      alert('Failed to join game');
      console.error(error);
    }
  };

  return (
    <>
      <div
        ref={mountRef}
        style={{ width: '100vw', height: '100vh', position: 'absolute', top: 0, left: 0 }}
      />
      <div
        style={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          display: 'flex',
          gap: '2rem',
          background: 'rgba(255, 255, 255, 0.1)',
          padding: '2rem',
          borderRadius: '12px',
          zIndex: 10,
        }}
      >
        {}
        <div
          style={{
            background: 'rgba(255, 255, 255, 0.95)',
            padding: '2rem',
            borderRadius: '12px',
            boxShadow: '0 5px 20px rgba(0,0,0,0.15)',
            width: '300px',
          }}
        >
          <h2 style={{ marginBottom: '1rem' }}>Join Game</h2>
          <input
            type="text"
            placeholder="Username"
            value={joinUsername}
            onChange={(e) => setJoinUsername(e.target.value)}
            style={inputStyle}
          />
          <input
            type="text"
            placeholder="Game ID"
            value={gameId}
            onChange={(e) => setGameId(e.target.value)}
            style={inputStyle}
          />
          <button onClick={handleJoinGame} style={{ ...buttonStyle, backgroundColor: '#007BFF' }}>
            Join
          </button>
        </div>

        {}
        <div
          style={{
            background: 'rgba(255, 255, 255, 0.95)',
            padding: '2rem',
            borderRadius: '12px',
            boxShadow: '0 5px 20px rgba(0,0,0,0.15)',
            width: '300px',
          }}
        >
          <h2 style={{ marginBottom: '1rem' }}>Create Game</h2>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={inputStyle}
          />
          <button onClick={handleCreateGame} style={{ ...buttonStyle, backgroundColor: '#4CAF50' }}>
            Create
          </button>
        </div>
      </div>
    </>
  );
}

const inputStyle = {
  width: '100%',
  padding: '0.6rem',
  marginBottom: '1rem',
  borderRadius: '8px',
  border: '1px solid #ccc',
  fontSize: '1rem',
};

const buttonStyle = {
  width: '100%',
  padding: '0.6rem',
  color: 'white',
  border: 'none',
  borderRadius: '8px',
  cursor: 'pointer',
  fontSize: '1rem',
};

export default MainMenu;
