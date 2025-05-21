import React from "react";
import './TradePanel.css';
import '../scene.css'

export default function TradePlayerPanel({ gameState, currentUsername, otherPlayer: name}) {
    const playerCount = gameState?.playerUsernames?.length  || 1;
    const tileStates = gameState.tileStates;

    const tradingLayout = {
        display: 'grid',
        gridTemplateColumns: `1fr 1fr)`,
        gridTemplateRows: '10% 10% 1fr 1fr 5%',
        width: '100%',
        height: '100%', 
    }
    const ownableTiles = [
        { tileName: 'Mediterranean Avenue', color: 'brown', col: 2, row: 2},
        { tileName: 'Baltic Avenue', color: 'brown', col: 3, row: 2},
        { tileName: 'Oriental Avenue', color: 'lightblue', col: 6 , row: 2},
        { tileName: 'Vermont Avenue', color: '', col: 7, row: 2},
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
                <div
                    style={{
                        gridColumn: `1 / 3`,
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
                <h1>{currentUsername} offering trade to {name}</h1>
                </div>
                {gameState.playerUsernames.map((name, index) => {
                    const ownedTiles = tileStates.filter(t => t.ownerUsername === name);
                    const player = gameState.playerStates.find(p => p.username === name);
                    const playerBalance = player?.money;
                    console.log('name:', name, '| currentUsername:', currentUsername);
                    return (
                        <React.Fragment key={name}>
                        


                        {/* Row 2: player name */}
                        <div
                            style={{
                                gridColumn: `${index + 1} / ${index + 2}`,
                                gridRow: '2 / 3',
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
                                gridRow: '3 / 4',
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
                        </div>
                        </React.Fragment>
                        
                    );
                })}
            </div>
        </div>
    );


    
}