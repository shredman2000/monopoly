// BuyPropertyPrompt.js
import React from 'react';
import './BuyPropertyPrompt.css';

export default function BuyPropertyPrompt({ tileName, price, userBalance, onBuy, onPass }) {
    return (
      <div className="tile-popup">
        <p>Buy {tileName} for ${price}?</p>
        <button
          disabled={userBalance < price} 
          onClick={onBuy}>Buy
        </button>
        <button onClick={onPass}>Pass</button>
      </div>
    );
  }