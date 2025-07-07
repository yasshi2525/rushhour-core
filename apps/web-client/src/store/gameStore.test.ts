import { describe, it, expect, beforeEach } from 'vitest'
import { useGameStore } from './gameStore'

describe('Game Store', () => {
  beforeEach(() => {
    // ストアをリセット
    useGameStore.setState({
      gameState: 'menu',
      connectionStatus: 'disconnected',
      playerCount: 0,
      trainCount: 0,
      stationCount: 0,
      revenue: 0,
    })
  })
  
  it('should have initial state', () => {
    const state = useGameStore.getState()
    
    expect(state.gameState).toBe('menu')
    expect(state.connectionStatus).toBe('disconnected')
    expect(state.playerCount).toBe(0)
    expect(state.trainCount).toBe(0)
    expect(state.stationCount).toBe(0)
    expect(state.revenue).toBe(0)
  })
  
  it('should start game', () => {
    const { startGame } = useGameStore.getState()
    
    startGame()
    
    const state = useGameStore.getState()
    expect(state.gameState).toBe('playing')
  })
  
  it('should pause game', () => {
    const { startGame, pauseGame } = useGameStore.getState()
    
    startGame()
    pauseGame()
    
    const state = useGameStore.getState()
    expect(state.gameState).toBe('paused')
  })
  
  it('should end game', () => {
    const { startGame, endGame } = useGameStore.getState()
    
    startGame()
    endGame()
    
    const state = useGameStore.getState()
    expect(state.gameState).toBe('ended')
  })
  
  it('should update connection status', () => {
    const { setConnectionStatus } = useGameStore.getState()
    
    setConnectionStatus('connected')
    
    const state = useGameStore.getState()
    expect(state.connectionStatus).toBe('connected')
  })
  
  it('should update stats', () => {
    const { updateStats } = useGameStore.getState()
    
    updateStats({
      playerCount: 5,
      trainCount: 10,
      revenue: 1000,
    })
    
    const state = useGameStore.getState()
    expect(state.playerCount).toBe(5)
    expect(state.trainCount).toBe(10)
    expect(state.revenue).toBe(1000)
    expect(state.stationCount).toBe(0) // 更新されていない値はそのまま
  })
})