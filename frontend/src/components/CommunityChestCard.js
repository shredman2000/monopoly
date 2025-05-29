import React, { useRef, useEffect } from 'react';
import * as THREE from 'three';

export default function CommunityChestCard({ info, onFinish }) {
  const canvasRef = useRef();

  useEffect(() => {
    const width = window.innerWidth;
    const height = window.innerHeight;
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ canvas: canvasRef.current, alpha: true });
    renderer.setSize(width, height);

    const textCanvas = document.createElement('canvas');
    textCanvas.width = 1024;
    textCanvas.height = 512;
    const ctx = textCanvas.getContext('2d');
    ctx.fillStyle = 'orange';
    ctx.fillRect(0, 0, textCanvas.width, textCanvas.height);

    ctx.fillStyle = 'black';
    ctx.font = 'bold 48px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';

    if (info.money) {
        ctx.fillText(info.text, textCanvas.width / 2, textCanvas.height / 2);
        ctx.fillText(`$Recieve: ${info.money}`, textCanvas.width / 2, textCanvas.height * 3 / 4);
    }
    if (info.paymoney) {
        ctx.fillText(info.text, textCanvas.width / 2, textCanvas.height / 2);
        ctx.fillText(`$Pay: ${info.paymoney}`, textCanvas.width / 2, textCanvas.height * 3 / 4);
    }
    if (info.type === 'advancetogo') {
        ctx.fillText(info.text, textCanvas.width / 2, textCanvas.height / 2);
        ctx.fillText(`Collect $200!`, textCanvas.width / 2, textCanvas.height * 3 / 4);
    }
    if (info.type === 'gotojail') {

    }
    if (info.type === 'collectfromplayers') {
        ctx.fillText(info.text, textCanvas.width / 2, textCanvas.height / 2);
    }
    if (info.type === 'advancerandomtile') {
        ctx.fillText(info.text, textCanvas.width / 2, textCanvas.height / 2);
        ctx.fillText(`If you pass go, collect $200`, textCanvas.width / 2, textCanvas.height * 3 / 4);
    }

    
    const texture = new THREE.CanvasTexture(textCanvas);

    camera.position.z = 5;

    const geometry = new THREE.PlaneGeometry(3, 2);
    const material = new THREE.MeshBasicMaterial({ map: texture });
    const card = new THREE.Mesh(geometry, material);
    scene.add(card);

    // flip card animation
    let angle = 0;
    const animate = () => {
      requestAnimationFrame(animate);
      if (angle < Math.PI) {
        card.rotation.y += 0.001;
        angle += 0.1;
      }
      else if (angle < Math.PI + 0.1) {
            angle = Math.PI + 0.1; // Prevent repeated calls
            if (onFinish) {
                setTimeout(onFinish, 1000);
            }
        }
      renderer.render(scene, camera);
    };
    animate();

    return () => {
      renderer.dispose();
    };
  },
  []);

  return (
    <canvas
      ref={canvasRef}
      style={{
        width: '100vw',
        height: '100vh',
        position: 'absolute',
        top: 0,
        left: 0,
        zIndex: 1000,
        pointerEvents: 'none',
      }}
    />
  );
}
