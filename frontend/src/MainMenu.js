import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';


function MainMenu() {
  const mountRef = useRef(null);
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [joinUsername, setJoinUsername] = useState('');
  const [gameId, setGameId] = useState('');
  const [devMode, setDevMode] = useState(false);
  const [sliderVal, setSliderVal] = useState(0);

  useEffect(() => {

  }, []);

  const handleCreateGame = async () => {
    try {
      let response = null;
      if (devMode) {
        setUsername("DEV");
        response = await fetch('http://localhost:8080/games/createGame', {
          method: 'POST',
          headers: {'Content-Type': 'application/json'},
          body: JSON.stringify({
            numPlayers: sliderVal,
            playerUsernames: [username],
            devMode: devMode,
          }),
        });
      }
      else {
        response = await fetch('http://localhost:8080/games/createGame', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            numPlayers: 1,
            playerUsernames: [username],
            devMode: devMode
          }),
        });
      }
      if (!response.ok) {
        const errorText = await response.text();
        alert(`Error: ${errorText}`);
        return;
      }

      const data = await response.json();
      navigate('/waiting', { state: { gameId: data.gameId, username, devMode } });
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
  const updateSliderVal = (e) => {
    setSliderVal(e.target.value);
  }
  return (
    <>

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
          <button onClick={() => setDevMode(true)} style={{backgroundColor: 'grey'}}>Dev Mode</button>

          {devMode && (
            <div>
              <input type="range" id="addplayers" min={0} max={5} value={sliderVal} onChange={updateSliderVal}/>
              
              <label for="addplayers">Add Players <output>{sliderVal}</output></label>
            </div>
          )}
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
