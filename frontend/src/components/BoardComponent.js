import React, {useRef, useEffect, useState } from 'react';
import './BoardComponent.css';

function BoardComponent({ gameState, onMoveComplete, roll }) {
    const [moving, setMoving] = useState(false);
    const boardRef = useRef();
    const [tilePositions, setTilePositions] = useState([]);

    const tileData = [
        { name: "GO", color: null },
        { name: "Mediterranean Avenue", color: "#8B4513" },
        { name: "Community Chest", color: null },
        { name: "Baltic Avenue", color: "#8B4513" },
        { name: "Income Tax", color: null },
        { name: "Reading Railroad", color: "black" },
        { name: "Oriental Avenue", color: "#ADD8E6" },
        { name: "Chance", color: null },
        { name: "Vermont Avenue", color: "#ADD8E6" },
        { name: "Connecticut Avenue", color: "#ADD8E6" },
        { name: "Jail / Just Visiting", color: null },

        { name: "St. Charles Place", color: "#FF00FF" },
        { name: "Electric Company", color: null },
        { name: "States Avenue", color: "#FF00FF" },
        { name: "Virginia Avenue", color: "#FF00FF" },
        { name: "Pennsylvania Railroad", color: "black" },
        { name: "St. James Place", color: "#FFA500" },
        { name: "Community Chest", color: null },
        { name: "Tennessee Avenue", color: "#FFA500" },
        { name: "New York Avenue", color: "#FFA500" },
        { name: "Free Parking", color: null },

        { name: "Kentucky Avenue", color: "#FF0000" },
        { name: "Chance", color: null },
        { name: "Indiana Avenue", color: "#FF0000" },
        { name: "Illinois Avenue", color: "#FF0000" },
        { name: "B&O Railroad", color: "black" },
        { name: "Atlantic Avenue", color: "#FFFF00" },
        { name: "Ventnor Avenue", color: "#FFFF00" },
        { name: "Water Works", color: null },
        { name: "Marvin Gardens", color: "#FFFF00" },
        { name: "Go to Jail", color: null },

        { name: "Pacific Avenue", color: "#008000" },
        { name: "North Carolina Avenue", color: "#008000" },
        { name: "Community Chest", color: null },
        { name: "Pennsylvania Avenue", color: "#008000" },
        { name: "Short Line", color: "black" },
        { name: "Chance", color: null },
        { name: "Park Place", color: "#0000FF" },
        { name: "Luxury Tax", color: null },
        { name: "Boardwalk", color: "#0000FF" }
    ];

    const gridSize = 11;
    const positions = [];

    //top row:
    for (let i = 0; i < 11; i++) {
        positions.push({row: 11, col: 11 - i});
    }
    // Right column (top to bottom, skipping corners)
    for (let i = 1; i < 10; i++) {
        positions.push({ row: 11 - i, col: 1 });
    }

    // Bottom row (right to left)
    for (let i = 0; i < 11; i++) {
        positions.push({ row: 1, col: i + 1 });
    }

    // Left column (bottom to top)
    for (let i = 1; i < 10; i++) {
        positions.push({ row: i + 1, col: 11 });
    }
    // retrieve pixel positions of tiles after render
    useEffect(() => {
        if (!boardRef.current) { return }

        const boardRect = boardRef.current.getBoundingClientRect();
        const tileSize = boardRect.width / gridSize;

        const coords = positions.map(pos => ({
            x: (pos.col - .5) * tileSize,
            y: (pos.row - .5) * tileSize
        }));
        setTilePositions(coords);

    }, [])


    const handleTransitionEnd = () => {
        setMoving(false);
        if (onMoveComplete) { onMoveComplete(); }
    }

    const [animatedPositions, setAnimatedPositions] = useState({}); // username = current tile id

    // Initialize animated positions
    useEffect(() => {
        if (!tilePositions.length) return;


        setAnimatedPositions(prev => {
            if (Object.keys(prev).length > 0) return prev;

            const initPos = {};
            gameState?.playerStates?.forEach(p => {
                initPos[p.username] = p.position;
            });
            return initPos;
        });
    }, [tilePositions, gameState]);

    
    useEffect(() => {
        if (!roll || !tilePositions.length) return;

        const { player, newPosition } = roll;
        const currentPos = animatedPositions[player];
        if (currentPos === undefined || currentPos === -1) return;

        const steps = [];
        const tiles = (newPosition - currentPos + 40) % 40;
        for (let i = 1; i <= tiles; i++) {
            steps.push((currentPos + i) % 40);
        }

        let stepIndex = 0;

        const moveStep = () => {
            if (stepIndex >= steps.length) return;
            setAnimatedPositions(prev => ({
            ...prev,
            [player]: steps[stepIndex]
            }));
            stepIndex++;
        };


        moveStep();

        const interval = setInterval(() => {
            if (stepIndex >= steps.length) {
            clearInterval(interval);
            return;
            }
            moveStep();
        }, 500);

        return () => clearInterval(interval);
    }, [roll, tilePositions]);

    return (
    <div>
            <div className='board-wrapper' ref={boardRef}>
                {tileData.map((tile, idx) => {
                    const pos = positions[idx];
                    return (
                        <div className='tile'
                            key={tile.name + idx}
                            style={{
                                gridRow: pos?.row,
                                gridColumn: pos?.col,
                                backgroundColor: tile.color ?? '#eee',
                                border: '1px solid #333',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center',
                                fontSize: '0.6rem',
                                textAlign: 'center',
                                position: 'relative'
                            }}
                        >
                            <div className='tile-name'>{tile.name}</div>
                        </div>
                    )
                })}
                <div className='player-overlay'>
                    <div className='player-piece'></div>
                    {tilePositions.length > 0 && gameState?.playerStates?.map((player, idx) => {
                        const tileIdx = animatedPositions[player.username]
                        if (tileIdx === undefined || tileIdx === -1) { 
                            console.log("exiting from tileIdx being -1");
                            return null; 
                        }

                        const {x,y} = tilePositions[tileIdx];

                        return (
                            <div className='player-piece'
                                key={player.username}
                                style={{
                                    position: 'absolute',
                                    width: '20px',
                                    height: '20px',
                                    borderRadius: '50%',
                                    backgroundColor: player.color || 'red',
                                    left: `${x}px`,
                                    top: `${y}px`,
                                    transform: 'translate(-50%, -50%)',
                                    transition: 'all 0.3s ease'

                                }}
                                onTransitionStart={() => setMoving(true)}
                                onTransitionEnd={() => {
                                    if (animatedPositions[player.username] === roll.newPosition) {
                                        onMoveComplete();
                                    }
                                }}
                                title={player.username}
                            />
                        )
                    })}
                </div>
            </div>
        </div>

  );
}
export default BoardComponent;