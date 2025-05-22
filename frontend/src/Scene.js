import React, { useRef } from 'react';
import { useState } from 'react';
import { useLocation } from 'react-router-dom';
import BuyPropertyPrompt from './components/BuyPropertyPrompt';
import ShowUserInfo from './components/ShowUserInfo';
import AuctionPrompt from './components/AuctionPrompt';
import UserPaysUser from './components/UserPaysUser';
import UserPaysTax from './components/UserPaysTax';
import UserRecievesMoney from './components/UserRecievesMoney';
import PassedGo from './components/PassedGo';
import WebSocketService from './WebSocketService';
import PostMovePanel from './components/PostMovePanel';
import TradePlayerPanel from './components/TradePlayerPanel';


import useBoardScene from './hooks/useBoardScene';
import usePlayerState from './hooks/usePlayerState';
import useGameSocket from './hooks/useGameSocket';
import MiniBoard from './components/MiniBoard';
import TileDetailsPanel from './components/TileDetailsPanel';
import { useSquareSize } from './hooks/useSquareSize';

import './scene.css';
import TradePanel from './components/TradePanel';
function Scene() {
  const mountRef = useRef(null);
  const { gameId, username } = useLocation().state || {};
  const [selectedTile, setSelectedTile] = useState(null);
  const [miniBoardRef, miniBoardSize] = useSquareSize();
  const [canTrade, setCanTrade] = useState(null);
  const [trading, setTrading] = useState(null);
  const [tradingPlayer, setTradingPlayer] = useState(null);
  const [tradeState, setTradeState] = useState(null);


  const {
    turnIndex,
    setTurnIndex,
    playerUsernames,
    setPlayerUsernames,
    userBalance,
    setUserBalance,
    ownedProperties,
    setOwnedProperties,
    hasRolled,
    setHasRolled,
    currentTileOptions,
    setCurrentTileOptions,
    paymentInfo,
    setPaymentInfo,
    passedGo,
    setPassedGo,
    userObj,
    setUser,
    auctionData,
    setAuctionData,
    isMyTurn,
    playerMapRef,
    canRoll,
    setCanRoll,
    inPostMoveState,
    setInPostMoveState,
    gameState,
    setGameState,
  } = usePlayerState(username);

  // create board camera and scene stuff
  const { sceneRef, cameraRef, boardRef } = useBoardScene(mountRef);

  useGameSocket({
    gameId,
    username,
    playerUsernames,
    setTurnIndex,
    setPlayerUsernames,
    setHasRolled,
    setUserBalance,
    setOwnedProperties,
    setCurrentTileOptions,
    setPaymentInfo,
    setPassedGo,
    setUser,
    setAuctionData,
    setInPostMoveState,
    setCanRoll,
    playerMapRef,
    sceneRef,
    cameraRef,
    boardRef,
    setGameState,
    setTradeState,
  });

  const handleRollDice = () => {
    if (canRoll) {
      WebSocketService.send('/app/rollDice', { gameId, username });
      setHasRolled(true);
    }
  };

  return (
    <>
      <div ref={mountRef} style={{ width: '100vw', height: '100vh', overflow: 'hidden' }} />

      {canRoll && !currentTileOptions && !inPostMoveState &&(
        <button
          onClick={handleRollDice}
          style={{
            position: 'absolute',
            bottom: '2rem',
            left: '50%',
            transform: 'translateX(-50%)',
            padding: '1rem 2rem',
            fontSize: '1.2rem',
            backgroundColor: '#007BFF',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            zIndex: 10,
          }}
        >
          Roll Dice
        </button>
      )}

      {['property', 'railroad', 'utility'].includes(currentTileOptions?.type) && (
        <BuyPropertyPrompt
          tileName={currentTileOptions.name}
          price={currentTileOptions.price}
          userBalance={userBalance}
          onBuy={() => {
            WebSocketService.send('/app/buyProperty', { gameId, username, tileName: currentTileOptions.name });
            setCurrentTileOptions(null);
          }}
          onPass={() => {
            WebSocketService.send('/app/auction', { gameId, username, tileName: currentTileOptions.name });
            setCurrentTileOptions(null);
          }}
        />
      )}

      <ShowUserInfo username={username} balance={userBalance} ownedProperties={ownedProperties} />

      {auctionData && (
        <AuctionPrompt
          tileName={auctionData.tilename}
          currentBid={auctionData.currentbid}
          currentBidder={auctionData.currentbidder}
          isMyTurn={auctionData.currentbidder === username}
          onBid={(amount) => WebSocketService.send('/app/auction/bid', { gameId, bidder: username, amount })}
          onPass={() => WebSocketService.send('/app/auction/pass', { gameId, bidder: username })}
          userBalance={userBalance}
        />
      )}

      {paymentInfo?.from && paymentInfo?.to && <UserPaysUser {...paymentInfo} />}
      {paymentInfo?.type === 'tax' && <UserPaysTax {...paymentInfo} />}
      {paymentInfo?.type === 'free parking' && <UserRecievesMoney {...paymentInfo} />}
      {passedGo && <PassedGo user={userObj} />}


      
    {inPostMoveState && (
      <div className="scene-overlay-grid">
        <div
        ref={miniBoardRef}
        style={{
          gridColumn: '2 / 3',
          gridRow: '1 / 2',
          width: '100%',
          height: '100%',
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'center',
          boxSizing: 'border-box',
          padding: '1rem',
        }}
      >
        <div
          style={{
            width: '100%',
            aspectRatio: '1',
            maxWidth: '100%',
            maxHeight: '100%',
            background: 'rgba(255, 255, 255, 0.85)',
            padding: '0.5rem',
            borderRadius: '6px',
            border: '1px solid #444',
            boxSizing: 'border-box',
          }}
        >
          <MiniBoard
            tileStates={gameState.tileStates}
            currentUsername={username}
            onTileClick={(tile) => setSelectedTile(tile)}
          />
        </div>
      </div>
   



      {selectedTile && (
        <TileDetailsPanel
          tile={selectedTile}
          username={username}
          onClose={() => setSelectedTile(null)}
          onHouseBuild={(tile) => {
            WebSocketService.send('/app/buildHouse', {
              gameId,
              username,
              tileName: tile.tileName,
            });
            setSelectedTile(null);
          }}
        />
      )}


      <div className="post-move-wrapper" style={{ pointerEvents: 'auto' }}>
        <PostMovePanel
          gameState={gameState}
          username={username}
          isMyTurn={isMyTurn}
          onClickTrade={() => setTrading(true)}
          onEndTurn={() => {
            WebSocketService.send('/app/finalizeTurn', { gameId, username });
            setInPostMoveState(false);
            setSelectedTile(null);
          }}
        />
      </div>

      {trading && (
        <div>
          <TradePanel
            currentUsername={username}
            gameState={gameState}
            onTradingWithPlayer={(name) => {
              WebSocketService.send('/app/startTrade', { gameId, username, name })
              setTrading(false);
              setTradingPlayer(name);
            }}
          />
        </div>
      )}

      {tradingPlayer && (
        <div>
          <TradePlayerPanel
            gameState={gameState}
            otherPlayer={tradingPlayer}
            currentUsername={username}
            tradeState={tradeState}

            onAddTileToTrade={(tileName, tileOwner) => {
              WebSocketService.send('/app/addTile', {gameId, tileOwner, tileName});
            }}
          />

        </div>
      )}


    </div>
    )}


      
    </>
  );
}

export default Scene;
