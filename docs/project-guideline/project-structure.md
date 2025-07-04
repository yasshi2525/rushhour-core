# ソースリポジトリ構成

## 全体構造

```
./
├── .github/                    # CI/CD設定
│   └── workflows/
│       ├── core-services.yml   # コアサービスのCI
│       ├── microservices.yml   # マイクロサービスのCI
│       └── integration.yml     # 結合テスト
├── apps/                       # アプリケーション層
│   ├── game-server/            # メインゲームサーバー（Spring Boot）
│   │   ├── src/
│   │   │   ├── main/java/net/rushhourgame/
│   │   │   │   ├── core/       # コアゲームロジック
│   │   │   │   ├── realtime/   # リアルタイム処理
│   │   │   │   ├── actors/     # アクターモデル実装
│   │   │   │   └── config/     # 設定クラス
│   │   │   └── resources/
│   │   ├── build.gradle
│   │   └── docker/
│   ├── web-client/             # Web クライアント（React/Three.js）
│   │   ├── src/
│   │   │   ├── components/     # UIコンポーネント
│   │   │   ├── engine/         # ゲームエンジン
│   │   │   ├── websocket/      # リアルタイム通信
│   │   │   └── utils/
│   │   ├── public/
│   │   └── package.json
│   ├── mobile-client/          # モバイルアプリ（React Native）
│   │   ├── src/
│   │   └── package.json
│   └── admin-dashboard/        # 管理画面（Vue.js）
│       ├── src/
│       └── package.json
├── services/                   # マイクロサービス群
│   ├── pathfinding-service/    # 経路探索サービス
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/pathfinding/
│   │   │       ├── algorithm/  # A*, ダイクストラ実装
│   │   │       ├── cache/      # 経路キャッシュ
│   │   │       └── grpc/       # gRPC実装
│   │   ├── build.gradle
│   │   └── docker/
│   ├── analytics-service/      # 統計・ランキングサービス
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/analytics/
│   │   │       ├── collector/  # データ収集
│   │   │       ├── aggregator/ # 集計処理
│   │   │       └── ranking/    # ランキング計算
│   │   └── build.gradle
│   ├── realtime-broadcast/     # リアルタイム配信サービス
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/broadcast/
│   │   │       ├── websocket/  # WebSocket実装
│   │   │       ├── kafka/      # Kafka連携
│   │   │       └── position/   # 位置情報配信
│   │   └── build.gradle
│   ├── timetable-service/      # ダイヤスケジューリングサービス
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/timetable/
│   │   │       ├── schedule/   # 時刻表管理
│   │   │       ├── delay/      # 遅延計算
│   │   │       └── optimization/ # ダイヤ最適化
│   │   └── build.gradle
│   └── map-renderer/           # マップレンダリングサービス
│       ├── src/main/java/
│       │   └── net/rushhourgame/renderer/
│       │       ├── tile/       # タイル生成
│       │       ├── cache/      # 画像キャッシュ
│       │       └── streaming/  # ストリーミング配信
│       └── build.gradle
├── packages/                   # 共有パッケージ
│   ├── shared-models/          # 共通データモデル
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/models/
│   │   │       ├── train/      # 電車関連モデル
│   │   │       ├── station/    # 駅関連モデル
│   │   │       ├── resident/   # 住民関連モデル
│   │   │       ├── route/      # 路線関連モデル
│   │   │       └── event/      # イベントモデル
│   │   └── build.gradle
│   ├── event-bus/              # 分散イベントバス
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/events/
│   │   │       ├── publisher/  # イベント発行
│   │   │       ├── subscriber/ # イベント購読
│   │   │       ├── kafka/      # Kafka実装
│   │   │       └── redis/      # Redis実装
│   │   └── build.gradle
│   ├── spatial-grid/           # 空間分割ライブラリ
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/spatial/
│   │   │       ├── grid/       # グリッドシステム
│   │   │       ├── collision/  # 衝突検知
│   │   │       └── partitioning/ # 空間分割
│   │   └── build.gradle
│   ├── actor-system/           # アクターシステム
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/actors/
│   │   │       ├── train/      # 電車アクター
│   │   │       ├── resident/   # 住民アクター
│   │   │       └── supervisor/ # スーパーバイザー
│   │   └── build.gradle
│   ├── game-physics/           # ゲーム物理演算
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/physics/
│   │   │       ├── movement/   # 移動計算
│   │   │       ├── signals/    # 信号制御
│   │   │       └── collision/  # 衝突物理
│   │   └── build.gradle
│   ├── distributed-cache/      # 分散キャッシュ
│   │   ├── src/main/java/
│   │   │   └── net/rushhourgame/cache/
│   │   │       ├── redis/      # Redis実装
│   │   │       ├── hazelcast/  # Hazelcast実装
│   │   │       └── strategy/   # キャッシュ戦略
│   │   └── build.gradle
│   └── web-components/         # Web共通コンポーネント
│       ├── src/
│       │   ├── train-viewer/   # 電車表示コンポーネント
│       │   ├── map-canvas/     # マップ描画
│       │   ├── station-panel/  # 駅情報パネル
│       │   └── realtime-chart/ # リアルタイムグラフ
│       └── package.json
├── infrastructure/             # インフラ設定
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   ├── kafka/
│   │   ├── redis/
│   │   ├── postgresql/
│   │   └── monitoring/
│   ├── kubernetes/
│   │   ├── game-server/
│   │   ├── microservices/
│   │   ├── ingress/
│   │   └── monitoring/
│   └── terraform/
│       └── aws/
├── tools/                      # 開発ツール
│   ├── load-testing/           # 負荷テストツール
│   │   ├── jmeter/
│   │   └── gatling/
│   ├── game-data-generator/    # ゲームデータ生成
│   │   ├── map-generator/
│   │   ├── train-data/
│   │   └── resident-data/
│   ├── monitoring/             # 監視ツール
│   │   ├── grafana/
│   │   ├── prometheus/
│   │   └── jaeger/
│   └── deployment/             # デプロイメントスクリプト
│       ├── scripts/
│       └── configs/
├── proto/                      # Protocol Buffers定義
│   ├── train.proto
│   ├── pathfinding.proto
│   ├── realtime.proto
│   └── analytics.proto
├── docs/                       # ドキュメント
│   ├── architecture/
│   │   ├── distributed-processing.md
│   │   ├── microservices.md
│   │   └── scaling-strategy.md
│   ├── api/
│   │   ├── game-server-api.md
│   │   └── microservice-apis.md
│   ├── game-design/
│   │   ├── simulation-logic.md
│   │   ├── physics-engine.md
│   │   └── ai-behavior.md
│   └── deployment/
│       ├── production-setup.md
│       └── monitoring-guide.md
├── build.gradle               # ルートビルド設定
├── settings.gradle            # Gradle設定
├── gradle.properties          # Gradleプロパティ
├── docker-compose.yml         # 開発環境用
├── docker-compose.prod.yml    # 本番環境用
└── README.md
```


## Gradle設定

### settings.gradle
```gradle
rootProject.name = 'rushhour-core'

// アプリケーション
include 'apps:game-server'
include 'apps:web-client'
include 'apps:mobile-client'
include 'apps:admin-dashboard'

// マイクロサービス
include 'services:pathfinding-service'
include 'services:analytics-service'
include 'services:realtime-broadcast'
include 'services:timetable-service'
include 'services:map-renderer'

// 共有パッケージ
include 'packages:shared-models'
include 'packages:event-bus'
include 'packages:spatial-grid'
include 'packages:actor-system'
include 'packages:game-physics'
include 'packages:distributed-cache'
include 'packages:web-components'

// ツール
include 'tools:load-testing'
include 'tools:game-data-generator'
```

### build.gradle（ルート）
```gradle
plugins {
    id 'org.springframework.boot' version '3.5.0' apply false
    id 'io.spring.dependency-management' version '1.1.7' apply false
    id 'com.google.protobuf' version '0.9.4' apply false
}

allprojects {
    group = 'net.roushhourgame'
    version = '1.0.0'
    
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'io.spring.dependency-management'
    
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    dependencyManagement {
        dependencies {
            // Spring Boot BOM
            imports {
                mavenBom 'org.springframework.boot:spring-boot-dependencies:3.5.0'
            }
            
            // 共通ライブラリバージョン管理
            dependency 'org.apache.kafka:kafka-clients:3.5.0'
            dependency 'io.grpc:grpc-netty-shaded:1.58.0'
            dependency 'io.grpc:grpc-protobuf:1.58.0'
            dependency 'io.grpc:grpc-stub:1.58.0'
            dependency 'redis.clients:jedis:5.0.0'
            dependency 'com.hazelcast:hazelcast:5.3.0'
            dependency 'io.jaegertracing:jaeger-client:1.8.1'
        }
    }
}

// タスク定義
task buildAllServices {
    group = 'build'
    description = 'Build all microservices'
    dependsOn ':services:pathfinding-service:build',
              ':services:analytics-service:build',
              ':services:realtime-broadcast:build',
              ':services:timetable-service:build',
              ':services:map-renderer:build'
}

task buildGameServer {
    group = 'build'
    description = 'Build game server'
    dependsOn ':apps:game-server:build'
}

task buildClients {
    group = 'build'
    description = 'Build all client applications'
    dependsOn ':apps:web-client:build',
              ':apps:mobile-client:build',
              ':apps:admin-dashboard:build'
}
```

## Docker Compose設定

### docker-compose.yml（開発環境）
```yaml
version: '3.8'

services:
  # インフラサービス
  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      
  kafka:
    image: confluentinc/cp-kafka:7.4.0
    depends_on: [zookeeper]
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_AUTO_CREATE_TOPICS_ENABLE: true
      
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    
  postgresql:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: rushhour
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
    volumes:
      - postgres_data:/var/lib/postgresql/data
      
  # ゲームサーバー
  game-server:
    build: 
      context: .
      dockerfile: apps/game-server/docker/Dockerfile
    ports:
      - "8080:8080"
    depends_on: [kafka, redis, postgresql]
    environment:
      SPRING_PROFILES_ACTIVE: development
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      REDIS_HOST: redis
      DATABASE_URL: jdbc:postgresql://postgresql:5432/rushhour
    volumes:
      - ./logs:/app/logs
      
  # マイクロサービス
  pathfinding-service:
    build:
      context: .
      dockerfile: services/pathfinding-service/docker/Dockerfile
    ports:
      - "9090:9090"
    depends_on: [redis]
    environment:
      GRPC_PORT: 9090
      REDIS_HOST: redis
      CACHE_SIZE: 10000
      
  analytics-service:
    build:
      context: .
      dockerfile: services/analytics-service/docker/Dockerfile
    ports:
      - "8081:8080"
    depends_on: [kafka, postgresql]
    environment:
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      DATABASE_URL: jdbc:postgresql://postgresql:5432/rushhour
      
  realtime-broadcast:
    build:
      context: .
      dockerfile: services/realtime-broadcast/docker/Dockerfile
    ports:
      - "8082:8080"
      - "8090:8090"
    depends_on: [kafka]
    environment:
      KAFKA_BOOTSTRAP_SERVERS: kafka:9092
      WEBSOCKET_PORT: 8090
      
  # クライアントアプリケーション
  web-client:
    build:
      context: .
      dockerfile: apps/web-client/docker/Dockerfile
    ports:
      - "3000:3000"
    environment:
      REACT_APP_GAME_SERVER_URL: http://localhost:8080
      REACT_APP_WEBSOCKET_URL: ws://localhost:8090
    volumes:
      - ./apps/web-client/src:/app/src
      
  # 監視・ツール
  jaeger:
    image: jaegertracing/all-in-one:1.48
    ports:
      - "16686:16686"
      - "14268:14268"
    environment:
      COLLECTOR_OTLP_ENABLED: true
      
  prometheus:
    image: prom/prometheus:v2.45.0
    ports:
      - "9091:9090"
    volumes:
      - ./tools/monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
      
  grafana:
    image: grafana/grafana:10.0.0
    ports:
      - "3001:3000"
    environment:
      GF_SECURITY_ADMIN_PASSWORD: admin
    volumes:
      - grafana_data:/var/lib/grafana

volumes:
  postgres_data:
  grafana_data:
```

## 段階的実装戦略

### Phase 1: モノリス + マルチスレッド（1-2ヶ月）
```gradle
// 初期実装対象
':apps:game-server'           // メインゲームロジック
':packages:shared-models'     // 共通モデル
':packages:actor-system'      // アクターシステム
':packages:spatial-grid'      // 空間分割
':apps:web-client'           // Webクライアント
```

### Phase 2: 読み取り専用サービス分離（2-3ヶ月）
```gradle
// 追加実装
':services:pathfinding-service'  // 経路探索
':services:analytics-service'    // 統計・ランキング
':services:map-renderer'        // マップレンダリング
':packages:distributed-cache'   // 分散キャッシュ
```

### Phase 3: リアルタイムサービス分離（3-4ヶ月）
```gradle
// 最終実装
':services:realtime-broadcast'  // リアルタイム配信
':services:timetable-service'   // ダイヤ管理
':packages:event-bus'          // イベントバス
':apps:mobile-client'          // モバイルアプリ
```

## 開発・デプロイコマンド

### 開発環境起動
```bash
# 全体環境起動
docker-compose up -d

# 特定サービスのみ
docker-compose up -d kafka redis postgresql
./gradlew :apps:game-server:bootRun

# フロントエンド開発
cd apps/web-client && npm run dev
```

### ビルド・テスト
```bash
# 全体ビルド
./gradlew build

# サービス別ビルド
./gradlew buildGameServer
./gradlew buildAllServices
./gradlew buildClients

# テスト実行
./gradlew test
./gradlew :services:pathfinding-service:test
```

### 本番デプロイ
```bash
# Dockerイメージビルド
./gradlew buildImage

# Kubernetes デプロイ
kubectl apply -f infrastructure/kubernetes/

# Terraform インフラ構築
cd infrastructure/terraform/aws
terraform init && terraform apply
```

## 主要な特徴

1. **段階的移行対応**: モノリスからマイクロサービスへの段階的移行
2. **共有パッケージ**: 分散処理に必要な共通ライブラリ群
3. **完全な開発環境**: Docker Composeによる一括環境構築
4. **監視・デバッグ**: Jaeger、Prometheus、Grafanaの統合
5. **負荷テスト**: JMeterとGatlingによる性能テスト
6. **インフラコード**: Kubernetes、Terraformによるインフラ管理

この構成により、文書で分析された分散処理戦略を実装しながら、保守性とスケーラビリティを確保できます。
