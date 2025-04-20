import React from 'react';
import './AuctionPrompt.css'; 

export default function AuctionPrompt({
  tileName,
  currentBid,
  currentBidder,
  isMyTurn,
  onBid,
  onPass,
  userBalance, 
}) {
  const increments = [5, 20, 50, 100];

  return (
    <div className="auction-container">
      <h2>Auction: {tileName}</h2>
      <p>Current Bid: ${currentBid}</p>
      <p>Current Bidder: {currentBidder}</p>

      {isMyTurn ? (
        <>
          <p>Your Turn to Bid:</p>
          <div className="bid-buttons">
            {increments.map((amount) => (
              <button
                key={amount}
                disabled={userBalance < currentBid + amount}
                onClick={() => onBid(amount)}
              >
                +${amount}
              </button>
            ))}
          </div>
          <button className="pass-button" onClick={onPass}>Pass</button>
        </>
      ) : (
        <p>Waiting for {currentBidder}...</p>
      )}
    </div>
  );
}
