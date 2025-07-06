import { useRef, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import { useGameStore } from '../store/gameStore'
import { TrainSystem } from './systems/TrainSystem'
import { StationSystem } from './systems/StationSystem'
import { TrackSystem } from './systems/TrackSystem'
import { Environment } from './Environment'
import { useECS } from '../engine/ECS'
import * as THREE from 'three'

export function Scene() {
  const groupRef = useRef<THREE.Group>(null)
  const { world, systems } = useECS()
  const gameState = useGameStore(state => state.gameState)
  
  // ECSシステムの更新
  useFrame((state, delta) => {
    if (world && systems.length > 0) {
      systems.forEach(system => {
        if (system.update) {
          system.update(world, delta)
        }
      })
    }
  })
  
  useEffect(() => {
    if (gameState === 'playing') {
      // ゲーム開始時の初期化
      console.log('Game started')
    }
  }, [gameState])
  
  return (
    <group ref={groupRef}>
      {/* 環境設定 */}
      <Environment />
      
      {/* ライティング */}
      <ambientLight intensity={0.3} />
      <directionalLight
        position={[10, 10, 5]}
        intensity={1}
        castShadow
        shadow-mapSize={[1024, 1024]}
        shadow-camera-far={50}
        shadow-camera-left={-10}
        shadow-camera-right={10}
        shadow-camera-top={10}
        shadow-camera-bottom={-10}
      />
      
      {/* ゲームオブジェクト */}
      <TrainSystem />
      <StationSystem />
      <TrackSystem />
    </group>
  )
}