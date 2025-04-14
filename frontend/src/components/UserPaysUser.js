import React from 'react';
import './UserPaysUser.css';

export default function UserPaysUser({ from, to, amount }) {
  return (
    <div className="toast-container">
      <p>{from} paid ${amount} to {to}</p>
    </div>
  );
}