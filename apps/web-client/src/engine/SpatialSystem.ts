import RBush from 'rbush'

export interface SpatialObject {
  id: number
  x: number
  y: number
  z: number
  width: number
  height: number
  depth: number
  type: 'train' | 'station' | 'track' | 'resident'
  data?: any
}

interface RBushItem {
  minX: number
  minY: number
  maxX: number
  maxY: number
  object: SpatialObject
}

export class SpatialHashGrid {
  private cellSize: number
  private grid: Map<string, Set<SpatialObject>>
  
  constructor(cellSize: number = 10) {
    this.cellSize = cellSize
    this.grid = new Map()
  }
  
  private getGridKey(x: number, z: number): string {
    const gridX = Math.floor(x / this.cellSize)
    const gridZ = Math.floor(z / this.cellSize)
    return `${gridX},${gridZ}`
  }
  
  private getAffectedCells(obj: SpatialObject): string[] {
    const keys: string[] = []
    const minX = obj.x - obj.width / 2
    const maxX = obj.x + obj.width / 2
    const minZ = obj.z - obj.depth / 2
    const maxZ = obj.z + obj.depth / 2
    
    const startGridX = Math.floor(minX / this.cellSize)
    const endGridX = Math.floor(maxX / this.cellSize)
    const startGridZ = Math.floor(minZ / this.cellSize)
    const endGridZ = Math.floor(maxZ / this.cellSize)
    
    for (let gx = startGridX; gx <= endGridX; gx++) {
      for (let gz = startGridZ; gz <= endGridZ; gz++) {
        keys.push(`${gx},${gz}`)
      }
    }
    
    return keys
  }
  
  insert(obj: SpatialObject): void {
    const cells = this.getAffectedCells(obj)
    
    for (const key of cells) {
      if (!this.grid.has(key)) {
        this.grid.set(key, new Set())
      }
      this.grid.get(key)!.add(obj)
    }
  }
  
  remove(obj: SpatialObject): void {
    const cells = this.getAffectedCells(obj)
    
    for (const key of cells) {
      const cell = this.grid.get(key)
      if (cell) {
        cell.delete(obj)
        if (cell.size === 0) {
          this.grid.delete(key)
        }
      }
    }
  }
  
  update(obj: SpatialObject): void {
    this.remove(obj)
    this.insert(obj)
  }
  
  queryRange(x: number, z: number, width: number, depth: number): SpatialObject[] {
    const results = new Set<SpatialObject>()
    const minX = x - width / 2
    const maxX = x + width / 2
    const minZ = z - depth / 2
    const maxZ = z + depth / 2
    
    const startGridX = Math.floor(minX / this.cellSize)
    const endGridX = Math.floor(maxX / this.cellSize)
    const startGridZ = Math.floor(minZ / this.cellSize)
    const endGridZ = Math.floor(maxZ / this.cellSize)
    
    for (let gx = startGridX; gx <= endGridX; gx++) {
      for (let gz = startGridZ; gz <= endGridZ; gz++) {
        const key = `${gx},${gz}`
        const cell = this.grid.get(key)
        if (cell) {
          for (const obj of cell) {
            // 実際の範囲内にあるかチェック
            const objMinX = obj.x - obj.width / 2
            const objMaxX = obj.x + obj.width / 2
            const objMinZ = obj.z - obj.depth / 2
            const objMaxZ = obj.z + obj.depth / 2
            
            if (objMaxX >= minX && objMinX <= maxX && 
                objMaxZ >= minZ && objMinZ <= maxZ) {
              results.add(obj)
            }
          }
        }
      }
    }
    
    return Array.from(results)
  }
  
  queryPoint(x: number, z: number): SpatialObject[] {
    const key = this.getGridKey(x, z)
    const cell = this.grid.get(key)
    
    if (!cell) return []
    
    return Array.from(cell).filter(obj => {
      const minX = obj.x - obj.width / 2
      const maxX = obj.x + obj.width / 2
      const minZ = obj.z - obj.depth / 2
      const maxZ = obj.z + obj.depth / 2
      
      return x >= minX && x <= maxX && z >= minZ && z <= maxZ
    })
  }
  
  clear(): void {
    this.grid.clear()
  }
  
  getAllObjects(): SpatialObject[] {
    const results = new Set<SpatialObject>()
    for (const cell of this.grid.values()) {
      for (const obj of cell) {
        results.add(obj)
      }
    }
    return Array.from(results)
  }
}

export class SpatialRTree {
  private tree: RBush<RBushItem>
  
  constructor() {
    this.tree = new RBush()
  }
  
  private toRBushItem(obj: SpatialObject): RBushItem {
    return {
      minX: obj.x - obj.width / 2,
      minY: obj.z - obj.depth / 2,
      maxX: obj.x + obj.width / 2,
      maxY: obj.z + obj.depth / 2,
      object: obj,
    }
  }
  
  insert(obj: SpatialObject): void {
    this.tree.insert(this.toRBushItem(obj))
  }
  
  remove(obj: SpatialObject): void {
    this.tree.remove(this.toRBushItem(obj))
  }
  
  update(oldObj: SpatialObject, newObj: SpatialObject): void {
    this.remove(oldObj)
    this.insert(newObj)
  }
  
  search(x: number, z: number, width: number, depth: number): SpatialObject[] {
    const bbox = {
      minX: x - width / 2,
      minY: z - depth / 2,
      maxX: x + width / 2,
      maxY: z + depth / 2,
    }
    
    return this.tree.search(bbox).map(item => item.object)
  }
  
  searchPoint(x: number, z: number): SpatialObject[] {
    return this.search(x, z, 0.1, 0.1)
  }
  
  clear(): void {
    this.tree.clear()
  }
  
  all(): SpatialObject[] {
    return this.tree.all().map(item => item.object)
  }
}

export class SpatialManager {
  private hashGrid: SpatialHashGrid
  private rTree: SpatialRTree
  private objects: Map<number, SpatialObject>
  
  constructor(cellSize: number = 10) {
    this.hashGrid = new SpatialHashGrid(cellSize)
    this.rTree = new SpatialRTree()
    this.objects = new Map()
  }
  
  addObject(obj: SpatialObject): void {
    if (this.objects.has(obj.id)) {
      this.removeObject(obj.id)
    }
    
    this.objects.set(obj.id, obj)
    this.hashGrid.insert(obj)
    this.rTree.insert(obj)
  }
  
  removeObject(id: number): void {
    const obj = this.objects.get(id)
    if (obj) {
      this.objects.delete(id)
      this.hashGrid.remove(obj)
      this.rTree.remove(obj)
    }
  }
  
  updateObject(id: number, newPosition: { x: number, y: number, z: number }): void {
    const obj = this.objects.get(id)
    if (obj) {
      const oldObj = { ...obj }
      obj.x = newPosition.x
      obj.y = newPosition.y
      obj.z = newPosition.z
      
      this.hashGrid.update(obj)
      this.rTree.update(oldObj, obj)
    }
  }
  
  getObject(id: number): SpatialObject | undefined {
    return this.objects.get(id)
  }
  
  // 範囲検索（高速な粗い検索）
  queryRangeCoarse(x: number, z: number, width: number, depth: number): SpatialObject[] {
    return this.hashGrid.queryRange(x, z, width, depth)
  }
  
  // 範囲検索（精密な検索）
  queryRangePrecise(x: number, z: number, width: number, depth: number): SpatialObject[] {
    return this.rTree.search(x, z, width, depth)
  }
  
  // 点検索
  queryPoint(x: number, z: number): SpatialObject[] {
    return this.hashGrid.queryPoint(x, z)
  }
  
  // 型別検索
  queryByType(type: SpatialObject['type'], x: number, z: number, radius: number): SpatialObject[] {
    return this.queryRangePrecise(x, z, radius * 2, radius * 2)
      .filter(obj => obj.type === type)
  }
  
  // 衝突検知
  checkCollisions(obj: SpatialObject): SpatialObject[] {
    return this.queryRangePrecise(obj.x, obj.z, obj.width, obj.depth)
      .filter(other => other.id !== obj.id)
  }
  
  // 最近傍検索
  findNearest(x: number, z: number, type?: SpatialObject['type'], maxDistance: number = 50): SpatialObject | null {
    const candidates = this.queryRangePrecise(x, z, maxDistance * 2, maxDistance * 2)
    
    let nearest: SpatialObject | null = null
    let minDistance = maxDistance
    
    for (const obj of candidates) {
      if (type && obj.type !== type) continue
      
      const distance = Math.sqrt(
        Math.pow(obj.x - x, 2) + Math.pow(obj.z - z, 2)
      )
      
      if (distance < minDistance) {
        minDistance = distance
        nearest = obj
      }
    }
    
    return nearest
  }
  
  // 統計情報
  getStats() {
    return {
      totalObjects: this.objects.size,
      objectsByType: Array.from(this.objects.values()).reduce((acc, obj) => {
        acc[obj.type] = (acc[obj.type] || 0) + 1
        return acc
      }, {} as Record<string, number>),
    }
  }
  
  clear(): void {
    this.objects.clear()
    this.hashGrid.clear()
    this.rTree.clear()
  }
}