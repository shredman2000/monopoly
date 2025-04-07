import React from 'react';
import { Routes, Route } from 'react-router-dom';
import MainMenu from './MainMenu';
import WaitingRoom from './WaitingRoom';
import Scene from './Scene';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainMenu />} />
      <Route path="/waiting" element={<WaitingRoom />} />
      <Route path="/scene" element={<Scene />} />
    </Routes>
  );
}

export default App;