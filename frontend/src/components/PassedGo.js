import React from 'react';
import './PassedGo.css';

export default function PassedGo({ user }) {
  return (
    <div className="toast-container">
      <p>{user} passed go!</p>
      <p>Collected $200</p>
    </div>
  );
}