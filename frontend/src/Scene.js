import React, { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';
import { FlyControls } from 'three/examples/jsm/controls/FlyControls.js';
import { BoardObject } from './BoardObject';
import { GamePiece } from './GamePiece';
import BuyPropertyPrompt from './components/BuyPropertyPrompt';
import ShowUserInfo from './components/ShowUserInfo';
import WebSocketService from './WebSocketService';
import UserPaysUser from './components/UserPaysUser';
import { useLocation } from 'react-router-dom';
import UserPaysTax from './components/UserPaysTax'
import UserRecievesMoney from './components/UserRecievesMoney';
import PassedGo from './components/PassedGo';
import AuctionPrompt from './components/AuctionPrompt';

function Scene() {
  const mountRef = useRef(null);
  const location = useLocation();
  const { gameId, username } = location.state || {};

  const playerMapRef = useRef({});
  const [turnIndex, setTurnIndex] = useState(0);
  const [playerUsernames, setPlayerUsernames] = useState([]);
  const [currentTileOptions, setCurrentTileOptions] = useState(null);
  const [userBalance, setUserBalance] = useState(1500); // if error initializing balance later check here
  const [ownedProperties, setOwnedProperties] = useState([]);
  const [paymentInfo, setPaymentInfo] = useState(null);
  const [hasRolled, setHasRolled] = useState(false);
  const [moveDone, setMoveDone] = useState(false);
  const [inPostMoveState, setInPostMoveState] = useState(false);
  const [passedGo, setPassedGo] = useState(false);
  const [userObj, setUser] = useState("");
  const [auctionData, setAuctionData] = useState(null);
  const [admin, setAdmin] = useState(null);

  const isMyTurn = playerUsernames[turnIndex] === username;

  useEffect(() => {
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(
      90,
      mountRef.current.clientWidth / mountRef.current.clientHeight,
      0.1,
      1000
    );
    camera.position.set(0, 15, 10);
    camera.lookAt(0, 0, 0);

    const renderer = new THREE.WebGLRenderer({ antialias: true });
    renderer.setSize(mountRef.current.clientWidth, mountRef.current.clientHeight);
    mountRef.current.appendChild(renderer.domElement);

    // flying controls for building
    /*
    const controls = new FlyControls(camera, renderer.domElement);
    controls.movementSpeed = 10;
    controls.rollSpeed = Math.PI / 24;
    controls.autoForward = false;
    controls.dragToLook = false;
    */
    const light = new THREE.DirectionalLight(0xffffff, 1);
    light.position.set(5, 10, 5);
    scene.add(light);

    const board = new BoardObject();
    board.getObject3D().rotation.x = -Math.PI / 2;
    scene.add(board.getObject3D());
    board.computeTileWorldPositions(scene);

    const colors = ['red', 'blue', 'green', 'yellow'];

    const renderGameState = (game) => {
      if (!game || !game.playerStates) return;
      
      
      setTurnIndex(game.turnIndex);
      setPlayerUsernames(game.playerUsernames);
      const isNowMyTurn = game.playerUsernames[game.turnIndex] === username;
      setHasRolled(!isNowMyTurn);

      game.playerStates.forEach((player, index) => {
        let piece = playerMapRef.current[player.username];
        if (!piece) {
          piece = new GamePiece(colors[index % colors.length]);
          scene.add(piece.getObject3D());
          playerMapRef.current[player.username] = piece;
        }

        // move piece to the position on the board
        const tilePos = board.tilePositions[player.position];
        const offset = new THREE.Vector3(index * 0.4, .2, 0);
        const pos = new THREE.Vector3().copy(tilePos).add(offset);
        piece.moveTo(pos);
        
        // look at piece whos turn it is
        if (player.username === game.playerUsernames[game.turnIndex]) {
          camera.position.set(pos.x + 5, pos.y + 10, pos.z + 5);
          camera.lookAt(pos);
        }

      });
      const currentPlayer = game.playerStates.find(p => p.username === username);
      if (currentPlayer) {
        setUserBalance(currentPlayer.money);
        const props = game.tileStates
          .filter(tile => tile.ownerUsername === username)
          .map(tile => tile.tileName);
        setOwnedProperties(props);
      }
    };

    WebSocketService.connect(() => {
      // subscribe to game updates
      WebSocketService.subscribe(`/topic/gameUpdates/${gameId}`, renderGameState);
      
      //subscribe to rolls
      // animate the user moving here 
      WebSocketService.subscribe(`/topic/rolled/${gameId}`, ({ username: rolledUser, roll, newPosition }) => {
        console.log(">>> /topic/rolled message received:", roll);
        // can also display what the player rolled.
        const numRolled = roll;
        const piece = playerMapRef.current[rolledUser];
        if (piece) {
          const index = playerUsernames.indexOf(rolledUser);
          const tilePos = board.tilePositions[newPosition];
          const offset = new THREE.Vector3(index * 0.4, 0.2, 0);
          const pos = new THREE.Vector3().copy(tilePos).add(offset);
          piece.moveTo(pos);
        }
        
          console.log(">>> sending to /handlePlayerLanding");
          WebSocketService.send("/app/handlePlayerLanding", {
            gameId: gameId.toString(),
            username: rolledUser,
            newPosition: newPosition.toString(),
            roll: roll,
          });


      });


      // subscribe to tile actions
      WebSocketService.subscribe(`/topic/tileAction/${gameId}`, (data) => {
        console.log("[tileAction] received:", data, "| this user:", username);
        
        //show toast for passing go and collecting 200 dollars
        if (data.passed_go === username) {
          setPassedGo(true);
          setUser(data.user);
        }

        // if it is a property and is unowned
        if (data.action === "offer_purchase" && data.player === username) {
          // Show Buy button in UI
          setCurrentTileOptions({
            type: "property",
            name: data.tileName,
            price: data.price,
          });
        }

        // if it is a property and is owned
        if (data.action === "pay_rent" && data.player === username) { /////////////////// if issue later look out for "username" here
          const rentToPay = data.rent;
          const userToPay = data.owner;

          if (data.type === "utility") {
            WebSocketService.send("/app/payUser", {
              gameId,
              fromUsername: username,
              toUsername: userToPay,
              amount: rentToPay,
            })
          }
          if (data.type === "property") {
            WebSocketService.send("/app/payUser", {
              gameId,
              fromUsername: username,
              toUsername: userToPay,
              amount: rentToPay,
            });
          }
          console.log("Paying" + userToPay + " $" +rentToPay);
      
          setPaymentInfo({ from: username, to: userToPay, amount: rentToPay });
          setTimeout(() => setPaymentInfo(null), 3000);

        }
        // if nothing to do
        if (data.action === "continue") {
            //
        }
        //luxury tax or income tax. just show toast 
        if (data.action === "pay_tax") {
          setPaymentInfo({ user: username, type: data.type, taxType: data.taxType, amount: data.amount, newFreeParkingTotal: data.freeparkingtotal });
          setTimeout(() => setPaymentInfo(null), 3000);
        }
        if (data.action === "free_parking") {
          setPaymentInfo({ user: username, type: data.type, amount: data.amount})
        }
      });

      WebSocketService.subscribe(`/topic/auctionUpdates/${gameId}`, (data) => {
        setAuctionData(data);
        if (data.action === "auction_won") {
          setAuctionData(null);
        }
      });

      // Get full game state on load
      WebSocketService.send(`/app/getGameState`, { gameId });
    });


    const clock = new THREE.Clock();
    const animate = () => {
      requestAnimationFrame(animate);
      //controls.update(clock.getDelta());
      renderer.render(scene, camera);
    };
    animate();

    return () => {
      if (mountRef.current && renderer.domElement.parentNode) {
        mountRef.current.removeChild(renderer.domElement);
      }
      renderer.dispose();
      WebSocketService.disconnect();
    };
  }, [gameId, username]);

  const handleRollDice = () => {
    if (isMyTurn && !hasRolled) {
      WebSocketService.send(`/app/rollDice`, { gameId, username, });
      setHasRolled(true);
    } else {
      alert("It's not your turn!");
    }
  };






  return (
    <>
      <div ref={mountRef} style={{ width: '100vw', height: '100vh', overflow: 'hidden' }} />
      {isMyTurn && !hasRolled &&(
        <button
          onClick={handleRollDice}
          style={{
            position: 'absolute',
            bottom: '2rem',
            left: '50%',
            transform: 'translateX(-50%)',
            padding: '1rem 2rem',
            fontSize: '1.2rem',
            backgroundColor: '#007BFF',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            zIndex: 10,
          }}
        >
          Roll Dice
        </button>
      )}

      {/*Show purchase property prompt */}
      {currentTileOptions?.type === "property" && (
        <BuyPropertyPrompt
          tileName={currentTileOptions.name}
          price={currentTileOptions.price}
          userBalance={userBalance}
          onBuy={() => {
            WebSocketService.send("/app/buyProperty", {
              gameId,
              username,
              tileName: currentTileOptions.name,
            });
            setCurrentTileOptions(null);
          }}
          onPass={() => {
            setCurrentTileOptions(null);
            WebSocketService.send("/app/auction", {
              gameId,
              tileName: currentTileOptions.name,
              username
            });
          }}
        />
      )}
      <ShowUserInfo
        username={username}
        balance={userBalance}
        ownedProperties={ownedProperties}
      />
      {auctionData && (
        <AuctionPrompt
          tileName={auctionData.tilename}
          currentBid={auctionData.currentbid}
          currentBidder={auctionData.currentbidder}
          isMyTurn={auctionData.currentbidder === username}
          
          onBid={(amount) => {
            WebSocketService.send("/app/auction/bid", {
              gameId,
              bidder: username,
              amount,
            });
          }}
          onPass={() => {
            WebSocketService.send("/app/auction/pass", {
              gameId,
              bidder: username,
            });
          }}
          userBalance = {userBalance}
        />
      )}

      {paymentInfo?.from && paymentInfo?.to && (
        <UserPaysUser from={paymentInfo.from} to={paymentInfo.to} amount={paymentInfo.amount} />
      )}
      {paymentInfo?.type  === "tax" && (<UserPaysTax user={paymentInfo.user} type={paymentInfo.taxType} amount={paymentInfo.amount} newFreeParkingTotal={paymentInfo.newFreeParkingTotal}/> )}
      {paymentInfo?.type === "free parking" && (<UserRecievesMoney user={paymentInfo.user} type={paymentInfo.type} amount={paymentInfo.amount}/> )}
      {passedGo && (<PassedGo user={userObj}/>)}
    </>
  );
}
export default Scene;
