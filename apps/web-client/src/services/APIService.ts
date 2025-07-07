import axios, { AxiosInstance, AxiosResponse } from 'axios'

export interface GameStats {
  totalPlayers: number
  totalTrains: number
  totalStations: number
  totalRevenue: number
}

export interface TrainData {
  id: number
  position: { x: number, y: number, z: number }
  speed: number
  capacity: number
  passengers: number
  route?: number[]
}

export interface StationData {
  id: number
  name: string
  position: { x: number, y: number, z: number }
  capacity: number
  waitingPassengers: number
  totalPassengers: number
}

export interface TrackData {
  id: number
  fromStationId: number
  toStationId: number
  length: number
  isActive: boolean
}

export class APIService {
  private client: AxiosInstance
  
  constructor() {
    const baseURL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
    
    this.client = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    })
    
    this.setupInterceptors()
  }
  
  private setupInterceptors(): void {
    // リクエストインターセプター
    this.client.interceptors.request.use(
      (config) => {
        // 認証トークンがあれば追加
        const token = localStorage.getItem('authToken')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        
        console.log(`API Request: ${config.method?.toUpperCase()} ${config.url}`)
        return config
      },
      (error) => {
        console.error('Request error:', error)
        return Promise.reject(error)
      }
    )
    
    // レスポンスインターセプター
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        console.log(`API Response: ${response.status} ${response.config.url}`)
        return response
      },
      (error) => {
        console.error('Response error:', error)
        
        if (error.response?.status === 401) {
          // 認証エラーの場合はトークンを削除
          localStorage.removeItem('authToken')
          window.location.href = '/login'
        }
        
        return Promise.reject(error)
      }
    )
  }
  
  // ゲーム統計情報の取得
  async getGameStats(): Promise<GameStats> {
    const response = await this.client.get<GameStats>('/stats')
    return response.data
  }
  
  // 電車情報の取得
  async getTrains(): Promise<TrainData[]> {
    const response = await this.client.get<TrainData[]>('/trains')
    return response.data
  }
  
  // 特定の電車情報の取得
  async getTrain(id: number): Promise<TrainData> {
    const response = await this.client.get<TrainData>(`/trains/${id}`)
    return response.data
  }
  
  // 電車の作成
  async createTrain(trainData: Omit<TrainData, 'id'>): Promise<TrainData> {
    const response = await this.client.post<TrainData>('/trains', trainData)
    return response.data
  }
  
  // 電車の更新
  async updateTrain(id: number, trainData: Partial<TrainData>): Promise<TrainData> {
    const response = await this.client.put<TrainData>(`/trains/${id}`, trainData)
    return response.data
  }
  
  // 電車の削除
  async deleteTrain(id: number): Promise<void> {
    await this.client.delete(`/trains/${id}`)
  }
  
  // 駅情報の取得
  async getStations(): Promise<StationData[]> {
    const response = await this.client.get<StationData[]>('/stations')
    return response.data
  }
  
  // 特定の駅情報の取得
  async getStation(id: number): Promise<StationData> {
    const response = await this.client.get<StationData>(`/stations/${id}`)
    return response.data
  }
  
  // 駅の作成
  async createStation(stationData: Omit<StationData, 'id'>): Promise<StationData> {
    const response = await this.client.post<StationData>('/stations', stationData)
    return response.data
  }
  
  // 駅の更新
  async updateStation(id: number, stationData: Partial<StationData>): Promise<StationData> {
    const response = await this.client.put<StationData>(`/stations/${id}`, stationData)
    return response.data
  }
  
  // 駅の削除
  async deleteStation(id: number): Promise<void> {
    await this.client.delete(`/stations/${id}`)
  }
  
  // 線路情報の取得
  async getTracks(): Promise<TrackData[]> {
    const response = await this.client.get<TrackData[]>('/tracks')
    return response.data
  }
  
  // 線路の作成
  async createTrack(trackData: Omit<TrackData, 'id'>): Promise<TrackData> {
    const response = await this.client.post<TrackData>('/tracks', trackData)
    return response.data
  }
  
  // 線路の削除
  async deleteTrack(id: number): Promise<void> {
    await this.client.delete(`/tracks/${id}`)
  }
  
  // 経路探索
  async findPath(fromStationId: number, toStationId: number): Promise<number[]> {
    const response = await this.client.post<{ path: number[] }>('/pathfinding', {
      from: fromStationId,
      to: toStationId,
    })
    return response.data.path
  }
  
  // ヘルスチェック
  async healthCheck(): Promise<{ status: string, timestamp: string }> {
    const response = await this.client.get('/health')
    return response.data
  }
}

// シングルトンインスタンス
export const apiService = new APIService()