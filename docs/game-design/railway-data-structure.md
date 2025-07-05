# 鉄道データモデル

ゲームの要件に必要な鉄道の構造を定義する。

## ゲーム中の鉄道に求められる要件

### 全体

* 鉄道は線路、駅、電車から構成される。
* 鉄道の建設・撤去はプレイヤーが行う。
* 他人が建設した鉄道は、閲覧できるが操作できない。
* 鉄道の建設・撤去にはコスト（お金）がかかる。
* 建設した鉄道は他プレイヤーと売買できる。
* プレイヤーがスムーズにゲームに入れるよう、あらかじめ構築された、誰にも所属しない鉄道も存在する。

列車・電車の用語については電車に統一する。より広範な意味の列車は現時点では使わない。
旅客輸送をスコープとし、貨物輸送はスコープ外とする。

### 線路

* 線路は曲線から構成される。
* 線路は分岐・交差・合流させることができる。
* 線路は任意のタイミングで設置・撤去できる。
* リアリティとゲーム性を損なわせないため、極端に近い場所に線路を並行させることはできない。
* 電車の衝突回避実現のため、信号機を配置することができる。
* 他人が設置した線路に接続させることができる。ただしこれは将来を見据えた仕様で、現時点では禁止する。

### 駅

* 駅は線路に隣接して存在する
* 駅は利用客が出入りする改札と電車に出入りできるプラットフォーム（ホーム）とこれらを結ぶ通路から構成される。
* 改札を通るのに一定の時間を要する。
* 改札が一杯の場合、利用客は列を成して待つ。
* ホームには収容可能な定員が存在する。
* 通路とホームは人数が多いほど移動速度が低下する。
* 駅は複数の改札、ホームを持てるが、ゲーム性を損なわせないよう、プレイヤーは極端に離れた場所に設置できない。
* 他人の線路との境界点に共同所有の駅を作ることができる。ただしこれは将来を見据えた仕様で、現時点では禁止する。

### 電車

* 電車は線路の上を走り、駅に停車・通過する
* プレイヤーは電車を任意の線路上に配置したり、撤去できる。
* ある線路の上は最大でも1台の電車しか存在できない。
* 電車は物理的大きさを持つ。編成長の分、線路を占有する。
* 電車は決まった経路に従って線路上を走る。
* 電車の経路はプレイヤーが定義できる。
* 電車は利用客を乗せて移動する。
* 電車は利用客の乗降がすべて終わるまで駅を発車できない。
* 利用客が一度に乗り降りできる人数はドア数によって決まる。
* プレイヤーの運行計画定義により、電車はある区間、利用客を載せずに走る回送運転を行うことができる。
* 電車は識別と運用の煩雑さを避けるため、種別（普通、快速、特急など）をもたせることができる。種別には優劣がある。
* プレイヤーのダイヤ定義を効率化するため、同等の運行計画を持つ電車グループを定義できる。
* 下位種別の電車は、退避可能な駅で後続の上位種別を退避する。
* 先行電車に近づきすぎると、衝突回避のために電車は減速・停車する。
* 電車は信号機の指示に従う。（信号機は現時点では衝突回避のための手段）
* 線路のカーブがきつくなればなるほど、電車の最大走行速度は下がる。
* 電車は1台以上の車体から構成される（両数）。
* 車体には乗車可能な定員が存在する。
* 電車は両数やドア数を増やしたり、加速性能、最大走行速度を向上させることができる。
* 電車は他人が設置した線路の上を走ったり、駅に停車できる。ただし、これは将来を見据えた仕様で、現時点では禁止する。

## 要件のグループ分けと整理

### 1. 基盤システム要件

#### 1.1 全体システム要件
* 鉄道は線路、駅、電車から構成される
* 鉄道の建設・撤去はプレイヤーが行う（コスト設定あり）
* 他人が建設した鉄道は閲覧可能だが操作不可
* プレイヤー間での鉄道売買機能
* プレイヤー導入支援用の既存鉄道（無所属）の存在
* **分散処理対応**: マルチプレイヤー環境での同時操作制御
* **リアルタイム同期**: 位置情報配信処理、住民シミュレーション処理

#### 1.2 用語・スコープ定義
* 電車用語への統一（列車は使用しない）
* 旅客輸送のみ対応（貨物輸送は対象外）

### 2. 線路システム要件

#### 2.1 基本構造
* 線路は曲線から構成される
* 分岐・交差・合流機能
* 任意タイミングでの設置・撤去
* **衝突検知システム**: 極端に近い並行線路の禁止制御

#### 2.2 信号・制御システム
* 信号機配置による衝突回避システム
* **分散処理対応**: 信号制御処理の分離
* **安全性最優先**: 閉塞制御とトークンベース管理

#### 2.3 接続・拡張機能
* 他人の線路への接続機能（将来仕様、現時点では禁止）

### 3. 駅システム要件

#### 3.1 構造・機能要件
* 線路隣接配置
* 改札・プラットフォーム・通路の三要素構成
* 改札通過時間の設定
* 改札満杯時の待機列処理

#### 3.2 容量・性能要件
* ホーム定員制限
* 人数増加による移動速度低下
* **空間分割システム**: 駅構内の効率的な人流管理

#### 3.3 拡張性要件
* 複数改札・ホーム対応
* 極端に離れた場所への設置制限
* 共同所有駅機能（将来仕様、現時点では禁止）

### 4. 電車システム要件

#### 4.1 基本運行要件
* 線路上走行・駅停車・通過機能
* 任意配置・撤去機能
* 線路あたり最大1台の制限
* **物理演算**: 編成長による線路占有面積の計算

#### 4.2 経路・運行制御要件
* プレイヤー定義経路システム
* **経路探索処理**: A*・ダイクストラ法による最適経路計算
* 利用客完全乗降まで発車待機
* ドア数による乗降人数制限

#### 4.3 運行計画・種別要件
* 回送運転機能
* 電車種別システム（普通・快速・特急等）
* **ダイヤスケジューリング処理**: 時刻表管理・遅延計算
* 電車グループによる効率的ダイヤ管理
* 退避システム（下位種別の上位種別退避）

#### 4.4 安全・制御要件
* **衝突検知・回避システム**: 先行電車接近時の減速・停車
* 信号機指示への従順
* **物理演算**: カーブ速度制限システム

#### 4.5 車両性能・仕様要件
* 複数車体構成（両数）
* 車体定員制限
* 性能向上システム（両数・ドア数・加速性能・最大速度）
* 他人線路乗り入れ機能（将来仕様、現時点では禁止）

### 5. 追加システム要件（他ドキュメントから検証）

#### 5.1 住民システム要件
* **住民AI**: 1万-50万人の経路探索処理
* **TravelPlan**: 発車地・目的地・出発時刻・優先路線管理
* **PersonalInfo・EconomicStatus**: 住民の個人・経済情報管理

#### 5.2 分散処理要件
* **マイクロサービス化**: 読み取り専用サービス（経路探索・統計・マップレンダリング）
* **リアルタイムサービス**: ブロードキャスト・時刻表管理の分離
* **イベント駆動アーキテクチャ**: Kafka・Redis Streamsによる高スループット処理

#### 5.3 性能・スケーラビリティ要件
* **同時接続**: 100-1000人/マップ対応
* **電車運行**: 100-5000編成同時運行
* **レスポンス**: 位置更新30fps、操作反映<100ms
* **可用性**: 99.9%稼働率目標

#### 5.4 マルチプレイヤー要件
* **協調・競合モード**: プレイヤー間相互作用
* **リアルタイム同期**: WebSocket・WebRTCによる低レイテンシ通信
* **権威分散システム**: 安全性優先の階層的制御

## 必要なデータ構造設計

### 1. 線路システムデータ構造

```java
// 線路セグメント
@Entity
public class TrackSegment {
    @Id
    private String segmentId;
    
    // 幾何情報
    private List<Point3D> curve;          // 曲線座標列
    private double length;                // セグメント長
    private double maxSpeed;              // 最大速度制限
    
    // 接続情報
    private String startJunctionId;      // 開始接続点
    private String endJunctionId;        // 終了接続点
    
    // 所有・制御情報
    private String ownerId;              // 所有プレイヤー
    private boolean isOccupied;          // 占有状態
    private String occupyingTrainId;     // 占有中電車ID
    
    // 信号制御
    private List<Signal> signals;        // 配置信号機
    
    // 分散処理用
    private String nodeId;               // 処理ノードID
    private LocalDateTime lastUpdate;    // 最終更新時刻
}

// 接続点（分岐・合流）
@Entity
public class Junction {
    @Id
    private String junctionId;
    
    private Point3D position;
    private List<String> connectedSegments;
    private JunctionType type;           // MERGE, SPLIT, CROSS
    
    // 信号制御
    private Map<String, SignalState> directions; // 方向別信号状態
}

// 信号機
@Entity
public class Signal {
    @Id
    private String signalId;
    
    private Point3D position;
    private String segmentId;            // 設置セグメント
    private SignalType type;             // BLOCK, PATH, ABSOLUTE
    private SignalState state;           // RED, YELLOW, GREEN
    
    // 制御ロジック
    private String controlledBy;         // 制御ノードID
    private List<String> protectedSegments; // 保護対象セグメント
}
```

### 2. 駅システムデータ構造

```java
// 駅
@Entity
public class Station {
    @Id
    private String stationId;
    
    // 基本情報
    private String name;
    private Point3D position;
    private String ownerId;
    
    // 施設構成
    private List<Gate> gates;            // 改札
    private List<Platform> platforms;    // ホーム
    private List<Corridor> corridors;    // 通路
    
    // 容量情報
    private int totalCapacity;           // 駅全体容量
    private int currentOccupancy;        // 現在利用者数
    
    // 接続情報
    private List<String> connectedTracks; // 接続線路
    
    // 統計情報
    private StationStatistics statistics;
}

// 改札
@Entity
public class Gate {
    @Id
    private String gateId;
    
    private String stationId;
    private Point3D position;
    private int capacity;                // 同時通過可能人数
    private double processingTime;       // 一人当たり通過時間
    
    // 現在状態
    private Queue<String> waitingQueue;  // 待機列
    private int currentProcessing;       // 処理中人数
}

// プラットフォーム
@Entity
public class Platform {
    @Id
    private String platformId;
    
    private String stationId;
    private String trackSegmentId;       // 接続線路
    private int capacity;                // 収容定員
    private List<Door> doors;            // ドア位置
    
    // 現在状態
    private Set<String> currentPassengers; // 現在の利用者
    private boolean isTrainPresent;      // 電車停車中フラグ
    private String currentTrainId;       // 停車中電車ID
}
```

### 3. 電車システムデータ構造

```java
// 電車
@Entity
public class Train {
    @Id
    private String trainId;
    
    // 基本情報
    private String ownerId;
    private TrainType type;              // 普通、快速、特急等
    private String groupId;              // 電車グループID
    
    // 編成情報
    private List<Car> cars;              // 車両編成
    private int totalCapacity;           // 総定員
    private int doorCount;               // 総ドア数
    
    // 現在状態
    private TrainState currentState;
    private Point3D position;
    private double speed;                // 現在速度
    private double acceleration;         // 加速度
    private String currentSegmentId;     // 現在セグメント
    private Direction direction;         // 進行方向
    
    // 運行情報
    private Route assignedRoute;         // 運行経路
    private Schedule schedule;           // 運行スケジュール
    private Set<String> passengers;      // 乗客ID集合
    
    // 制御情報
    private boolean isControlledByPlayer; // プレイヤー制御フラグ
    private String controllingNodeId;    // 制御ノードID
    private LocalDateTime lastUpdate;
}

// 車両
@Entity
public class Car {
    @Id
    private String carId;
    
    private String trainId;
    private int carNumber;               // 編成内番号
    private int capacity;                // 定員
    private int doorCount;               // ドア数
    private CarType type;                // 車両種別
    
    // 性能
    private double maxSpeed;             // 最大速度
    private double acceleration;         // 加速性能
    private double brakingForce;         // 制動性能
}

// 運行経路
@Entity
public class Route {
    @Id
    private String routeId;
    
    private String trainId;
    private List<RoutePoint> routePoints; // 経路点列
    private boolean isLoop;              // 循環路線フラグ
    private OperationMode mode;          // 営業・回送
}

// 経路点
@Entity
public class RoutePoint {
    @Id
    private String routePointId;
    
    private String routeId;
    private int sequence;                // 順序
    private String stationId;            // 駅ID（停車時）
    private String segmentId;            // 通過セグメントID
    private RouteAction action;          // STOP, PASS, REVERSE
    private Duration stopTime;           // 停車時間（停車時のみ）
}
```

### 4. 住民システムデータ構造

```java
// 住民（shared-modelsから拡張）
@Entity
public class Resident {
    @Id
    private String residentId;
    
    // 基本情報
    private PersonalInfo personalInfo;   // 既存
    private EconomicStatus economicStatus; // 既存
    
    // 移動関連
    private TravelPlan currentTravelPlan; // 既存
    private Point3D currentPosition;
    private ResidentState state;         // WAITING, TRAVELING, AT_DESTINATION
    
    // AI行動
    private AIBehavior behavior;
    private List<TravelPattern> patterns; // 行動パターン
    
    // 分散処理用
    private String assignedNodeId;       // 処理ノードID
    private LocalDateTime lastUpdate;
}

// AI行動パターン
@Entity
public class AIBehavior {
    @Id
    private String behaviorId;
    
    private String residentId;
    private BehaviorType type;           // COMMUTER, TOURIST, RANDOM
    private Map<String, Double> preferences; // 路線選好
    private double timeValue;            // 時間価値
    private double comfortValue;         // 快適性価値
}
```

### 5. 分散処理用データ構造

```java
// 処理ノード管理
@Entity
public class ProcessingNode {
    @Id
    private String nodeId;
    
    private NodeType type;               // TRAIN_CONTROL, RESIDENT_AI, PATHFINDING
    private String serviceUrl;
    private NodeStatus status;           // ACTIVE, INACTIVE, OVERLOADED
    
    // 負荷情報
    private int assignedEntities;        // 割り当てエンティティ数
    private double cpuUsage;
    private double memoryUsage;
    
    // 管轄領域
    private List<String> managedSegments; // 管理セグメント
    private List<String> managedStations; // 管理駅
}

// イベントストリーム
@Entity
public class GameEvent {
    @Id
    private String eventId;
    
    private EventType type;              // TRAIN_MOVED, PASSENGER_BOARDED
    private String sourceEntityId;       // 発生元エンティティ
    private Map<String, Object> payload; // イベントデータ
    private LocalDateTime timestamp;
    
    // 分散処理用
    private String sourceNodeId;         // 発生ノード
    private List<String> targetNodes;    // 配信対象ノード
    private int version;                 // イベントバージョン
}
```

### 6. パフォーマンス最適化用データ構造

```java
// 空間インデックス
@Entity
public class SpatialIndex {
    @Id
    private String indexId;
    
    private BoundingBox bounds;          // 境界領域
    private int level;                   // 分割レベル
    private List<String> entities;       // 含まれるエンティティID
    private List<String> childIndices;   // 子インデックス
    
    // キャッシュ情報
    private LocalDateTime lastAccess;
    private int accessCount;
}

// 経路キャッシュ
@Entity
public class PathCache {
    @Id
    private String cacheKey;             // origin_destination_preferences
    
    private String originStationId;
    private String destinationStationId;
    private Map<String, Double> preferences; // 経路選好
    
    private List<String> optimalPath;    // 最適経路
    private double totalCost;            // 総コスト
    private Duration estimatedTime;      // 推定時間
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int usageCount;
}
```

これらのデータ構造により、マルチプレイヤー対応の大規模鉄道シミュレーションシステムの要件を満たし、分散処理とリアルタイム同期を実現できます。