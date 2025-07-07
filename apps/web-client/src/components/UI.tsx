import { useGameStore } from '../store/gameStore'
import './UI.css'

export function UI() {
  const { 
    gameState, 
    startGame, 
    pauseGame, 
    connectionStatus 
  } = useGameStore()
  
  return (
    <div className="ui-overlay">
      {/* ヘッダー */}
      <header className="ui-header">
        <h1>Rush Hour Railway</h1>
        <div className="connection-status">
          <span className={`status-indicator ${connectionStatus}`}>
            {connectionStatus === 'connected' ? '🟢' : 
             connectionStatus === 'connecting' ? '🟡' : '🔴'}
          </span>
          {connectionStatus}
        </div>
      </header>
      
      {/* メインコントロール */}
      <div className="ui-controls">
        {gameState === 'menu' && (
          <div className="menu">
            <button onClick={startGame} className="btn-primary">
              ゲーム開始
            </button>
          </div>
        )}
        
        {gameState === 'playing' && (
          <div className="game-controls">
            <button onClick={pauseGame} className="btn-secondary">
              一時停止
            </button>
            <div className="game-stats">
              <div>乗客数: 0</div>
              <div>収益: ¥0</div>
            </div>
          </div>
        )}
        
        {gameState === 'paused' && (
          <div className="pause-menu">
            <button onClick={startGame} className="btn-primary">
              再開
            </button>
          </div>
        )}
      </div>
      
      {/* ツールバー */}
      <div className="ui-toolbar">
        <div className="tool-group">
          <button className="tool-btn">🚂 電車</button>
          <button className="tool-btn">🏢 駅</button>
          <button className="tool-btn">🛤️ 線路</button>
        </div>
      </div>
      
      {/* 情報パネル */}
      <div className="ui-info">
        <div className="info-panel">
          <h3>選択中</h3>
          <p>何も選択されていません</p>
        </div>
      </div>
    </div>
  )
}