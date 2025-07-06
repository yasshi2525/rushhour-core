import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { GameCanvas } from './components/GameCanvas'
import { UI } from './components/UI'
import { GameProvider } from './store/GameContext'
import './App.css'

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5分
      cacheTime: 10 * 60 * 1000, // 10分
    },
  },
})

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <GameProvider>
        <div className="app">
          <GameCanvas />
          <UI />
        </div>
      </GameProvider>
    </QueryClientProvider>
  )
}

export default App