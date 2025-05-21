import React from 'react';

export default function TileDetailsPanel({ tile, username, onClose }) {
  if (!tile) return null;


  return (
    <div className="tile-details-panel">
      <h4>{tile.tileName}</h4>
      <p>Type: {tile.type}</p>
      {tile.ownerUsername && <p>Owner: {tile.ownerUsername}</p>}
      {tile.price && <p>Price: ${tile.price}</p>}
      <button onClick={onClose}>Close</button>
    </div>
  );
}
