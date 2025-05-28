import React, {useState} from "react";
import './TradePanel.css';
import '../scene.css'

export default function TradePlayerPanel({ 
    gameState, 
    currentUsername, 
    otherPlayer: name, 
    onAddTileToTrade, 
    onRemoveTileFromTrade, 
    onSendTrade, 
    onChangeMoney,  
    tradeState,
    onAcceptTrade,
    onRejectTrade,
    onEditTrade,
}) {
    const playerCount = gameState?.playerUsernames?.length  || 1;
    const tileStates = gameState.tileStates;
    const [tempSliderValue, setTempSliderValue] = useState(0);
    const [sliderValues, setSliderValues] = useState({});
    const isMyTurnToEdit = currentUsername === tradeState?.currentOfferingPlayer;
    const canAcceptOrReject = currentUsername === tradeState?.player1 || tradeState?.player2;
    const canEditTrade = canAcceptOrReject && currentUsername !== tradeState?.currentOfferingPlayer;
    const player1Accepted = tradeState.player1Confirmed;
    const player2Accepted = tradeState.player2Confirmed;



    const tradingLayout = {
        display: 'grid',
        gridTemplateColumns: `1fr 1fr`,
        gridTemplateRows: '10% 10% 10% 1fr 1fr 5%',
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
                    { !tradeState.tradeSent && 
                        <h1>{currentUsername} offering trade to {name}</h1>
                    }
                    { tradeState.tradeSent &&
                        <h1>Review Trade...</h1>
                    }
                </div>
                {gameState.playerUsernames.map((name, index) => {
                    const ownedTiles = tileStates.filter(t => t.ownerUsername === name);
                    const player = gameState.playerStates.find(p => p.username === name);
                    const playerBalance = player?.money;

                    console.log('name:', name, '| currentUsername:', currentUsername);
                    console.log('can accept, reject, or edit');
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
                                    borderColor: `${currentUsername === name ? 'green' : 'none'}`,
                                    boxShadow: `${currentUsername === name ? 'inset 0 0 20px rgba(0, 255, 0, 0.5)' : 'none'}`
                                }}
                            >
                                <h3 style={{ marginBottom: 0 }}>{name}'s collection</h3>
                                <p style={{ marginTop: 0 }}>Balance: {playerBalance}</p>
                            </div>
                            {/* Row 3: money slider */}
                            <div style={{
                                gridColumn: `${index + 1} / ${index + 2}`,
                                gridRow: '3 / 4',
                                padding: '0.5rem',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                justifyContent: 'center',
                                gap: '0.5rem',
                                zIndex: 10,
                                position: 'relative' 
                                }}>
                                
                                <input
                                    type="range"
                                    min="0"
                                    defaultValue={0}
                                    max={playerBalance}
                                    disabled={tradeState?.tradeSent || !isMyTurnToEdit}
                                    value={sliderValues[name] ?? 0}
                                    onChange={(e) => {
                                        const newVal = parseInt(e.target.value, 10);
                                        setSliderValues(prev => ({ ...prev, [name]: newVal }));
                                    }}
                                    onMouseUp={() => { 
                                        const val = sliderValues[name] ?? 0;
                                        onChangeMoney(name, val);
                                    }}
                                    style={{ width: '80%' }}
                                />
                                <label>
                                    ${name === tradeState?.player1 ? tradeState.moneyOffered1 : tradeState?.moneyOffered2}
                                </label>
                            </div>

                            {/* Row 4: owned ttiles */}
                            <div
                                style={{
                                    gridColumn: `${index + 1} / ${index + 2}`,
                                    gridRow: '4 / 5',
                                    padding: '1rem',
                                    overflow: 'auto',
                                    border: '1px solid #aaa',
                                    display: 'grid',
                                    gridTemplateColumns: '5% 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 1fr 5%',
                                    gridTemplateRows: '5% 1fr 1fr 1fr 1fr 1fr 1fr 1fr 5%',
                                    gap: '0.3rem',
                                    maxHeight: '100%',

                                }}
                            >
                                {ownableTiles.map(tile => {
                                    const ownsTile = ownedTiles.some(t => t.tileName === tile.tileName);
                                    const tileState = tileStates.find(t => t.tileName === tile.tileName);
                                    const isInTrade = (tradeState?.tilesOffered1 || []).some(t => t.tileName === tile.tileName)
                                                    || (tradeState?.tilesOffered2 || []).some(t => t.tileName === tile.tileName);
                                    const isMortgaged = tileState?.mortgaged ?? false;
                                    return (
                                        <div
                                            key={tile.tileName}
                                            style={{
                                                gridColumn: tile.col,
                                                gridRow: tile.row,
                                                backgroundColor: ownsTile ? (!isMortgaged ? 'white' : "#a8a8a8") : '#404040',
                                                border: `2px solid ${isInTrade ? 'green' : '#999'}`,
                                                borderRadius: '4px',
                                                position: 'relative',
                                                opacity: ownsTile ? 1 : 0.3,
                                                cursor: ownsTile && isMyTurnToEdit && !tradeState?.tradeSent ? 'pointer' : 'not-allowed'

                                            }}
                
                                            onClick={() => {
                                                const matched = ownedTiles.find(t => t.tileName === tile.tileName);
                                                const tileOwner = matched?.ownerUsername;

                                                if (tileOwner && !isInTrade) {
                                                    onAddTileToTrade(tile.tileName, tileOwner);
                                                }
                                                else if (tileOwner && isInTrade) {
                                                    onRemoveTileFromTrade(tile.tileName, tileOwner);
                                                }
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

                        <div
                            style={{
                                gridColumn: `${index + 1} / ${index + 2}`,
                                gridRow: '5 / 6',
                                padding: '1rem',
                                border: '1px solid #666',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                gap: '0.5rem',
                                overflowY: 'auto',
                            }}
                            >
                            <h4>Offering:</h4>
                            <p>${name === tradeState?.player1 ? tradeState.moneyOffered1 : tradeState?.moneyOffered2}</p>
                            {
                                tradeState &&
                                Array.isArray(name === tradeState.player1 ? tradeState.tilesOffered1 : tradeState.tilesOffered2) &&

                                (name === tradeState.player1 ? tradeState.tilesOffered1 : tradeState.tilesOffered2).map(tile => (
                                    <div key={tile.tileName} style={{ /* styling for showing tiles */ }}>
                                        
                                        {tile.tileName}
                                    </div>
                                ))
                            }
                        </div>
                        
                        {/* send trade */}
                        {!tradeState.tradeSent && isMyTurnToEdit && 
                            <div 
                                
                                    style={{
                                        gridColumn: `1/3`,
                                        gridRow: '6/7',
                                        justifyContent: 'center',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'center',
                                        textAlign: 'center',
                                        fontSize: '15',
                                    }}>
                                    <button onClick={() => {
                                        onSendTrade();
                                        
                                    }}
                                    style={{ fontSize: 20 }}
                                    >Send Trade</button>
                            </div>
                        }

                        

                        </React.Fragment>
                        
                    );
                })}
                {/* buttons to accept, reject, or edit trade */}
                    {tradeState.tradeSent && canAcceptOrReject &&
                        <div style={{
                            gridColumn: `1/3`,
                            gridRow: '6/7',
                            display: 'flex',
                            justifyContent: 'center',
                            flexDirection: 'row',
                            

                        }}>
                            <div id="accept-button"
                                style={{
                                    gridColumn: `1/3`,
                                    gridRow: '6/7',
                                    justifyContent: 'center',
                                    display: 'flex',
                                    flexDirection: 'row',
                                    alignItems: 'center',
                                    textAlign: 'center',
                                    fontSize: '15',
                                }}
                            >
                                <button onClick={() => {
                                    onAcceptTrade(currentUsername);
                                }}>Accept Trade</button>
                            </div>
                            <div id="reject-button"
                                style={{
                                    gridColumn: `1/3`,
                                    gridRow: '6/7',
                                    justifyContent: 'center',
                                    display: 'flex',
                                    flexDirection: 'row',
                                    alignItems: 'center',
                                    textAlign: 'center',
                                    fontSize: '15',
                                }}
                            >
                                <button onClick={() => {
                                    onRejectTrade();
                                }}>Reject Trade</button>
                            </div>
                            {canEditTrade &&
                                <div id="edit-trade-button"
                                    style={{
                                        gridColumn: `1/3`,
                                        gridRow: '6/7',
                                        justifyContent: 'center',
                                        display: 'flex',
                                        flexDirection: 'row',
                                        alignItems: 'center',
                                        textAlign: 'center',
                                        fontSize: '15',
                                    }}
                                >
                                    <button onClick={() => {
                                        onEditTrade();
                                    }}>Counter offer</button>

                                </div>
                            }
                        </div>
                    }
                
                    <div id="player1confirmedoutline"
                        style={{
                            gridColumn: '1/2',
                            gridRow: '2/6',
                            background: '',
                            outlineColor: '',
                            border: `2px solid ${player1Accepted ? 'green' : 'black'}`
                        }}
                    >   
                    </div>
                    <div id="player2confirmedoutline"
                        style={{
                            gridColumn: '2/3',
                            gridRow: '2/6',
                            background: '',
                            outlineColor: '',
                            border: `2px solid ${player2Accepted ? 'green' : 'black'}`
                        }}
                    >   
                    </div>
            </div>
        </div>
    );


    
}