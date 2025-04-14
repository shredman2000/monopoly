// BuyPropertyPrompt.js
import React from 'react';
import './BuyPropertyPrompt.css';

export default function BuyPropertyPrompt({ tileName, price, onBuy, onPass }) {
    return (
      <div className="tile-popup">
        <p>Buy {tileName} for ${price}?</p>
        <button onClick={onBuy}>Buy</button>
        <button onClick={onPass}>Pass</button>
      </div>
    );
  }