import React from 'react';
import './UserPaysTax.css';

export default function UserRecievesMoney({ user, type, amount}) {
  return (
    <div className="toast-container">
      <p>{user} recieved ${amount} from {type}!</p>
    </div>
  );
}