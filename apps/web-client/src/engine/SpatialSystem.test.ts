import { describe, it, expect, beforeEach } from 'vitest'
import { SpatialHashGrid, SpatialRTree, SpatialManager, SpatialObject } from './SpatialSystem'

describe('SpatialHashGrid', () => {
  let grid: SpatialHashGrid
  let testObject: SpatialObject
  
  beforeEach(() => {
    grid = new SpatialHashGrid(10)
    testObject = {
      id: 1,
      x: 5,
      y: 1,
      z: 5,
      width: 2,
      height: 2,
      depth: 2,
      type: 'train',
    }
  })
  
  it('should insert and find objects', () => {
    grid.insert(testObject)
    
    const results = grid.queryPoint(5, 5)
    expect(results).toHaveLength(1)
    expect(results[0]?.id).toBe(1)
  })
  
  it('should find objects in range', () => {
    grid.insert(testObject)
    
    const results = grid.queryRange(0, 0, 20, 20)
    expect(results).toHaveLength(1)
    expect(results[0]?.id).toBe(1)
  })
  
  it('should remove objects', () => {
    grid.insert(testObject)
    grid.remove(testObject)
    
    const results = grid.queryPoint(5, 5)
    expect(results).toHaveLength(0)
  })
  
  it('should update object positions', () => {
    grid.insert(testObject)
    
    testObject.x = 15
    testObject.z = 15
    grid.update(testObject)
    
    const oldResults = grid.queryPoint(5, 5)
    const newResults = grid.queryPoint(15, 15)
    
    expect(oldResults).toHaveLength(0)
    expect(newResults).toHaveLength(1)
  })
})

describe('SpatialRTree', () => {
  let tree: SpatialRTree
  let testObject: SpatialObject
  
  beforeEach(() => {
    tree = new SpatialRTree()
    testObject = {
      id: 1,
      x: 5,
      y: 1,
      z: 5,
      width: 2,
      height: 2,
      depth: 2,
      type: 'station',
    }
  })
  
  it('should insert and search objects', () => {
    tree.insert(testObject)
    
    const results = tree.search(0, 0, 20, 20)
    expect(results).toHaveLength(1)
    expect(results[0]?.id).toBe(1)
  })
  
  it('should search by point', () => {
    tree.insert(testObject)
    
    const results = tree.searchPoint(5, 5)
    expect(results).toHaveLength(1)
  })
})

describe('SpatialManager', () => {
  let manager: SpatialManager
  let train: SpatialObject
  let station: SpatialObject
  
  beforeEach(() => {
    manager = new SpatialManager(10)
    train = {
      id: 1,
      x: 5,
      y: 1,
      z: 5,
      width: 4,
      height: 2,
      depth: 1.5,
      type: 'train',
    }
    station = {
      id: 2,
      x: 20,
      y: 0,
      z: 20,
      width: 8,
      height: 4,
      depth: 6,
      type: 'station',
    }
  })
  
  it('should add and get objects', () => {
    manager.addObject(train)
    
    const retrieved = manager.getObject(1)
    expect(retrieved).toBeDefined()
    expect(retrieved?.type).toBe('train')
  })
  
  it('should query by type', () => {
    manager.addObject(train)
    manager.addObject(station)
    
    const trains = manager.queryByType('train', 5, 5, 10)
    const stations = manager.queryByType('station', 20, 20, 10)
    
    expect(trains).toHaveLength(1)
    expect(stations).toHaveLength(1)
  })
  
  it('should find nearest objects', () => {
    manager.addObject(train)
    manager.addObject(station)
    
    const nearest = manager.findNearest(6, 6, 'train')
    expect(nearest).toBeDefined()
    expect(nearest?.id).toBe(1)
  })
  
  it('should check collisions', () => {
    const otherTrain: SpatialObject = {
      id: 3,
      x: 6,
      y: 1,
      z: 5,
      width: 4,
      height: 2,
      depth: 1.5,
      type: 'train',
    }
    
    manager.addObject(train)
    manager.addObject(otherTrain)
    
    const collisions = manager.checkCollisions(train)
    expect(collisions).toHaveLength(1)
    expect(collisions[0]?.id).toBe(3)
  })
  
  it('should provide statistics', () => {
    manager.addObject(train)
    manager.addObject(station)
    
    const stats = manager.getStats()
    expect(stats.totalObjects).toBe(2)
    expect(stats.objectsByType.train).toBe(1)
    expect(stats.objectsByType.station).toBe(1)
  })
})