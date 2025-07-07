import React, { createContext, useContext, useEffect, useMemo, ReactNode } from 'react'
import { useGameStore } from './gameStore'
import { SocketService } from '../services/SocketService'
import { ECSProvider } from '../engine/ECS'

interface GameContextValue {
  socketService: SocketService
}

const GameContext = createContext<GameContextValue | null>(null)

interface GameProviderProps {
  children: ReactNode
}

export function GameProvider({ children }: GameProviderProps) {
  const setConnectionStatus = useGameStore(state => state.setConnectionStatus)
  const socketService = useMemo(() => new SocketService(), [])
  
  useEffect(() => {
    // WebSocket接続の初期化
    socketService.connect()
    
    // 接続状態の監視
    socketService.on('connect', () => {
      setConnectionStatus('connected')
    })
    
    socketService.on('disconnect', () => {
      setConnectionStatus('disconnected')
    })
    
    socketService.on('error', () => {
      setConnectionStatus('error')
    })
    
    return () => {
      socketService.disconnect()
    }
  }, [setConnectionStatus, socketService])
  
  const contextValue: GameContextValue = {
    socketService,
  }
  
  return (
    <GameContext.Provider value={contextValue}>
      <ECSProvider>
        {children}
      </ECSProvider>
    </GameContext.Provider>
  )
}

export function useGameContext() {
  const context = useContext(GameContext)
  if (!context) {
    throw new Error('useGameContext must be used within a GameProvider')
  }
  return context
}