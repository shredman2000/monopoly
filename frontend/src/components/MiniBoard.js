import React from "react";
import './MiniBoard.css';

export default function MiniBoard({ tileStates, currentUsername, onTileClick}) { 
    return (
        <div className="mini-board div-background">
            {tileStates.map((tile, i) => {
                const isOwnedByCurrent = tile.ownerUsername === currentUsername;
                const isOwned = tile.ownerUsername !== null;
                // other stuff
                    

                return (
                    <div 
                        key={tile.tileIndex}
                        className={`tile 
                            ${isOwnedByCurrent ? 'owned' : isOwned ? 'owned-other' : 'unowned'}
                            tile-pos-${i}
                            ${tile.color ? `color-${tile.color}` : ''}`} 
                        onClick={() => onTileClick(tile)}
                        title={tile.tileName}
                        style={{borderTop: `4px solid ${tile.color || 'transparent'}`}}
                        ></div>
                );
        })}
        </div>
    );
}