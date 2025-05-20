import { useRef, useState } from 'react';

export default function usePlayerState(username) {
    const playerMapRef = useRef({});

    const [turnIndex, setTurnIndex] = useState(0);
    const [playerUsernames, setPlayerUsernames] = useState([]);
    const [userBalance, setUserBalance] = useState(1500);
    const [ownedProperties, setOwnedProperties] = useState([]);
    const [hasRolled, setHasRolled] = useState(false);

    const [currentTileOptions, setCurrentTileOptions] = useState(null);
    const [paymentInfo, setPaymentInfo] = useState(null);
    const [passedGo, setPassedGo] = useState(false);
    const [userObj, setUser] = useState("");
    const [auctionData, setAuctionData] = useState(null);
    const [canRoll, setCanRoll] = useState(false)

    const isMyTurn = playerUsernames[turnIndex] === username;

    return {
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
    };
}
