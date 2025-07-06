import { useRef, useEffect } from 'react'
import { useFrame } from '@react-three/fiber'
import { Box } from '@react-three/drei'
import { useECS, Position, Velocity, Train } from '../../engine/ECS'
import { defineQuery } from 'bitecs'
import * as THREE from 'three'

export function TrainSystem() {
  const { world, createEntity, addComponentToEntity } = useECS()
  const trainGroupRef = useRef<THREE.Group>(null)
  
  // 電車エンティティのクエリ
  const trainQuery = world ? defineQuery([Position, Train])(world) : []
  
  useEffect(() => {
    if (!world) return
    
    // 初期電車の作成
    const trainEid = createEntity()
    addComponentToEntity(trainEid, Position, { x: 0, y: 1, z: 0 })
    addComponentToEntity(trainEid, Velocity, { x: 0, y: 0, z: 0 })
    addComponentToEntity(trainEid, Train, { 
      id: 1, 
      speed: 5.0, 
      capacity: 100, 
      passengers: 0 
    })
  }, [world, createEntity, addComponentToEntity])
  
  return (
    <group ref={trainGroupRef}>
      {world && trainQuery.map((eid) => (
        <TrainEntity 
          key={eid}
          entityId={eid}
          position={[
            Position.x[eid],
            Position.y[eid],
            Position.z[eid]
          ]}
        />
      ))}
    </group>
  )
}

interface TrainEntityProps {
  entityId: number
  position: [number, number, number]
}

function TrainEntity({ entityId, position }: TrainEntityProps) {
  const meshRef = useRef<THREE.Mesh>(null)
  
  useFrame(() => {
    if (meshRef.current) {
      meshRef.current.position.set(position[0], position[1], position[2])
    }
  })
  
  return (
    <Box
      ref={meshRef}
      args={[4, 2, 1.5]}
      position={position}
      castShadow
      receiveShadow
    >
      <meshStandardMaterial color="#FF6B6B" />
    </Box>
  )
}