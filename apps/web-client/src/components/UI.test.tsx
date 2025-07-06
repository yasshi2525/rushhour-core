import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { UI } from './UI'
import { GameProvider } from '../store/GameContext'

// テスト用のQueryClientを作成
const createTestQueryClient = () => new QueryClient({
  defaultOptions: {
    queries: { retry: false },
    mutations: { retry: false },
  },
})

describe('UI Component', () => {
  it('renders the game title', () => {
    const queryClient = createTestQueryClient()
    
    render(
      <QueryClientProvider client={queryClient}>
        <GameProvider>
          <UI />
        </GameProvider>
      </QueryClientProvider>
    )
    
    expect(screen.getByText('Rush Hour Railway')).toBeInTheDocument()
  })
  
  it('shows connection status', () => {
    const queryClient = createTestQueryClient()
    
    render(
      <QueryClientProvider client={queryClient}>
        <GameProvider>
          <UI />
        </GameProvider>
      </QueryClientProvider>
    )
    
    expect(screen.getByText(/disconnected|connecting|connected/)).toBeInTheDocument()
  })
  
  it('displays start game button when in menu state', () => {
    const queryClient = createTestQueryClient()
    
    render(
      <QueryClientProvider client={queryClient}>
        <GameProvider>
          <UI />
        </GameProvider>
      </QueryClientProvider>
    )
    
    expect(screen.getByText('ゲーム開始')).toBeInTheDocument()
  })
})