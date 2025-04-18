import React from 'react';
import './UserPaysTax.css';

export default function UserPaysTax({ user, type, amount, newFreeParkingTotal }) {
  return (
    <div className="toast-container">
      <p>{user} paid ${amount} {type} tax!</p>
      <p>New free parking total: ${newFreeParkingTotal}</p>
    </div>
  );
}