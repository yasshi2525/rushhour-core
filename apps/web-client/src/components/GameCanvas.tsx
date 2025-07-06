import { Canvas } from '@react-three/fiber'
import { OrbitControls, Stats } from '@react-three/drei'
import { Suspense } from 'react'
import { Scene } from './Scene'

export function GameCanvas() {
  return (
    <div className="game-canvas">
      <Canvas
        camera={{
          position: [0, 50, 50],
          fov: 75,
        }}
        shadows
        dpr={[1, 2]} // pixel ratio
        gl={{ antialias: true }}
      >
        <Suspense fallback={null}>
          <Scene />
        </Suspense>
        
        {/* 開発用のコントロール */}
        <OrbitControls
          enablePan={true}
          enableZoom={true}
          enableRotate={true}
          maxPolarAngle={Math.PI / 2}
          minDistance={5}
          maxDistance={200}
        />
        
        {/* 統計情報（開発時のみ） */}
        {import.meta.env.DEV && <Stats />}
      </Canvas>
    </div>
  )
}