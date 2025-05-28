import React from "react";
import './MiniBoard.css';

export default function MiniBoard({ tileStates, currentUsername, onTileClick, playerStates}) { 
    const playerColorMap = {};
    playerStates.forEach(player => {
        playerColorMap[player.username] = player.color;
    });

    return (
        <div className="mini-board div-background">
            {tileStates.map((tile, i) => {
                const isOwnedByCurrent = tile.ownerUsername === currentUsername;
                const isOwned = tile.ownerUsername !== null;
                
                // other stuff
                const ownerColorClass = tile.ownerUsername ? `owner-color-${playerColorMap[tile.ownerUsername]}${tile.mortgaged ? '-mortgaged' : ''} ` : '';
                

                return (
                    <div 
                        key={tile.tileIndex}
                        className={`tile 
                            ${isOwnedByCurrent ? 'owned' : isOwned ? 'owned-other' : 'unowned'}
                            ${ownerColorClass}
                            tile-pos-${i}
                            ${tile.color ? `color-${tile.color}` : ''}`} 
                        onClick={() => onTileClick(tile)}
                        title={tile.tileName}
                    />
                );
        })}
        </div>
    );
}   