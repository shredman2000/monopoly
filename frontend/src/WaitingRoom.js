import React, { useEffect, useRef, useState } from 'react';

import { useLocation, useNavigate } from 'react-router-dom';

import WebSocketService from './WebSocketService';

function WaitingRoom() {
  const mountRef = useRef(null);
  const location = useLocation();
  const navigate = useNavigate();
  const { gameId, username, devMode } = location.state || {};
  const [players, setPlayers] = useState([]);
  const [admin, setAdmin] = useState(null);
  //const [devMode, setDevMode] = useState(false);





  useEffect(() => {
    //console.log("Players state updated:", players);
    WebSocketService.connect(() => {
      fetch('http://localhost:8080/games/getAdmin', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ gameId })  // Send the gameId in the request body
      })
        .then(response => response.text())
        .then(adminName => { 
          setAdmin(adminName);
          console.log("fetched admin:", adminName);
        })
        .catch(err => console.log("error fetching admin", err));


      WebSocketService.subscribe(`/topic/lobby/${gameId}`, (updatedPlayers) => {
        console.log("Players from server:", updatedPlayers);
        setPlayers(updatedPlayers); // assuming backend sends player list
      });
      setTimeout(() => {
        WebSocketService.send(`/app/getLobbyPlayers`, { gameId });
      }, 100);
      WebSocketService.subscribe(`/topic/start/${gameId}`, () => {
        navigate('/scene', { state: { gameId, username, devMode } });
      });

      WebSocketService.send(`/app/joinLobby`, { gameId, username });
    });


    return () => {

    };
  }, [gameId, username, navigate]);

  const handleStartGame = () => {
    WebSocketService.send(`/app/startGame`, { gameId });
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
          {(players).map((username, i) => ( <li key={i}> {username} {username === admin && <strong>(ADMIN)</strong>}</li> ))}
        </ul>
        {(username === admin || devMode ) && (
          <button onClick={handleStartGame}>Start Game</button> // only show start button to admin
        )}
      </div>
    </>
  );
}

export default WaitingRoom;
