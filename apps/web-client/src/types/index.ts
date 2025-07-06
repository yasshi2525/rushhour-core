// ゲーム状態の型定義
export type GameState = 'menu' | 'loading' | 'playing' | 'paused' | 'ended'
export type ConnectionStatus = 'disconnected' | 'connecting' | 'connected' | 'error'

// 3D空間の位置
export interface Position3D {
  x: number
  y: number
  z: number
}

// 3D空間の回転
export interface Rotation3D {
  x: number
  y: number
  z: number
}

// 電車の型定義
export interface Train {
  id: number
  position: Position3D
  rotation: Rotation3D
  velocity: Position3D
  speed: number
  maxSpeed: number
  capacity: number
  passengers: number
  routeId?: number
  nextStationId?: number
  state: 'idle' | 'moving' | 'stopping' | 'boarding' | 'maintenance'
}

// 駅の型定義
export interface Station {
  id: number
  name: string
  position: Position3D
  capacity: number
  waitingPassengers: number
  totalPassengers: number
  platforms: Platform[]
  connections: number[] // 接続されている駅のID
}

// プラットフォームの型定義
export interface Platform {
  id: number
  stationId: number
  position: Position3D
  trackIds: number[]
  isOccupied: boolean
  trainId?: number
}

// 線路の型定義
export interface Track {
  id: number
  fromStationId: number
  toStationId: number
  length: number
  points: Position3D[]
  isActive: boolean
  maxSpeed: number
  signals: Signal[]
}

// 信号の型定義
export interface Signal {
  id: number
  trackId: number
  position: Position3D
  state: 'red' | 'yellow' | 'green'
  type: 'automatic' | 'manual'
}

// 住民の型定義
export interface Resident {
  id: number
  position: Position3D
  originStationId: number
  destinationStationId: number
  state: 'waiting' | 'boarding' | 'traveling' | 'arrived'
  trainId?: number
  patience: number
  maxPatience: number
  travelPlan: TravelPlan
}

// 旅行計画の型定義
export interface TravelPlan {
  departureTime: number
  arrivalTime: number
  route: number[]
  priority: 'low' | 'normal' | 'high'
  cost: number
}

// 路線の型定義
export interface Route {
  id: number
  name: string
  stationIds: number[]
  trackIds: number[]
  trains: number[]
  schedule: Schedule[]
  color: string
  isActive: boolean
}

// スケジュール/時刻表の型定義
export interface Schedule {
  id: number
  routeId: number
  trainId: number
  stationTimes: StationTime[]
  frequency: number // 分単位
  isActive: boolean
}

export interface StationTime {
  stationId: number
  arrivalTime: string // HH:mm format
  departureTime: string // HH:mm format
  platform?: number
}

// イベントの型定義
export interface GameEvent {
  id: string
  type: 'train-arrived' | 'train-departed' | 'station-full' | 'delay' | 'accident'
  timestamp: number
  data: any
  severity: 'info' | 'warning' | 'error'
}

// 統計の型定義
export interface GameStats {
  totalPlayers: number
  totalTrains: number
  totalStations: number
  totalTracks: number
  totalPassengers: number
  totalRevenue: number
  averageDelay: number
  satisfaction: number
}

// プレイヤー情報
export interface Player {
  id: string
  name: string
  color: string
  isOnline: boolean
  stats: PlayerStats
}

export interface PlayerStats {
  trainsOwned: number
  stationsOwned: number
  revenue: number
  passengersSatisfied: number
  efficiency: number
}

// UI関連の型
export interface ViewportBounds {
  minX: number
  maxX: number
  minZ: number
  maxZ: number
}

export interface CameraSettings {
  position: Position3D
  target: Position3D
  zoom: number
  minZoom: number
  maxZoom: number
}

// API レスポンスの型
export interface APIResponse<T> {
  success: boolean
  data: T
  message?: string
  error?: string
}

// WebSocket メッセージの型
export interface SocketMessage {
  type: string
  data: any
  timestamp: number
  senderId?: string
}