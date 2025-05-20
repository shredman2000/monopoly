import React from 'react'; 
import { useState } from 'react';

import MiniBoard from './MiniBoard';

export default function PostMovePanel({ onEndTurn, gameState, username, isMyTurn }) {

  const currentPlayerUsername = gameState?.playerUsernames?.[gameState.turnIndex];
  const currentPlayerState = gameState?.playerStates?.find(p => p.username === currentPlayerUsername);
  const ownedProps = gameState?.tileStates?.filter(tile => tile.ownerUsername === currentPlayerUsername);

  const [selectedTile, setSelectedTile] = useState(null);

  return (
    <div style={{
      backgroundColor: 'white',
      border: '2px solid #000',
      padding: '1rem',
      borderRadius: '8px',
      width: '300px'
    }}>
      {selectedTile && (
        <div style={{ marginTop: '1rem', padding: '0.5rem', border: '1px solid #ccc' }}>
            <strong>{selectedTile.tileName}</strong>
            <p>Owner: {selectedTile.ownerUsername}</p>
            <p> Mortage button </p>

            <button onClick={() => setSelectedTile(null)}>Close</button>
        </div>
      )}
      <p>Current Player: {currentPlayerUsername}</p>
      <p>Money: ${currentPlayerState?.money}</p>

      <button onClick={onEndTurn} disabled={!isMyTurn}>
        End Turn
      </button>
    </div>
  );
}