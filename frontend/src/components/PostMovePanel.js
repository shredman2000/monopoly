import React from 'react';

export default function PostMovePanel({ onEndTurn, gameState, username, isMyTurn }) {

  const currentPlayerUsername = gameState?.playerUsernames?.[gameState.turnIndex];
  const currentPlayerState = gameState?.playerStates?.find(p => p.username === currentPlayerUsername);
  const ownedProps = gameState?.tileStates?.filter(tile => tile.ownerUsername === currentPlayerUsername);



  return (
    <div style={{
      position: 'absolute',
      bottom: '3rem',
      left: '50%',
      transform: 'translateX(-50%)',
      backgroundColor: 'white',
      border: '2px solid #000',
      padding: '1rem',
      borderRadius: '8px',
      zIndex: 20,
      minWidth: '300px'
    }}>
      <p>Current Player: {currentPlayerUsername}</p>
      <p>Money: ${currentPlayerState?.money}</p>
      <h4>Owned Properties:</h4>
      <ul>
        {ownedProps?.map(tile => (
          <li key={tile.tileName}>{tile.tileName} (${tile.price})</li>
        ))}
      </ul>

      <button onClick={onEndTurn} disabled={!isMyTurn}>
        End Turn
      </button>
    </div>
  );
}