import { io, Socket } from 'socket.io-client'

export interface GameEvent {
  type: string
  data: any
  timestamp: number
}

export interface TrainPosition {
  id: number
  x: number
  y: number
  z: number
  speed: number
  rotation: number
}

export interface StationUpdate {
  id: number
  waitingPassengers: number
  totalPassengers: number
}

export class SocketService {
  private socket: Socket | null = null
  private reconnectAttempts = 0
  private maxReconnectAttempts = 5
  private reconnectDelay = 1000
  
  connect(): void {
    const serverUrl = import.meta.env.VITE_SERVER_URL || 'http://localhost:8080'
    
    this.socket = io(serverUrl, {
      transports: ['websocket', 'polling'],
      autoConnect: true,
      reconnection: true,
      reconnectionAttempts: this.maxReconnectAttempts,
      reconnectionDelay: this.reconnectDelay,
    })
    
    this.setupEventListeners()
  }
  
  disconnect(): void {
    if (this.socket) {
      this.socket.disconnect()
      this.socket = null
    }
  }
  
  private setupEventListeners(): void {
    if (!this.socket) return
    
    this.socket.on('connect', () => {
      console.log('Connected to game server')
      this.reconnectAttempts = 0
    })
    
    this.socket.on('disconnect', (reason) => {
      console.log('Disconnected from game server:', reason)
      
      if (reason === 'io server disconnect') {
        // サーバーが切断した場合は手動で再接続
        this.attemptReconnection()
      }
    })
    
    this.socket.on('connect_error', (error) => {
      console.error('Connection error:', error)
      this.attemptReconnection()
    })
    
    // ゲーム固有のイベントリスナー
    this.socket.on('train-position-update', this.handleTrainPositionUpdate.bind(this))
    this.socket.on('station-update', this.handleStationUpdate.bind(this))
    this.socket.on('game-state-changed', this.handleGameStateChanged.bind(this))
    this.socket.on('player-joined', this.handlePlayerJoined.bind(this))
    this.socket.on('player-left', this.handlePlayerLeft.bind(this))
  }
  
  private attemptReconnection(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      
      setTimeout(() => {
        console.log(`Reconnection attempt ${this.reconnectAttempts}`)
        this.connect()
      }, this.reconnectDelay * this.reconnectAttempts)
    } else {
      console.error('Max reconnection attempts reached')
    }
  }
  
  // イベントハンドラー
  private handleTrainPositionUpdate(data: TrainPosition[]): void {
    // ECSシステムの電車位置を更新
    console.log('Train positions updated:', data)
  }
  
  private handleStationUpdate(data: StationUpdate[]): void {
    // 駅の状態を更新
    console.log('Station updated:', data)
  }
  
  private handleGameStateChanged(data: { state: string }): void {
    console.log('Game state changed:', data.state)
  }
  
  private handlePlayerJoined(data: { playerId: string, playerName: string }): void {
    console.log('Player joined:', data)
  }
  
  private handlePlayerLeft(data: { playerId: string }): void {
    console.log('Player left:', data)
  }
  
  // メッセージ送信メソッド
  emit(event: string, data?: any): void {
    if (this.socket && this.socket.connected) {
      this.socket.emit(event, data)
    } else {
      console.warn('Socket not connected, cannot emit event:', event)
    }
  }
  
  // イベントリスナー登録
  on(event: string, callback: (...args: any[]) => void): void {
    if (this.socket) {
      this.socket.on(event, callback)
    }
  }
  
  // イベントリスナー削除
  off(event: string, callback?: (...args: any[]) => void): void {
    if (this.socket) {
      this.socket.off(event, callback)
    }
  }
  
  // プレイヤーアクション
  joinGame(playerName: string): void {
    this.emit('join-game', { playerName })
  }
  
  leaveGame(): void {
    this.emit('leave-game')
  }
  
  placeTrain(position: { x: number, y: number, z: number }): void {
    this.emit('place-train', { position })
  }
  
  placeStation(position: { x: number, y: number, z: number }, name: string): void {
    this.emit('place-station', { position, name })
  }
  
  buildTrack(from: number, to: number): void {
    this.emit('build-track', { fromStationId: from, toStationId: to })
  }
  
  // 接続状態確認
  get isConnected(): boolean {
    return this.socket?.connected ?? false
  }
  
  get connectionId(): string | undefined {
    return this.socket?.id
  }
}