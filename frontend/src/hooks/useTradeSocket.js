import { useEffect } from "react";
import WebSocketService from "../WebSocketService";

export default function useTradeSocket(gameId, setTradeState) {
  useEffect(() => {
    const subscription = WebSocketService.subscribe(`/topic/tradeUpdates/${gameId}`, (message) => {
      const parsed = JSON.parse(message.body);
      setTradeState(parsed);
    });

    return () => {
      if (subscription && subscription.unsubscribe) {
        subscription.unsubscribe();
      }
    };
  }, [gameId, setTradeState]);
}