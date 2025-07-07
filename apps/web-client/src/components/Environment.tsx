import { Plane } from '@react-three/drei'

export function Environment() {
  return (
    <>
      {/* 地面 */}
      <Plane
        args={[1000, 1000]}
        rotation={[-Math.PI / 2, 0, 0]}
        position={[0, -0.1, 0]}
        receiveShadow
      >
        <meshStandardMaterial color="#4a5d23" />
      </Plane>
      
      {/* グリッド */}
      <gridHelper
        args={[1000, 100, '#888888', '#444444']}
        position={[0, 0, 0]}
      />
      
      {/* スカイボックス */}
      <hemisphereLight
        args={['#87CEEB', '#98D982', 0.6]}
      />
    </>
  )
}