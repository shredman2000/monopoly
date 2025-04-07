import React, { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { FlyControls } from 'three/examples/jsm/controls/FlyControls.js';
import { BoardObject } from './BoardObject';
import { GamePiece } from './GamePiece';
import WebSocketService from './WebSocketService';
import { useLocation } from 'react-router-dom';

function Scene() {
  const mountRef = useRef(null);
  const location = useLocation();
  const { gameId, username } = location.state || {};

  const [playerMap, setPlayerMap] = useState({});
  const [turnIndex, setTurnIndex] = useState(0);
  const [playerUsernames, setPlayerUsernames] = useState([]);

  useEffect(() => {
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(
      90,
      mountRef.current.clientWidth / mountRef.current.clientHeight,
      0.1,
      1000
    );
    camera.position.set(0, 15, 10);
    camera.lookAt(0, 0, 0);

    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(mountRef.current.clientWidth, mountRef.current.clientHeight);
    mountRef.current.appendChild(renderer.domElement);

    const controls = new FlyControls(camera, renderer.domElement);
    controls.movementSpeed = 10;
    controls.rollSpeed = Math.PI / 24;
    controls.autoForward = false;
    controls.dragToLook = false;

    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(5, 10, 5);
    scene.add(light);

    const board = new BoardObject();
    board.getObject3D().rotation.x = -Math.PI / 2;
    scene.add(board.getObject3D());
    board.computeTileWorldPositions(scene);

    const piecesByUsername = {};
    const colors = ['red', 'blue', 'green', 'yellow'];

    WebSocketService.connect(() => {
      WebSocketService.subscribe(`/topic/gameUpdates/${gameId}`, (game) => {
        setTurnIndex(game.turnIndex);
        setPlayerUsernames(game.playerUsernames);

        game.playerStates.forEach((player, index) => {
          let piece = piecesByUsername[player.username];
          if (!piece) {
            piece = new GamePiece(colors[index % colors.length]);
            scene.add(piece.getObject3D());
            piecesByUsername[player.username] = piece;
          }
          const tilePos = board.tilePositions[player.position];
          const offset = new THREE.Vector3(index * 0.4, 0, 0);
          const pos = new THREE.Vector3().copy(tilePos).add(offset);
          piece.moveTo(pos);
        });

        setPlayerMap(piecesByUsername);
      });

      // Request initial game state (optional if already pushing on /start)
      WebSocketService.send(`/app/getLobbyPlayers`, { gameId });
    });

    const clock = new THREE.Clock();
    const animate = () => {
      requestAnimationFrame(animate);
      controls.update(clock.getDelta());
      renderer.render(scene, camera);
    };
    animate();

    return () => {
      if (mountRef.current && renderer.domElement.parentNode) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
      WebSocketService.disconnect();
    };
  }, [gameId, username]);

  const handleRollDice = () => {
    if (playerUsernames[turnIndex] === username) {
      WebSocketService.send(`/app/rollDice`, { gameId });
    } else {
      alert("It's not your turn!");
    }
  };

  return (
    <>
      <div ref={mountRef} style={{ width: '100vw', height: '100vh', overflow: 'hidden' }} />
      <button
        onClick={handleRollDice}
        style={{
          position: 'absolute',
          bottom: '2rem',
          left: '50%',
          transform: 'translateX(-50%)',
          padding: '1rem 2rem',
          fontSize: '1.2rem',
          backgroundColor: '#007BFF',
          color: 'white',
          border: 'none',
          borderRadius: '8px',
          cursor: 'pointer',
          zIndex: 10
        }}
      >
        Roll Dice
      </button>
    </>
  );
}

export default Scene;
