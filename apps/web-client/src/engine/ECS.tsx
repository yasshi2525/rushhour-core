import React, { createContext, useContext, useEffect, useRef, ReactNode } from 'react'
import { 
  createWorld, 
  defineComponent, 
  addEntity, 
  addComponent,
  IWorld,
  Types,
  defineQuery
} from 'bitecs'

// コンポーネント定義
export const Position = defineComponent({
  x: Types.f32,
  y: Types.f32,
  z: Types.f32,
})

export const Velocity = defineComponent({
  x: Types.f32,
  y: Types.f32,
  z: Types.f32,
})

export const Train = defineComponent({
  id: Types.ui32,
  speed: Types.f32,
  capacity: Types.ui32,
  passengers: Types.ui32,
})

export const Station = defineComponent({
  id: Types.ui32,
  name: Types.ui32, // string indexとして使用
  capacity: Types.ui32,
  waitingPassengers: Types.ui32,
})

export const Track = defineComponent({
  id: Types.ui32,
  fromStationId: Types.ui32,
  toStationId: Types.ui32,
  length: Types.f32,
})

export const Resident = defineComponent({
  id: Types.ui32,
  originStationId: Types.ui32,
  destinationStationId: Types.ui32,
  state: Types.ui8, // 0: waiting, 1: boarding, 2: traveling, 3: arrived
})

// システム定義
export interface System {
  name: string
  update?: (world: IWorld, deltaTime: number) => void
  init?: (world: IWorld) => void
  cleanup?: (world: IWorld) => void
}

// 移動システム
export const MovementSystem: System = {
  name: 'MovementSystem',
  update: (world: IWorld, deltaTime: number) => {
    const entities = defineQuery([Position, Velocity])(world)
    
    for (let i = 0; i < entities.length; i++) {
      const eid = entities[i]
      if (eid === undefined) continue
      
      try {
        Position.x[eid] = (Position.x[eid] ?? 0) + (Velocity.x[eid] ?? 0) * deltaTime
        Position.y[eid] = (Position.y[eid] ?? 0) + (Velocity.y[eid] ?? 0) * deltaTime
        Position.z[eid] = (Position.z[eid] ?? 0) + (Velocity.z[eid] ?? 0) * deltaTime
      } catch (e) {
        // bitECS配列が未初期化の場合をハンドル
      }
    }
  }
}

// 電車システム
export const TrainMovementSystem: System = {
  name: 'TrainMovementSystem',
  update: (world: IWorld, deltaTime: number) => {
    const trainEntities = defineQuery([Position, Velocity, Train])(world)
    
    for (let i = 0; i < trainEntities.length; i++) {
      const eid = trainEntities[i]
      if (eid === undefined) continue
      
      // 電車固有の移動ロジック
      const currentSpeed = Train.speed[eid] ?? 0
      
      // 線路に沿った移動（簡略化）
      Velocity.x[eid] = currentSpeed * Math.cos(0) // 角度は後で実装
      Velocity.z[eid] = currentSpeed * Math.sin(0)
      Velocity.y[eid] = 0
    }
  }
}

// 住民シミュレーションシステム
export const ResidentSystem: System = {
  name: 'ResidentSystem',
  update: (world: IWorld, deltaTime: number) => {
    const residentEntities = defineQuery([Resident, Position])(world)
    
    for (let i = 0; i < residentEntities.length; i++) {
      const eid = residentEntities[i]
      if (eid === undefined) continue
      
      // 住民の状態に応じた行動
      const state = Resident.state[eid]
      
      switch (state) {
        case 0: // waiting
          // 駅で待機中
          break
        case 1: // boarding
          // 電車に乗車中
          break
        case 2: // traveling
          // 移動中
          break
        case 3: // arrived
          // 到着済み
          break
      }
    }
  }
}

// ECSコンテキスト
interface ECSContextValue {
  world: IWorld | null
  systems: System[]
  addSystem: (system: System) => void
  createEntity: () => number
  addComponentToEntity: (eid: number, component: any, data: any) => void
}

const ECSContext = createContext<ECSContextValue | null>(null)

interface ECSProviderProps {
  children: ReactNode
}

export function ECSProvider({ children }: ECSProviderProps) {
  const worldRef = useRef<IWorld | null>(null)
  const systemsRef = useRef<System[]>([])
  
  useEffect(() => {
    // ECSワールドの初期化
    const world = createWorld()
    worldRef.current = world
    
    // デフォルトシステムの追加
    const defaultSystems = [
      MovementSystem,
      TrainMovementSystem,
      ResidentSystem,
    ]
    
    systemsRef.current = defaultSystems
    
    // システムの初期化
    defaultSystems.forEach(system => {
      if (system.init) {
        system.init(world)
      }
    })
    
    return () => {
      // クリーンアップ
      systemsRef.current.forEach(system => {
        if (system.cleanup) {
          system.cleanup(world)
        }
      })
    }
  }, [])
  
  const addSystem = (system: System) => {
    if (worldRef.current && system.init) {
      system.init(worldRef.current)
    }
    systemsRef.current.push(system)
  }
  
  const createEntity = () => {
    if (!worldRef.current) throw new Error('World not initialized')
    return addEntity(worldRef.current)
  }
  
  const addComponentToEntity = (eid: number, component: any, data: any) => {
    if (!worldRef.current) throw new Error('World not initialized')
    addComponent(worldRef.current, component, eid)
    
    // データの設定
    Object.keys(data).forEach(key => {
      if (component[key] !== undefined) {
        component[key][eid] = data[key]
      }
    })
  }
  
  const contextValue: ECSContextValue = {
    world: worldRef.current,
    systems: systemsRef.current,
    addSystem,
    createEntity,
    addComponentToEntity,
  }
  
  return (
    <ECSContext.Provider value={contextValue}>
      {children}
    </ECSContext.Provider>
  )
}

export function useECS() {
  const context = useContext(ECSContext)
  if (!context) {
    throw new Error('useECS must be used within an ECSProvider')
  }
  return context
}