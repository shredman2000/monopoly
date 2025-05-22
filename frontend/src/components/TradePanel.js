import React from "react";
import './TradePanel.css';
import '../scene.css'

export default function TradePanel({ gameState, currentUsername, onTradingWithPlayer }) {
    const playerCount = gameState?.playerUsernames?.length  || 1;
    const tileStates = gameState.tileStates;

    const tradingLayout = {
        display: 'grid',
        gridTemplateColumns: `repeat(${playerCount}, 1fr)`,
        gridTemplateRows: '10% 1fr 1fr 5%',
        width: '100%',
        height: '100%', 
    }
    const ownableTiles = [
        { tileName: 'Mediterranean Avenue', color: 'brown', col: 2, row: 2},
        { tileName: 'Baltic Avenue', color: 'brown', col: 3, row: 2},
        { tileName: 'Oriental Avenue', color: 'lightblue', col: 6 , row: 2},
        { tileName: 'Vermont Avenue', color: 'lightblue', col: 7, row: 2},
        { tileName: 'Connecticut Avenue', color: 'lightblue', col: 8 , row: 2},
        { tileName: 'St. Charles Place', color: 'pink', col: 10, row: 2},
        { tileName: 'States Avenue', color: 'pink', col: 11, row: 2},
        { tileName: 'Virginia Avenue', color: 'pink', col: 12, row: 2},
        { tileName: 'St. James Place', color: 'orange', col: 2, row: 4},
        { tileName: 'Tennessee Avenue', color: 'orange', col: 3, row: 4},
        { tileName: 'New York Avenue', color: 'orange', col:4 , row: 4},
        { tileName: 'Kentucky Avenue', color: 'red', col: 6, row: 4},
        { tileName: 'Indiana Avenue', color: 'red', col: 7, row: 4},
        { tileName: 'Illinois Avenue', color: 'red', col: 8, row: 4},
        { tileName: 'Atlantic Avenue', color: 'yellow', col: 10, row: 4},
        { tileName: 'Ventnor Avenue', color: 'yellow', col: 11, row: 4},
        { tileName: 'Marvin Gardens', color: 'yellow', col: 12, row: 4},
        { tileName: 'Pacific Avenue', color: 'green', col: 2, row: 6},
        { tileName: 'North Carolina Avenue', color: 'green', col: 3, row: 6},
        { tileName: 'Pennsylvania Avenue', color: 'green', col: 4, row: 6},
        { tileName: 'Park Place', color: 'blue', col: 6, row: 6},
        { tileName: 'Boardwalk', color: 'blue', col: 7, row: 6},
        { tileName: 'Reading Railroad', color: 'black', col: 2, row: 8},
        { tileName: 'Pennsylvania Railroad', color: 'black', col: 3, row: 8},
        { tileName: 'B&O Railroad', color: 'black', col: 4, row: 8},
        { tileName: 'Short Line', color: 'black', col: 5, row: 8},
        { tileName: 'Electric Company', color: 'grey', col: 10, row: 8},
        { tileName: 'Water Works', color: 'grey', col: 11, row: 8},
    ]

    return (
        <div className='trading-overlay'>
            <div style={tradingLayout}>
                {gameState.playerUsernames.map((name, index) => {
                    const ownedTiles = tileStates.filter(t => t.ownerUsername === name);
                    const player = gameState.playerStates.find(p => p.username === name);
                    const playerBalance = player?.money;
                    console.log('name:', name, '| currentUsername:', currentUsername);
                    return (
                        <React.Fragment key={name}>
                        {/* Row 1: player name */}
                        <div
                            style={{
                                gridColumn: `${index + 1} / ${index + 2}`,
                                gridRow: '1 / 2',
                                border: '1px solid black',
                                padding: '1rem',
                                display: 'flex',
                                justifyContent: 'center',
                                flexDirection: 'column',
                                alignItems: 'center',
                                textAlign: 'center',
                            }}
                        >
                            <h3>{name}'s collection</h3>
                            <p>Balance: {playerBalance}</p>
                        </div>

                        {/* Row 2: owned ttiles */}
                        <div
                            style={{
                                gridColumn: `${index + 1} / ${index + 2}`,
                                gridRow: '2 / 3',
                                padding: '1rem',
                                overflow: 'auto',
                                border: '1px solid #aaa',
                                display: 'grid',
                                gridTemplateColumns: '5% 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 5%',
                                gridTemplateRows: '5% 1fr 1fr 1fr 1fr 1fr 1fr 1fr 5%',
                                gap: '0.3rem',
                                height: '100%',
                            }}
                        >
                            {ownableTiles.map(tile => {
                            const ownsTile = ownedTiles.some(t => t.tileName === tile.tileName);
                            return (
                                <div
                                key={tile.tileName}
                                style={{
                                    gridColumn: tile.col,
                                    gridRow: tile.row,
                                    backgroundColor: ownsTile ? 'white' : '#404040',
                                    border: '1px solid #999',
                                    borderRadius: '4px',
                                    position: 'relative',
                                    opacity: ownsTile ? 1 : 0.3,
                                }}
                                >
                                {/* color bar */}
                                <div
                                    style={{
                                    backgroundColor: tile.color,
                                    height: '10%',
                                    width: '100%',
                                    borderTopLeftRadius: '4px',
                                    borderTopRightRadius: '4px',
                                    }}
                                />
                                </div>
                            );
                            })}
                        {/* render button to trade with players other than one's self */}
                        </div> 
                        {name !== currentUsername && (
                            <div
                                style={{
                                    gridColumn: `${index + 1} / ${index + 2}`,
                                    gridRow: '4/5',
                                    justifyContent: 'center',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    alignItems: 'center',
                                    textAlign: 'center',
                                    fontSize: '15',
                                }}>
                                <button onClick={() => onTradingWithPlayer(name)} style={{ fontSize: 20 }}>Trade with {name}</button>
                            </div>
                        )}
                        </React.Fragment>
                        
                    );
                })}
            </div>
        </div>
    );


    
}