import React, { useRef } from 'react';
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


import useBoardScene from './hooks/useBoardScene';
import usePlayerState from './hooks/usePlayerState';
import useGameSocket from './hooks/useGameSocket';
function Scene() {
  const mountRef = useRef(null);
  const { gameId, username } = useLocation().state || {};


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

      {currentTileOptions?.type === 'property' && (
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
        <PostMovePanel
          gameState={gameState}
          username={username}
          isMyTurn={isMyTurn}
          onEndTurn={() => {
            WebSocketService.send('/app/finalizeTurn', { gameId, username });
            setInPostMoveState(false); // reset
          }}
        />
      )}
      
    </>
  );
}

export default Scene;
