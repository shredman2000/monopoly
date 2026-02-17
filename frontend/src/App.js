import React from 'react';
import { Routes, Route } from 'react-router-dom';
import MainMenu from './MainMenu';
import WaitingRoom from './WaitingRoom';
import Scene from './Scene';
import './App.css';
function App() {
  return (
    <div className='app-background'>
      <Routes>
        <Route path="/" element={<MainMenu />} />
        <Route path="/waiting" element={<WaitingRoom />} />
        <Route path="/scene" element={<Scene />} />
      </Routes>
    </div>
  );
}

export default App;