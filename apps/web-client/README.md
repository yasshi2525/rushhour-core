# Rush Hour Railway Simulation - Web Client

React + Three.js + TypeScript を使用した鉄道シミュレーションゲームのWebクライアントです。

## 技術スタック

### コアフレームワーク
- **React 18** - UIフレームワーク
- **TypeScript** - 型安全性
- **Vite** - 高速ビルドツール

### 3D描画・レンダリング
- **Three.js** - 3D描画エンジン
- **@react-three/fiber** - ThreeのReactバインディング
- **@react-three/drei** - 追加コンポーネント群

### 状態管理・アーキテクチャ
- **Zustand** - 軽量状態管理
- **bitECS** - Entity Component System
- **Immer** - 不変オブジェクト管理

### リアルタイム通信
- **socket.io-client** - WebSocket通信
- **@tanstack/react-query** - サーバー状態管理
- **axios** - HTTP通信

### パフォーマンス最適化
- **独自SpatialHashGrid** - 空間分割システム
- **rbush** - 空間インデックス（R-tree）

## セットアップ

### 依存関係のインストール

```bash
cd apps/web-client
npm install
```

### 環境変数の設定

```bash
cp .env.example .env
```

`.env`ファイルを編集してサーバーURLを設定：

```env
VITE_SERVER_URL=http://localhost:8080
VITE_API_URL=http://localhost:8080/api
```

### 開発サーバーの起動

```bash
npm run dev
```

ブラウザで http://localhost:3000 を開きます。

## スクリプト

- `npm run dev` - 開発サーバー起動
- `npm run build` - プロダクションビルド
- `npm run lint` - ESLintによるコードチェック
- `npm run test` - テスト実行
- `npm run test:coverage` - カバレッジ付きテスト
- `npm run preview` - ビルド済みアプリのプレビュー

## アーキテクチャ

### ディレクトリ構造

```
src/
├── components/          # UIコンポーネント
│   ├── systems/        # ECSシステムコンポーネント
│   ├── GameCanvas.tsx  # メインの3Dキャンバス
│   ├── Scene.tsx       # 3Dシーン
│   └── UI.tsx          # ゲームUI
├── engine/             # ゲームエンジン
│   ├── ECS.tsx         # Entity Component System
│   └── SpatialSystem.ts # 空間分割システム
├── services/           # 外部サービス
│   ├── SocketService.ts # WebSocket通信
│   └── APIService.ts   # REST API通信
├── store/              # 状態管理
│   ├── gameStore.ts    # ゲーム状態
│   └── GameContext.tsx # React Context
├── types/              # 型定義
└── test/               # テスト設定
```

### ECS（Entity Component System）

ゲームオブジェクトの管理にECSアーキテクチャを採用：

- **Entity**: ゲームオブジェクトの識別子
- **Component**: データの定義（Position, Velocity, Train等）
- **System**: ロジックの実装（MovementSystem, TrainSystem等）

### 空間分割システム

大量のオブジェクトを効率的に管理するため2つの空間分割手法を併用：

- **独自SpatialHashGrid**: 高速な粗い検索（自前実装）
- **RBush (R-tree)**: 精密な範囲検索

### リアルタイム通信

- **WebSocket**: リアルタイムゲーム状態の同期
- **REST API**: ゲームデータの CRUD 操作

## 開発ガイド

### 新しいゲームオブジェクトの追加

1. `src/engine/ECS.tsx`でComponentを定義
2. `src/components/systems/`にSystemコンポーネントを作成
3. `src/components/Scene.tsx`にSystemを追加

### パフォーマンス最適化

- LOD（Level of Detail）システムによる描画最適化
- 距離ベースのオブジェクト表示制御
- バッチ処理による描画呼び出し削減

### テスト

```bash
# 単体テスト実行
npm run test

# カバレッジ付きテスト
npm run test:coverage
```

## デプロイ

### プロダクションビルド

```bash
npm run build
```

ビルド成果物は`dist/`ディレクトリに出力されます。

### Docker（将来対応予定）

```bash
docker build -t rushhour-web-client .
docker run -p 3000:3000 rushhour-web-client
```

## トラブルシューティング

### よくある問題

1. **3D描画が表示されない**
   - WebGLに対応したブラウザを使用しているか確認
   - グラフィックドライバーを最新に更新

2. **接続エラー**
   - ゲームサーバーが起動しているか確認
   - `.env`ファイルのサーバーURLが正しいか確認

3. **パフォーマンスが悪い**
   - ブラウザの開発者ツールでパフォーマンスを確認
   - デバイスのスペックに応じて設定を調整