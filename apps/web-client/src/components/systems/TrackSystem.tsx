import { useRef, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import { Box } from '@react-three/drei'
import { useECS, Position, Track } from '../../engine/ECS'
import { defineQuery } from 'bitecs'
import * as THREE from 'three'

export function TrackSystem() {
  const { world, createEntity, addComponentToEntity } = useECS()
  const trackGroupRef = useRef<THREE.Group>(null)
  
  // 線路エンティティのクエリ
  const trackQuery = world ? defineQuery([Position, Track])(world) : []
  
  useEffect(() => {
    if (!world) return
    
    // 初期線路の作成（駅間を接続）
    const tracks = [
      // 中央駅 - 北駅
      { from: 1, to: 2, startX: 0, startZ: -20, endX: 0, endZ: 20 },
      // 中央駅 - 東駅
      { from: 1, to: 3, startX: 0, startZ: -20, endX: 20, endZ: 0 },
      // 中央駅 - 西駅
      { from: 1, to: 4, startX: 0, startZ: -20, endX: -20, endZ: 0 },
    ]
    
    tracks.forEach((trackData, index) => {
      const trackEid = createEntity()
      
      // 線路の中央位置を計算
      const centerX = (trackData.startX + trackData.endX) / 2
      const centerZ = (trackData.startZ + trackData.endZ) / 2
      const length = Math.sqrt(
        Math.pow(trackData.endX - trackData.startX, 2) + 
        Math.pow(trackData.endZ - trackData.startZ, 2)
      )
      
      addComponentToEntity(trackEid, Position, { 
        x: centerX, 
        y: 0, 
        z: centerZ 
      })
      addComponentToEntity(trackEid, Track, {
        id: index + 1,
        fromStationId: trackData.from,
        toStationId: trackData.to,
        length: length,
      })
    })
  }, [world, createEntity, addComponentToEntity])
  
  return (
    <group ref={trackGroupRef}>
      {world && trackQuery.map((eid) => (
        <TrackEntity 
          key={eid}
          entityId={eid}
          position={[
            Position.x[eid],
            Position.y[eid],
            Position.z[eid]
          ]}
          length={Track.length[eid]}
          fromStationId={Track.fromStationId[eid]}
          toStationId={Track.toStationId[eid]}
        />
      ))}
    </group>
  )
}

interface TrackEntityProps {
  entityId: number
  position: [number, number, number]
  length: number
  fromStationId: number
  toStationId: number
}

function TrackEntity({ entityId, position, length, fromStationId, toStationId }: TrackEntityProps) {
  const meshRef = useRef<THREE.Mesh>(null)
  
  // 駅の位置から角度を計算（簡略化）
  const getRotationFromStations = () => {
    const stationPositions: Record<number, [number, number]> = {
      1: [0, -20], // Central
      2: [0, 20],  // North
      3: [20, 0],  // East
      4: [-20, 0], // West
    }
    
    const from = stationPositions[fromStationId]
    const to = stationPositions[toStationId]
    
    if (!from || !to) return 0
    
    return Math.atan2(to[1] - from[1], to[0] - from[0])
  }
  
  const rotation = getRotationFromStations()
  
  useFrame(() => {
    if (meshRef.current) {
      meshRef.current.position.set(position[0], position[1], position[2])
      meshRef.current.rotation.y = rotation
    }
  })
  
  return (
    <group position={position} rotation={[0, rotation, 0]}>
      {/* レール（左） */}
      <Box
        args={[length, 0.1, 0.1]}
        position={[0, 0.05, -0.3]}
        receiveShadow
      >
        <meshStandardMaterial color="#8B4513" />
      </Box>
      
      {/* レール（右） */}
      <Box
        args={[length, 0.1, 0.1]}
        position={[0, 0.05, 0.3]}
        receiveShadow
      >
        <meshStandardMaterial color="#8B4513" />
      </Box>
      
      {/* 枕木 */}
      {Array.from({ length: Math.floor(length / 2) }, (_, i) => (
        <Box
          key={i}
          args={[1.5, 0.2, 0.8]}
          position={[-length/2 + i * 2, 0, 0]}
          receiveShadow
        >
          <meshStandardMaterial color="#654321" />
        </Box>
      ))}
    </group>
  )
}