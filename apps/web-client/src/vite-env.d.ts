/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_API_BASE_URL: string
  readonly VITE_WEBSOCKET_URL: string
  // 他の環境変数をここに追加
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}