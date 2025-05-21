import React from 'react';
import './ShowUserInfo.css';

export default function ShowUserInfo({ username, balance, ownedProperties }) {
  return (
    <div className="user-info-box">
      <p><strong>Balance:</strong> ${balance}</p>
      <p><strong>Properties:</strong></p>
      <ul>
        {ownedProperties.length === 0 ? (
          <li>No properties</li>
        ) : (
          ownedProperties.map((prop, idx) => <li key={idx}>{prop}</li>)
        )}
      </ul>
    </div>
  );
}