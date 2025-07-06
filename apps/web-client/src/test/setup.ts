import '@testing-library/jest-dom'

// Three.jsのモック
global.ResizeObserver = class ResizeObserver {
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

global.HTMLCanvasElement.prototype.getContext = jest.fn(() => mockWebGLContext)

// WebSocket関連のモック
global.WebSocket = class WebSocket {
  onopen = null
  onclose = null
  onmessage = null
  onerror = null
  
  constructor(url: string) {
    setTimeout(() => {
      if (this.onopen) this.onopen({} as Event)
    }, 0)
  }
  
  send() {}
  close() {}
} as any