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

## データ表現戦略（ハイブリッド型）

このプロジェクトでは、**ハイブリッド型データ表現**を採用し、用途に応じて最適な技術を選択します：

- **リアルタイムデータ**: Redis + Jackson による JSON シリアライズ（30fps更新対応）
- **永続化データ**: PostgreSQL + JPA（ACID保証、リレーション管理）
- **サービス間通信**: Protocol Buffers（型安全性、効率性）

## 必要なデータ構造設計

### 1. 線路システムデータ構造

#### 1.1 永続化用データ構造（PostgreSQL + JPA）

```java
// 線路セグメント（永続化）
@Entity
@Table(name = "track_segments")
@Cacheable
public class TrackSegment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String segmentId;
    
    // 幾何情報
    @Convert(converter = CurvePointsConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<Point3D> curve;          // 曲線座標列
    
    @Column(nullable = false)
    private double length;                // セグメント長
    
    @Column(nullable = false)
    private double maxSpeed;              // 最大速度制限
    
    // 接続情報
    @Column(name = "start_junction_id")
    private String startJunctionId;      // 開始接続点
    
    @Column(name = "end_junction_id")
    private String endJunctionId;        // 終了接続点
    
    // 所有・制御情報
    @Column(name = "owner_id", nullable = false)
    private String ownerId;              // 所有プレイヤー
    
    // 信号制御（別テーブルとのリレーション）
    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Signal> signals;        // 配置信号機
    
    // 監査用
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;                // 楽観的ロック
}

// リアルタイム状態管理用（Redis）
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackSegmentState {
    @JsonProperty("segmentId")
    private String segmentId;
    
    @JsonProperty("isOccupied")
    private boolean isOccupied;          // 占有状態
    
    @JsonProperty("occupyingTrainId")
    private String occupyingTrainId;     // 占有中電車ID
    
    @JsonProperty("nodeId")
    private String nodeId;               // 処理ノードID
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;          // 最終更新時刻
}

// 接続点（分岐・合流）
@Entity
@Table(name = "junctions")
@Cacheable
public class Junction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String junctionId;
    
    @Embedded
    private Location position;
    
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<String> connectedSegments;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JunctionType type;           // MERGE, SPLIT, CROSS
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

// 信号機状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class JunctionState {
    @JsonProperty("junctionId")
    private String junctionId;
    
    @JsonProperty("directions")
    private Map<String, SignalState> directions; // 方向別信号状態
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}

// 信号機（永続化）
@Entity
@Table(name = "signals")
@Cacheable
public class Signal {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String signalId;
    
    @Embedded
    private Location position;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id")
    @JsonBackReference
    private TrackSegment segment;        // 設置セグメント
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SignalType type;             // BLOCK, PATH, ABSOLUTE
    
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<String> protectedSegments; // 保護対象セグメント
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

// 信号機状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignalState {
    @JsonProperty("signalId")
    private String signalId;
    
    @JsonProperty("state")
    private SignalDisplayState state;    // RED, YELLOW, GREEN
    
    @JsonProperty("controlledBy")
    private String controlledBy;         // 制御ノードID
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}
```

#### 1.2 Protocol Buffers対応（サービス間通信）

```protobuf
// proto/track.proto
syntax = "proto3";

message TrackSegmentUpdate {
    string segment_id = 1;
    bool is_occupied = 2;
    string occupying_train_id = 3;
    int64 timestamp = 4;
}

message SignalStateUpdate {
    string signal_id = 1;
    SignalDisplayState state = 2;
    string controlled_by = 3;
    int64 timestamp = 4;
}

enum SignalDisplayState {
    RED = 0;
    YELLOW = 1;
    GREEN = 2;
}
```

```java
// Java側のProtocolBuffers変換
@Component
public class TrackStateMapper {
    public TrackSegmentState fromProto(TrackSegmentUpdate proto) {
        TrackSegmentState state = new TrackSegmentState();
        state.setSegmentId(proto.getSegmentId());
        state.setOccupied(proto.getIsOccupied());
        state.setOccupyingTrainId(proto.getOccupyingTrainId());
        state.setLastUpdate(Instant.ofEpochMilli(proto.getTimestamp()));
        return state;
    }
    
    public TrackSegmentUpdate toProto(TrackSegmentState state) {
        return TrackSegmentUpdate.newBuilder()
            .setSegmentId(state.getSegmentId())
            .setIsOccupied(state.isOccupied())
            .setOccupyingTrainId(state.getOccupyingTrainId())
            .setTimestamp(state.getLastUpdate().toEpochMilli())
            .build();
    }
}
```

### 2. 駅システムデータ構造

#### 2.1 永続化用データ構造

```java
// 駅（永続化）
@Entity
@Table(name = "stations")
@Cacheable
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String stationId;
    
    // 基本情報
    @Column(nullable = false)
    private String name;
    
    @Embedded
    private Location location;
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
    
    // 施設構成（別テーブルとのリレーション）
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Gate> gates;            // 改札
    
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Platform> platforms;    // ホーム
    
    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Corridor> corridors;    // 通路
    
    // 容量情報
    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;           // 駅全体容量
    
    // 接続情報
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<String> connectedTracks; // 接続線路
    
    // 監査用
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}

// 駅状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationState {
    @JsonProperty("stationId")
    private String stationId;
    
    @JsonProperty("currentOccupancy")
    private int currentOccupancy;        // 現在利用者数
    
    @JsonProperty("gateStates")
    private Map<String, GateState> gateStates; // 改札状態
    
    @JsonProperty("platformStates")
    private Map<String, PlatformState> platformStates; // ホーム状態
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}

// 改札（永続化）
@Entity
@Table(name = "gates")
@Cacheable
public class Gate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String gateId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @JsonBackReference
    private Station station;
    
    @Embedded
    private Location position;
    
    @Column(nullable = false)
    private int capacity;                // 同時通過可能人数
    
    @Column(name = "processing_time", nullable = false)
    private double processingTime;       // 一人当たり通過時間
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

// 改札状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GateState {
    @JsonProperty("gateId")
    private String gateId;
    
    @JsonProperty("waitingQueue")
    private Queue<String> waitingQueue;  // 待機列
    
    @JsonProperty("currentProcessing")
    private int currentProcessing;       // 処理中人数
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}

// プラットフォーム（永続化）
@Entity
@Table(name = "platforms")
@Cacheable
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String platformId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    @JsonBackReference
    private Station station;
    
    @Column(name = "track_segment_id")
    private String trackSegmentId;       // 接続線路
    
    @Column(nullable = false)
    private int capacity;                // 収容定員
    
    @Convert(converter = DoorListConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<Door> doors;            // ドア位置
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

// プラットフォーム状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformState {
    @JsonProperty("platformId")
    private String platformId;
    
    @JsonProperty("currentPassengers")
    private Set<String> currentPassengers; // 現在の利用者
    
    @JsonProperty("isTrainPresent")
    private boolean isTrainPresent;      // 電車停車中フラグ
    
    @JsonProperty("currentTrainId")
    private String currentTrainId;       // 停車中電車ID
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}
```

### 3. 電車システムデータ構造（ハイブリッド設計）

#### 3.1 永続化用データ構造

```java
// 電車（永続化）
@Entity
@Table(name = "trains")
@Cacheable
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String trainId;
    
    // 基本情報
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainType type;              // 普通、快速、特急等
    
    @Column(name = "group_id")
    private String groupId;              // 電車グループID
    
    // 編成情報（別テーブルとのリレーション）
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Car> cars;              // 車両編成
    
    @Column(name = "total_capacity", nullable = false)
    private int totalCapacity;           // 総定員
    
    @Column(name = "door_count", nullable = false)
    private int doorCount;               // 総ドア数
    
    // 運行情報
    @OneToOne(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Route assignedRoute;         // 運行経路
    
    @OneToOne(mappedBy = "train", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Schedule schedule;           // 運行スケジュール
    
    // 制御情報
    @Column(name = "is_controlled_by_player")
    private boolean isControlledByPlayer; // プレイヤー制御フラグ
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}

// 電車状態（リアルタイム）
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainState {
    @JsonProperty("trainId")
    private String trainId;
    
    @JsonProperty("currentState")
    private TrainOperationState currentState; // STOPPED, MOVING, BOARDING
    
    @JsonProperty("position")
    private Position position;
    
    @JsonProperty("speed")
    private double speed;                // 現在速度
    
    @JsonProperty("acceleration")
    private double acceleration;         // 加速度
    
    @JsonProperty("currentSegmentId")
    private String currentSegmentId;     // 現在セグメント
    
    @JsonProperty("direction")
    private Direction direction;         // 進行方向
    
    @JsonProperty("passengers")
    private Set<String> passengers;      // 乗客ID集合
    
    @JsonProperty("controllingNodeId")
    private String controllingNodeId;    // 制御ノードID
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
    
    // Jackson/Proto 両対応
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }
    }
}
```

#### 3.2 Protocol Buffers対応

```protobuf
// proto/train.proto
syntax = "proto3";

message TrainPositionUpdate {
    string train_id = 1;
    double x = 2;
    double y = 3;
    double speed = 4;
    double acceleration = 5;
    string current_segment_id = 6;
    Direction direction = 7;
    int64 timestamp = 8;
}

message TrainStateUpdate {
    string train_id = 1;
    TrainOperationState state = 2;
    repeated string passengers = 3;
    string controlling_node_id = 4;
    int64 timestamp = 5;
}

enum TrainOperationState {
    STOPPED = 0;
    MOVING = 1;
    BOARDING = 2;
    EMERGENCY = 3;
}

enum Direction {
    FORWARD = 0;
    BACKWARD = 1;
}
```

### 4. 住民システムデータ構造（ECS対応）

```java
// 住民（shared-modelsから拡張）
@Entity
@Table(name = "residents")
@Cacheable
public class Resident {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String residentId;
    
    // 基本情報（既存shared-modelsとの互換性）
    @Embedded
    private PersonalInfo personalInfo;   // 既存
    
    @Embedded
    private EconomicStatus economicStatus; // 既存
    
    // AI行動
    @OneToOne(mappedBy = "resident", cascade = CascadeType.ALL)
    @JsonManagedReference
    private AIBehavior behavior;
    
    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<TravelPattern> patterns; // 行動パターン
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Version
    private Long version;
}

// 住民状態（リアルタイム）
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResidentState {
    @JsonProperty("residentId")
    private String residentId;
    
    @JsonProperty("currentTravelPlan")
    private TravelPlan currentTravelPlan; // 既存
    
    @JsonProperty("currentPosition")
    private Position currentPosition;
    
    @JsonProperty("state")
    private ResidentActivityState state; // WAITING, TRAVELING, AT_DESTINATION
    
    @JsonProperty("assignedNodeId")
    private String assignedNodeId;       // 処理ノードID
    
    @JsonProperty("lastUpdate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Instant lastUpdate;
}
```

### 5. フェーズ別実装戦略

#### Phase 1: モノリス期（Jackson + Redis + PostgreSQL）

```java
// Redis設定
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        Jackson2JsonRedisSerializer<Object> serializer = 
            new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(mapper);
        
        template.setDefaultSerializer(serializer);
        return template;
    }
}

// サービス実装例
@Service
@Transactional
public class TrainStateService {
    @Autowired
    private RedisTemplate<String, TrainState> redisTemplate;
    
    @Autowired
    private TrainRepository trainRepository;
    
    @Cacheable(value = "trainStates", key = "#trainId")
    public TrainState getTrainState(String trainId) {
        String key = "train:state:" + trainId;
        return redisTemplate.opsForValue().get(key);
    }
    
    public void updateTrainState(TrainState state) {
        String key = "train:state:" + state.getTrainId();
        redisTemplate.opsForValue().set(key, state, Duration.ofSeconds(30));
        
        // 非同期でイベント発行
        applicationEventPublisher.publishEvent(new TrainStateUpdatedEvent(state));
    }
}
```

#### Phase 2: マイクロサービス期（Protocol Buffers追加）

```java
// gRPCサービス実装
@GrpcService
public class TrainPositionGrpcService extends TrainPositionServiceGrpc.TrainPositionServiceImplBase {
    
    @Autowired
    private TrainStateService trainStateService;
    
    @Autowired
    private TrainStateMapper mapper;
    
    @Override
    public void updatePosition(TrainPositionUpdate request, 
                              StreamObserver<TrainPositionResponse> responseObserver) {
        TrainState state = mapper.fromProto(request);
        trainStateService.updateTrainState(state);
        
        TrainPositionResponse response = TrainPositionResponse.newBuilder()
            .setSuccess(true)
            .setTimestamp(System.currentTimeMillis())
            .build();
            
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
```

#### Phase 3: 大規模対応期（最適化）

```java
// 高速バイナリシリアライズ
@Component
public class OptimizedTrainStateSerializer {
    private final Kryo kryo = new Kryo();
    
    @PostConstruct
    public void init() {
        kryo.register(TrainState.class);
        kryo.register(Position.class);
        kryo.setReferences(false);
    }
    
    public byte[] serialize(TrainState state) {
        try (Output output = new Output(256, -1)) {
            kryo.writeObject(output, state);
            return output.toBytes();
        }
    }
    
    public TrainState deserialize(byte[] data) {
        try (Input input = new Input(data)) {
            return kryo.readObject(input, TrainState.class);
        }
    }
}

// 空間インデックスキャッシュ
@Service
public class SpatialCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Cacheable(value = "spatialIndex", key = "#bounds.toString()")
    public List<String> getEntitiesInBounds(BoundingBox bounds) {
        // Redisの地理空間インデックスを使用
        String key = "spatial:entities";
        return redisTemplate.opsForGeo()
            .radiusByMember(key, bounds.getCenter(), 
                           Distance.of(bounds.getRadius(), DistanceUnit.KILOMETERS))
            .getContent()
            .stream()
            .map(result -> result.getContent().getName())
            .collect(Collectors.toList());
    }
}
```

### 6. 技術選択まとめ

| 用途 | Phase 1 | Phase 2 | Phase 3 |
|------|---------|---------|---------|
| リアルタイムデータ | Jackson + Redis | + Protocol Buffers | + Kryo最適化 |
| 永続化データ | JPA + PostgreSQL | + 読み取り専用レプリカ | + 分散DB |
| サービス間通信 | REST API | gRPC + Protocol Buffers | + 負荷分散 |
| キャッシュ | Spring Cache | + 分散キャッシュ | + 空間インデックス |
| イベント配信 | Spring Events | Kafka + Jackson | + バイナリ圧縮 |

この段階的なアプローチにより、各フェーズで必要な性能とスケーラビリティを確保しながら、技術的負債を最小化した実装が可能です。

## DDDアプローチにおける業務ロジック配置指針

### 1. 各層の役割と責務

共通モデル（packages/shared-models）

```java
// ✅ 適切な使用例
@Data
public class Track {
    private String id;
    private double length;
    private double maxSpeed;

    // ビジネスルールのみ - 永続化やインフラ依存なし
    public boolean canAccommodateSpeed(double requestedSpeed) {
        return requestedSpeed <= maxSpeed;
    }

    public boolean isLongEnough(double minimumLength) {
        return length >= minimumLength;
    }
}
```

Entity（JPA永続化モデル）

```java
// ✅ 適切な使用例
@Entity
public class TrackEntity implements Serializable {
    // データベース構造とマッピングのみ
    // ビジネスロジックは含まない
    @Id private String id;
    @Column private Double length;
    @OneToMany private List<SignalEntity> signals;
}
```

### 2. 業務ロジック実装場所の指針

「線路を延長する」ロジックの実装

```java
// 🎯 ドメインサービス層での実装
@Service
@Transactional
public class TrackExtensionService {

    private final TrackRepository trackRepository;
    private final JunctionRepository junctionRepository;
    private final TrackMapper trackMapper;

    /**
    * 線路延長の複雑な業務ロジック
    */
    public Track extendTrack(String trackId, double extensionLength, Point3D endPoint) {
        // 1. ドメインモデルで業務ルール検証
        Track track = findTrackOrThrow(trackId);

        if (!track.canBeExtended(extensionLength)) {
            throw new TrackExtensionNotAllowedException("Track cannot be extended beyond maximum length");
        }

        // 2. 複雑な業務ロジック実行
        Junction newJunction = createJunctionAt(endPoint);
        Track extendedTrack = track.extend(extensionLength, newJunction.getId());

        // 3. 永続化（インフラ層へ移譲）
        TrackEntity savedEntity = trackRepository.save(trackMapper.toEntity(extendedTrack));
        return trackMapper.toDomain(savedEntity);
    }
}
```

「ホームを増やす」ロジックの実装

```java
// 🎯 集約ルート（Station）での調整
@Service
@Transactional
public class StationExpansionService {

    /**
    * 駅へのホーム追加（集約内の整合性保証）
    */
    public Station addPlatform(String stationId, Platform newPlatform) {
        Station station = findStationOrThrow(stationId);

        // ドメインモデルでの制約チェック
        if (!station.canAccommodateNewPlatform(newPlatform)) {
            throw new PlatformCapacityExceededException("Station capacity exceeded");
        }

        // 集約内での状態変更
        Station updatedStation = station.addPlatform(newPlatform);

        return stationRepository.save(stationMapper.toEntity(updatedStation))
            .let(stationMapper::toDomain);
    }
}
```

### 3. レイヤー別責務マトリックス

| 操作       | 共通モデル | Entity | Repository | Service | Controller |
|----------|-------|--------|------------|---------|------------|
| データ検証    | ✅     | ❌      | ❌          | ✅       | ❌          |
| 業務ルール    | ✅     | ❌      | ❌          | ✅       | ❌          |
| 永続化      | ❌     | ✅      | ✅          | ❌       | ❌          |
| トランザクション | ❌     | ❌      | ❌          | ✅       | ❌          |
| API変換    | ❌     | ❌      | ❌          | ❌       | ✅          |

## MapStructマッパー実装戦略

自動実装 vs カスタム実装

MapStructによる自動生成（推奨）:
```java
// 現在のインタフェース → 自動実装される
@Mapper(componentModel = "spring")
public interface StationMapper {
    Station toDomain(StationEntity entity);
    StationEntity toEntity(Station domain);
}

// ビルド時に以下が自動生成される:
// StationMapperImpl.java
```

複雑なマッピングが必要な場合:
```java
@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface TrackMapper {

    @Mapping(target = "signals", source = "signals")
    @Mapping(target = "curve", qualifiedByName = "mapCurvePoints")
    Track toDomain(TrackEntity entity);

    // カスタムマッピングメソッド
    @Named("mapCurvePoints")
    default List<Point3D> mapCurvePoints(List<Point3DEmbeddable> entities) {
        return entities.stream()
            .sorted(Comparator.comparing(Point3DEmbeddable::getSequenceOrder))
            .map(this::mapPoint3D)
            .collect(Collectors.toList());
    }

    Point3D mapPoint3D(Point3DEmbeddable entity);
}
```

推奨実装パターン:

1. 基本マッピング: MapStruct自動生成に委譲
2. 複雑な変換: @Namedメソッドでカスタム実装
3. validation: ドメインモデル内で実装
4. 特殊ケース: 専用ServiceやFactoryクラス作成

これらの方針により、DDDの原則を保ちながら効率的な実装が可能になります。
