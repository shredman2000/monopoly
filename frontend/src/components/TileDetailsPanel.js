import React from 'react';

export default function TileDetailsPanel({ tile, username, onClose, onHouseBuild }) {
  if (!tile) return null;

  const canBuild = tile.canPlaceHouse && tile.ownerUsername === username;


  return (
    <div className="tile-details-panel" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <h4>{tile.tileName}</h4>
      <p>Type: {tile.type}</p>
      {tile.ownerUsername && <p>Owner: {tile.ownerUsername}</p>}
      {tile.price && <p>Price: ${tile.price}</p>}


      <p>House cost: {tile.houseCost}</p>
      <button
        onClick={() => onHouseBuild(tile)}
        disabled={!canBuild}
        style={{
          opacity: canBuild ? 1 : 0.5,
          cursor: canBuild ? 'pointer' : 'not-allowed',
          marginBottom: '0.5rem',
        }}
      >
        Build House
      </button>

      <div style={{ marginTop: 'auto' }}> 
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}
