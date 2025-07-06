import { create } from 'zustand'
import { devtools } from 'zustand/middleware'
import { immer } from 'zustand/middleware/immer'

export type GameState = 'menu' | 'loading' | 'playing' | 'paused' | 'ended'
export type ConnectionStatus = 'disconnected' | 'connecting' | 'connected' | 'error'

interface GameStore {
  // ゲーム状態
  gameState: GameState
  connectionStatus: ConnectionStatus
  
  // 統計
  playerCount: number
  trainCount: number
  stationCount: number
  revenue: number
  
  // アクション
  startGame: () => void
  pauseGame: () => void
  endGame: () => void
  setConnectionStatus: (status: ConnectionStatus) => void
  updateStats: (stats: Partial<{
    playerCount: number
    trainCount: number
    stationCount: number
    revenue: number
  }>) => void
}

export const useGameStore = create<GameStore>()(
  devtools(
    immer((set) => ({
      // 初期状態
      gameState: 'menu',
      connectionStatus: 'disconnected',
      playerCount: 0,
      trainCount: 0,
      stationCount: 0,
      revenue: 0,
      
      // アクション
      startGame: () => set((state) => {
        state.gameState = 'playing'
      }),
      
      pauseGame: () => set((state) => {
        state.gameState = 'paused'
      }),
      
      endGame: () => set((state) => {
        state.gameState = 'ended'
      }),
      
      setConnectionStatus: (status) => set((state) => {
        state.connectionStatus = status
      }),
      
      updateStats: (stats) => set((state) => {
        Object.assign(state, stats)
      }),
    })),
    {
      name: 'game-store',
    }
  )
)