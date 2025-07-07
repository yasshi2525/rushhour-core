import { useRef, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import { Box, Text } from '@react-three/drei'
import { useECS, Position, Station } from '../../engine/ECS'
import { defineQuery } from 'bitecs'
import * as THREE from 'three'

export function StationSystem() {
  const { world, createEntity, addComponentToEntity } = useECS()
  const stationGroupRef = useRef<THREE.Group>(null)
  
  // 駅エンティティのクエリ
  const stationQuery = world ? defineQuery([Position, Station])(world) : []
  
  useEffect(() => {
    if (!world) return
    
    // 初期駅の作成
    const stations = [
      { name: 'Central Station', x: 0, z: -20 },
      { name: 'North Station', x: 0, z: 20 },
      { name: 'East Station', x: 20, z: 0 },
      { name: 'West Station', x: -20, z: 0 },
    ]
    
    stations.forEach((stationData, index) => {
      const stationEid = createEntity()
      addComponentToEntity(stationEid, Position, { 
        x: stationData.x, 
        y: 0, 
        z: stationData.z 
      })
      addComponentToEntity(stationEid, Station, {
        id: index + 1,
        name: index, // 名前のインデックス
        capacity: 200,
        waitingPassengers: Math.floor(Math.random() * 50),
      })
    })
  }, [world, createEntity, addComponentToEntity])
  
  return (
    <group ref={stationGroupRef}>
      {world && stationQuery.map((eid) => (
        <StationEntity 
          key={eid}
          stationId={Station.id[eid] ?? eid}
          position={[
            Position.x[eid] ?? 0,
            Position.y[eid] ?? 0,
            Position.z[eid] ?? 0
          ]}
          waitingPassengers={Station.waitingPassengers[eid] ?? 0}
        />
      ))}
    </group>
  )
}

interface StationEntityProps {
  readonly stationId: number
  readonly position: readonly [number, number, number]
  readonly waitingPassengers: number
}

function StationEntity({ stationId, position, waitingPassengers }: StationEntityProps) {
  const meshRef = useRef<THREE.Mesh>(null)
  
  useFrame(() => {
    if (meshRef.current) {
      meshRef.current.position.set(position[0], position[1], position[2])
    }
  })
  
  return (
    <group position={position}>
      {/* 駅建物 */}
      <Box
        ref={meshRef}
        args={[8, 4, 6]}
        position={[0, 2, 0]}
        castShadow
        receiveShadow
      >
        <meshStandardMaterial color="#4ECDC4" />
      </Box>
      
      {/* プラットフォーム */}
      <Box
        args={[12, 0.2, 4]}
        position={[0, 0.1, -6]}
        receiveShadow
      >
        <meshStandardMaterial color="#95A5A6" />
      </Box>
      
      {/* 駅名表示 */}
      <Text
        position={[0, 5, 0]}
        fontSize={1}
        color="white"
        anchorX="center"
        anchorY="middle"
      >
        Station {stationId}
      </Text>
      
      {/* 待機乗客数 */}
      <Text
        position={[0, 3.5, 0]}
        fontSize={0.5}
        color="yellow"
        anchorX="center"
        anchorY="middle"
      >
        Waiting: {waitingPassengers}
      </Text>
    </group>
  )
}