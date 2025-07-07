import '@testing-library/jest-dom'
import { vi } from 'vitest'

// グローバル型定義を削除し、直接実装

// Three.jsのモック
globalThis.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// WebGLコンテキストのモック
const mockWebGLContext = {
  canvas: {},
  drawingBufferWidth: 1024,
  drawingBufferHeight: 768,
  getExtension: () => null,
  getParameter: () => null,
  getShaderPrecisionFormat: () => ({ precision: 1, rangeMin: 1, rangeMax: 1 }),
}

globalThis.HTMLCanvasElement.prototype.getContext = vi.fn(() => mockWebGLContext) as any

// WebSocket関連のモック
;(globalThis as any).WebSocket = class WebSocket {
  onopen: ((event: Event) => void) | null = null
  onclose: ((event: CloseEvent) => void) | null = null
  onmessage: ((event: MessageEvent) => void) | null = null
  onerror: ((event: Event) => void) | null = null
  
  constructor(_url: string) {
    setTimeout(() => {
      const openHandler = this.onopen
      if (openHandler) {
        openHandler({} as Event)
      }
    }, 0)
  }
  
  send(_data: string | ArrayBuffer | Blob) {}
  close(_code?: number, _reason?: string) {}
}