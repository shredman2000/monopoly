import { useEffect } from 'react';
import WebSocketService from '../WebSocketService';
import { GamePiece } from '../GamePiece';
import * as THREE from 'three';

export default function useGameSocket({
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
  playerMapRef,
  sceneRef,
  cameraRef,
  boardRef,
  setCanRoll,
  setGameState,
  setInPostMoveState,
}) {
  useEffect(() => {
    if (!gameId || !username) return;

    WebSocketService.connect(() => {
      WebSocketService.subscribe(`/topic/gameUpdates/${gameId}`, (game) => {
        if (!game || !game.playerStates) return;

        setGameState(game); // store full game image

        setTurnIndex(game.turnIndex);
        setPlayerUsernames(game.playerUsernames);

        const isNowMyTurn = game.playerUsernames[game.turnIndex] === username;
        if (isNowMyTurn) {
          setHasRolled(false);
        }

        game.playerStates.forEach((player, index) => {
          let piece = playerMapRef.current[player.username];
          if (!piece) {
            piece = new GamePiece(new THREE.Color(player.color));
            sceneRef.current.add(piece.getObject3D());
            playerMapRef.current[player.username] = piece;
          }

          const tilePos = boardRef.current.tilePositions[player.position];
          const offset = new THREE.Vector3(index * 0.4, 0.2, 0);
          const pos = new THREE.Vector3().copy(tilePos).add(offset);
          piece.moveTo(pos);

          if (player.username === game.playerUsernames[game.turnIndex]) {
            cameraRef.current.position.set(pos.x + 5, pos.y + 10, pos.z + 5);
            cameraRef.current.lookAt(pos);
          }
        });

        const currentPlayer = game.playerStates.find(p => p.username === username);
        if (currentPlayer) {
          setUserBalance(currentPlayer.money);
          setCanRoll(currentPlayer.canRoll);
          const turnUsername = game.playerUsernames[game.turnIndex];
          const turnPlayer = game.playerStates.find(p => p.username === turnUsername);
          const postMoveActive = turnPlayer?.inPostMove ?? false;

          setInPostMoveState(postMoveActive);
          const props = game.tileStates
            .filter(tile => tile.ownerUsername === username)
            .map(tile => tile.tileName);
          setOwnedProperties(props);
        }
      });

      WebSocketService.subscribe(`/topic/rolled/${gameId}`, ({ username: rolledUser, roll, newPosition }) => {
        const piece = playerMapRef.current[rolledUser];
        const index = playerUsernames.indexOf(rolledUser);
        const tilePos = boardRef.current.tilePositions[newPosition];
        const offset = new THREE.Vector3(index * 0.4, 0.2, 0);
        const pos = new THREE.Vector3().copy(tilePos).add(offset);
        if (piece) piece.moveTo(pos);

        WebSocketService.send('/app/handlePlayerLanding', {
          gameId: gameId.toString(),
          username: rolledUser,
          newPosition: newPosition.toString(),
          roll,
        });
      });

      WebSocketService.subscribe(`/topic/tileAction/${gameId}`, (data) => {
        if (data.passed_go === username) {
          setPassedGo(true);
          setUser(data.user);
        }

        if (data.action === 'offer_purchase' && data.player === username) {
          setCanRoll(false);
          setCurrentTileOptions({
            type: data.type || 'property',
            name: data.tileName,
            price: data.price,
          });
        }

        if (data.action === 'pay_rent' && data.player === username) {
          WebSocketService.send('/app/payUser', {
            gameId,
            fromUsername: username,
            toUsername: data.owner,
            amount: data.rent,
          });
          setPaymentInfo({ from: username, to: data.owner, amount: data.rent });
          setTimeout(() => setPaymentInfo(null), 3000);
        }

        if (data.action === 'pay_tax') {
          setPaymentInfo({
            user: username,
            type: data.type,
            taxType: data.taxType,
            amount: data.amount,
            newFreeParkingTotal: data.freeparkingtotal,
          });
          setTimeout(() => setPaymentInfo(null), 3000);
        }

        if (data.action === 'free_parking') {
          setPaymentInfo({ user: username, type: data.type, amount: data.amount });
        }
      });

      WebSocketService.subscribe(`/topic/auctionUpdates/${gameId}`, (data) => {
        if (data.action === 'auction_won') {
          setAuctionData(null);
        } else {
          setAuctionData(data);
        }
      });

      WebSocketService.send('/app/getGameState', { gameId });
    });

    return () => {
      WebSocketService.disconnect();
    };
  }, [gameId, username]);
}
