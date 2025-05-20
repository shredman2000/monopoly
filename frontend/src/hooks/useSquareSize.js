import { useState, useEffect, useRef } from 'react';

export function useSquareSize() {
  const ref = useRef(null);
  const [size, setSize] = useState(0);

  useEffect(() => {
    const element = ref.current;
    if (!element) return;

    const resize = () => {
      const rect = element.getBoundingClientRect();
      setSize(Math.min(rect.width, rect.height));
    };

    // Resize immediately after mount
    resize();

    // Use ResizeObserver for responsive resizing
    const observer = new ResizeObserver(resize);
    observer.observe(element);

    return () => observer.disconnect();
  }, []);

  return [ref, size];
}
