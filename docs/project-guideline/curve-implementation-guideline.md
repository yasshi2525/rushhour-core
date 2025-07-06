# 鉄道線路曲線生成アルゴリズムと衝突判定の技術調査報告書

## 要約

本調査は、鉄道シミュレーションゲームにおける曲線生成アルゴリズムと衝突判定システムの技術的要件と実装方法について包括的な分析を行いました。**主要な発見として、鉄道線路には単純な補間曲線ではなく、クロソイド曲線（緩和曲線）の使用が必須**であり、C²連続性の確保が乗客の快適性と安全性に直接影響することが明らかになりました。Java実装においては、JTS Topology SuiteとApache Commons Mathの組み合わせが最も実用的なソリューションを提供します。

## 1. 点の配列から滑らかな曲線を生成するアルゴリズム

### 1.1 基本的な曲線生成アルゴリズム

**3次スプライン補間**は最も基本的でありながら強力な手法です。数学的定義として、n+1個のデータ点に対して以下の式で表現されます：

```
S(x) = ai(x-xi)³ + bi(x-xi)² + ci(x-xi) + di
```

各セグメントi は4つの係数（ai, bi, ci, di）を使用し、**C²連続性を保証**します。計算複雑度はThomas算法（TDMA）を使用してO(n)となり、実用的な性能を提供します。

**ベジェ曲線**は直感的な制御点操作を可能にします。3次ベジェ曲線は以下のBernstein基底関数を使用：

```
P(t) = (1-t)³P₀ + 3t(1-t)²P₁ + 3t²(1-t)P₂ + t³P₃
```

制御点の凸包内に曲線が収まる特性により、予測可能な挙動を示します。

**B-スプライン**は局所制御の利点を提供し、Cox-de Boor再帰式を使用：

```
Bi,k(t) = ((t-ti)/(ti+k-ti))Bi,k-1(t) + ((ti+k+1-t)/(ti+k+1-ti+1))Bi+1,k-1(t)
```

1つの制御点の変更が局所的な曲線セグメントにのみ影響するため、大規模な線路設計に適しています。

### 1.2 アルゴリズム選択基準

各アルゴリズムには明確な用途があります：

- **データ補間用途**：3次スプライン（全点を通過、C²連続性）
- **設計ツール用途**：ベジェ曲線（直感的制御）
- **局所編集用途**：B-スプライン、Catmull-Rom（局所制御）
- **産業用途**：NURBS（最も包括的だが複雑）

## 2. 鉄道線路に適した曲線生成手法

### 2.1 緩和曲線（イージング曲線）

**鉄道工学における最重要概念**として、緩和曲線は直線部と円弧部の間の急激な変化を防ぎます。標準的な実装では40-80メートル（時速100km）、高速鉄道では最大180メートルの長さが必要です。

**設計基準**：
- 最大横加速度変化率：0.03g/秒（AREMA標準）
- 曲率とカントの緩やかな導入
- 乗客の快適性制約の厳格な遵守

### 2.2 クロソイド曲線（オイラーの螺旋）

**数学的基礎**として、クロソイド曲線は弧長に対して曲率が線形に変化する特性を持ちます：

```
曲率 κ = s/A²
```

ここで、sは弧長、Aは螺旋パラメータです。Leonhard Euler（1744年）により最初に導出され、1947年にAREMAが採用した標準的手法です。

**実用的な設計パラメータ**：
- 螺旋長さ：L = A²/R（Rは円弧半径）
- 最適設計：一定のAを持つ配置により均一な快適性条件を提供
- 座標方程式：フレネル積分に基づく

### 2.3 工学的制約

**最小曲率半径**は列車の種類と速度に基づいて厳格に規定されています：

- **一般互換性**：288フィート（88m）絶対最小値
- **推奨最小値**：410フィート（125m）標準運用
- **長編成貨物列車**：574フィート（175m）推奨最小値
- **高速鉄道**：300km/h で4,000m、400km/h で7,000m

**カント（スーパーエレベーション）制限**：
- 北米（AREMA）：最大6インチ（152mm）
- 欧州標準：160-180mm最大
- 高速鉄道：貨物列車を除外時に最大180mm

## 3. C1連続性とC2連続性の違いと鉄道での重要性

### 3.1 数学的定義

**C¹連続性**は関数値と1次導関数の連続性を要求し、曲線の接線方向が連続であることを意味します。**C²連続性**はさらに2次導関数の連続性を要求し、曲率の連続性を保証します。

**幾何学的連続性**（G¹、G²）は形状に焦点を当て、導関数ベクトルが平行であることを要求しますが、大きさの違いは許容します。

### 3.2 鉄道での実用的重要性

**C¹連続性の重要性**：
- 軌道方向の急激な変化を防止
- 横方向の急激な動きを排除
- 脱線リスクの最小化

**C²連続性の重要性**：
- 横加速度の急激な変化を防止
- 乗客の快適性確保
- 車両および軌道の摩耗軽減

**従来のクロソイド曲線の限界**：横加速度変化率（LCA）に不連続性があり、高速運用では不快感と摩耗増加の原因となります。現代の解決策として、SPTC（対称投影移行曲線）などの先進的螺旋設計が開発されています。

### 3.3 快適性基準

**許容限界**：
- 横加速度：標準快適性で0.1g未満
- ジャーク率：0.03g/秒以下
- 体重変化感覚：通常体重の7%以内の変動

## 4. 曲線同士の交差・衝突判定アルゴリズム

### 4.1 パラメトリック曲線交点検出

**主要アルゴリズム**：

**暗黙化手法**：パラメトリック曲線を暗黙形式に変換し、結果行列を使用します。計算複雑度はO(d⁶)（d次曲線）ですが、高精度を実現します。

**分割アルゴリズム**：再帰的空間分割により、平均的にO(n log n)の性能を提供します。

**代数的除去法**：グレブナー基底と結果計算により、正確な交点を求めます。

### 4.2 衝突判定最適化

**空間データ構造の性能比較**：

| 手法 | 構築時間 | クエリ時間 | メモリ | 動的更新 |
|------|----------|------------|--------|----------|
| 四分木 | O(n log n) | O(log n + k) | O(n) | O(log n) |
| R-tree | O(n log n) | O(log n + k) | O(n) | O(log n) |
| グリッドハッシュ | O(n) | O(1 + k) | O(n + m) | O(1) |
| Sweep & Prune | O(n log n) | O(n + k) | O(n) | O(n) |

**実装推奨**：
- 疎なネットワーク：Sweep and Prune
- 密な都市部：空間ハッシュ
- 動的環境：R-tree
- 均等分布：四分木

### 4.3 鉄道特有の衝突検出

**軌道離間距離要件**：
- 最小側方離間：軌道中心から8-12フィート
- 垂直離間：レール上22-23フィート（貨物）、19フィート（旅客）
- 軌道間隔：並行軌道中心間13'6"最小値

**競合検出タイプ**：
- 経路競合：連動装置を通る重複列車経路
- 時間競合：連続列車間の不十分な間隔
- 資源競合：プラットフォーム、軌道、信号の競合

## 5. 曲線の最小曲率半径制約

### 5.1 速度別要件

**標準軌間（1435mm）要件**：
- 高速鉄道300km/h：最小半径4,000m
- 高速鉄道400km/h：最小半径7,000m（カント・カント不足含む）
- 一般運用：410フィート（125m）推奨最小値

**速度-半径関係式**：
```
r = Gv²/[g(ha + hb)]
```
（G：軌間、v：速度、g：重力、ha：カント、hb：カント不足）

### 5.2 勾配制限

**支配勾配**：
- 平坦地：1/150～1/200（0.5-0.67%）
- 丘陵地：1/100（1.0%）
- 本線：一般的に1%以下

**曲線での勾配補正**：
- 広軌：曲線1度当たり0.04%
- 狭軌：曲線1度当たり0.02%

## 6. Java実装例とライブラリ

### 6.1 推奨ライブラリ組み合わせ

**JTS Topology Suite**：堅牢な幾何学操作
```xml
<dependency>
    <groupId>org.locationtech.jts</groupId>
    <artifactId>jts-core</artifactId>
    <version>1.20.0</version>
</dependency>
```

**Apache Commons Math**：数学的曲線フィッティング
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-math3</artifactId>
    <version>3.6.1</version>
</dependency>
```

### 6.2 実装パターン

**オブジェクト指向曲線表現**：
```java
public abstract class RailwayCurve {
    protected List<Point2D> controlPoints;
    protected CurveType type;
    protected double[] parameters;
    
    public abstract Point2D evaluate(double t);
    public abstract double getCurvature(double t);
    public abstract boolean validateRailwayConstraints();
}

public class ClothoidCurve extends RailwayCurve {
    private double spiralParameter;
    
    @Override
    public Point2D evaluate(double t) {
        // フレネル積分を使用したクロソイド評価
        return evaluateClothoid(t, spiralParameter);
    }
    
    @Override
    public boolean validateRailwayConstraints() {
        double minRadius = 1.0 / getMaxCurvature();
        return minRadius >= RAILWAY_MIN_RADIUS;
    }
}
```

### 6.3 Spring Boot統合

**RESTful API設計**：
```java
@RestController
@RequestMapping("/api/railway-curves")
public class RailwayCurveController {
    
    @PostMapping("/generate")
    public ResponseEntity<CurveResponseDTO> generateCurve(
            @RequestBody @Valid RailwayCurveRequest request) {
        
        // 鉄道制約の検証
        validateRailwayConstraints(request);
        
        // 曲線生成
        RailwayCurve curve = curveService.generateRailwayCurve(request);
        
        return ResponseEntity.ok(new CurveResponseDTO(curve));
    }
    
    @GetMapping("/{id}/collision-check")
    public ResponseEntity<CollisionResult> checkCollision(
            @PathVariable Long id,
            @RequestParam Long otherCurveId) {
        
        CollisionResult result = collisionService
            .checkCurveCollision(id, otherCurveId);
        return ResponseEntity.ok(result);
    }
}
```

### 6.4 性能最適化

**キャッシュ戦略**：
```java
@Service
public class RailwayCurveService {
    
    @Cacheable(value = "curves", key = "#id")
    public RailwayCurve findById(Long id) {
        return curveRepository.findById(id)
            .orElseThrow(() -> new CurveNotFoundException(id));
    }
    
    @Cacheable(value = "collisionResults", 
               key = "#id1 + '_' + #id2")
    public CollisionResult checkCollision(Long id1, Long id2) {
        return collisionDetector.detect(findById(id1), findById(id2));
    }
}
```

## 7. 衝突判定実装戦略

### 7.1 アルゴリズム選択指針

**鉄道用途推奨**：
- **広域フェーズ**：疎なネットワーク向けSweep and Prune
- **狭域フェーズ**：凸形状向けGJK/EPA
- **曲線交差**：堅牢性重視の分割法
- **スケジューリング**：最適化用遺伝的アルゴリズム

### 7.2 実装考慮事項

**数値安定性**：
- 幾何学計算に倍精度を使用
- 堅牢な幾何学述語の実装
- 適切な許容値（通常1e-6～1e-9）
- 退化ケースの明示的処理

**性能最適化**：
- 空間的・時間的コヒーレンスの活用
- ベクトル化操作向けSIMD命令
- キャッシュ性能最適化のメモリレイアウト
- 並列アルゴリズム向けGPUアクセラレーション

## 結論

鉄道線路の曲線生成は、単純な数学的補間を超えた工学的精密性を要求します。**クロソイド曲線によるC²連続性の確保**が安全性と快適性の基盤となり、適切な衝突判定システムが運用効率を保証します。Java実装においては、JTS Topology SuiteとApache Commons Mathの組み合わせが実用的な解決策を提供し、Spring Bootフレームワークにより企業級アプリケーションの開発が可能になります。

現代の鉄道シミュレーションでは、理論的な数学的基礎と実用的な工学的制約の両方を理解し、適切なアルゴリズムの選択と実装最適化を行うことが成功の鍵となります。
