import React from 'react';

export default function TileDetailsPanel({ tile, username, onClose, onHouseBuild, onMortgageTile, onBuyBackMortgage }) {
  if (!tile) return null;

  const canBuild = tile.canPlaceHouse && tile.ownerUsername === username;


  return (
    <div className="tile-details-panel" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <h4>{tile.tileName}</h4>
      <p>Type: {tile.type}</p>
      {tile.ownerUsername && <p>Owner: {tile.ownerUsername}</p>}
      {tile.price && <p>Price: ${tile.price}</p>}


      <h3> House cost: {tile.houseCost}</h3>
      <p style={{ fontWeight: tile.houseCount === 0 ? 'bold' : 'normal' }}>Rent with 0 Houses: {tile.rent0House}</p>
      <p style={{ fontWeight: tile.houseCount === 1 ? 'bold' : 'normal' }}>Rent with 1 Houses: {tile.rent1House}</p>
      <p style={{ fontWeight: tile.houseCount === 2 ? 'bold' : 'normal' }}>Rent with 2 Houses: {tile.rent2House}</p>
      <p style={{ fontWeight: tile.houseCount === 3 ? 'bold' : 'normal' }}>Rent with 3 Houses: {tile.rent3House}</p>
      <p style={{ fontWeight: tile.houseCount === 4 ? 'bold' : 'normal' }}>Rent with 4 Houses: {tile.rent4House}</p>
      <p style={{ fontWeight: tile.houseCount === 5 ? 'bold' : 'normal' }}>Rent with Hotel: {tile.rentHotel}</p>
      <button
        onClick={() => onHouseBuild(tile)}
        disabled={!canBuild}
        style={{
          opacity: canBuild ? 1 : 0.5,
          //cursor: canBuild ? 'pointer' : 'not-allowed',
          marginBottom: '0.5rem',
        }}
      >
        Build House
      </button>

      <button id='mortgagetile-button'
        onClick={() => onMortgageTile(tile)}
        disabled={tile.ownerUsername !== username || tile.mortgaged}
        >
          Mortgage Tile +${tile.costToMortgage}
      </button>
      <button id='buybackmortgagedtile-button'
        onClick={() => onBuyBackMortgage(tile)}
        disabled={tile.ownerUsername !== username || !tile.mortgaged}
        >
          Buy Back Tile -${tile.costToMortgage}
      </button>

      <div style={{ marginTop: 'auto' }}> 
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}
