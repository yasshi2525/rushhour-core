# GradleマルチプロジェクトでのSonarQube設定完全ガイド

SonarQubeをGradleマルチプロジェクト環境で効果的に設定する方法について、特に鉄道シミュレーションゲームのような大規模モノレポプロジェクトに焦点を当てた包括的な調査結果をお届けします。Java + JavaScript混在環境でのファイル重複問題の解決策から、実践的なCI/CD統合まで詳しく解説します。

## ファイル重複インデックス問題の根本原因と解決策

**ファイル重複エラーは主に3つの原因から発生します**。最も一般的なのは、`sonar.sources`と`sonar.tests`のパス設定が重複している場合です。例えば、`sonar.sources`を`src`と設定し、`sonar.tests`を`src/test`と設定すると、testディレクトリが両方でスキャンされてしまいます。

もう一つの原因は、ルートプロジェクトにソースコードが存在し、かつサブモジュールも定義されている場合です。SonarQube 6.4以前のバージョンでは、この構成を正しく処理できません。3つ目は、モジュールキーに無効な文字（特に'/'）が含まれている場合で、ネストされたディレクトリ構造で頻繁に発生します。

**解決策として最も効果的なアプローチ**は、SonarQubeプラグインをルートプロジェクトのみに適用し、サブプロジェクトでは設定の継承を活用することです：

```gradle
// ルートbuild.gradle - プラグインは一度だけ適用
plugins {
    id 'org.sonarqube' version '5.0.0.4638'
}

// グローバル設定
sonar {
    properties {
        property 'sonar.projectName', 'Railway Simulation'
        property 'sonar.projectKey', 'com.railsim:railway-simulation'
        property 'sonar.host.url', 'http://localhost:9000'
    }
}

// サブプロジェクト共通設定
subprojects {
    sonar {
        properties {
            property 'sonar.sources', 'src/main/java'
            property 'sonar.tests', 'src/test/java'
            // モジュールキーの'/'を':'に置換
            property 'sonar.moduleKey', 
                "${rootProject.group}:${rootProject.name}:${project.name.replaceAll('/', ':')}"
        }
    }
}
```

## Java + JavaScript混在プロジェクトの最適設定

鉄道シミュレーションゲームのような複雑なプロジェクトでは、バックエンドのSpring BootとフロントエンドのReact/Three.jsを適切に分離しながら統合的に分析する必要があります。

### 言語別ソース設定の最適化

```gradle
sonarqube {
    properties {
        // マルチ言語ソース設定
        property "sonar.sources", "src/main/java,src/main/webapp,frontend/src"
        property "sonar.tests", "src/test/java,frontend/src/**/*.spec.ts,frontend/src/**/*.test.js"
        
        // Java固有設定
        property "sonar.java.binaries", "build/classes/java/main"
        property "sonar.java.libraries", "build/libs/**/*.jar"
        property "sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml"
        
        // JavaScript/TypeScript設定
        property "sonar.javascript.lcov.reportPaths", "frontend/coverage/lcov.info"
        property "sonar.typescript.lcov.reportPaths", "frontend/coverage/lcov.info"
    }
}
```

### 包括的な除外パターン戦略

ファイル重複を防ぎ、分析の精度を高めるための除外設定：

```gradle
property "sonar.exclusions", [
    // ビルド生成物
    "**/build/**/*",
    "**/target/**/*",
    "**/dist/**/*",
    
    // 依存関係
    "**/node_modules/**/*",
    "**/bower_components/**/*",
    
    // 生成コード
    "**/generated/**/*",
    "**/src/main/java/**/generated/**/*",
    
    // 静的リソース
    "**/assets/vendor/**/*",
    "**/static/js/libs/**/*"
].join(',')

// 言語間の干渉を防ぐマルチクライテリア設定
property "sonar.issue.ignore.multicriteria", "e1,e2"
property "sonar.issue.ignore.multicriteria.e1.ruleKey", "java:*"
property "sonar.issue.ignore.multicriteria.e1.resourceKey", "**/*.js,**/*.ts"
property "sonar.issue.ignore.multicriteria.e2.ruleKey", "javascript:*"
property "sonar.issue.ignore.multicriteria.e2.resourceKey", "**/*.java"
```

## モノレポ構成でのベストプラクティス実装

鉄道シミュレーションプロジェクトの構造に最適化された設定例を示します。

### プロジェクトキー命名規則

モノレポでは一貫性のある命名規則が重要です：

```
{organization}_{project}_{module-type}_{module-name}

例：
- railsim_gaming_app_game-server
- railsim_gaming_package_spatial-grid
- railsim_gaming_service_pathfinding-service
```

### GitHub Actionsによる並列分析設定

```yaml
name: SonarQube Analysis
on:
  push:
    branches: [main, develop]
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  analyze-modules:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        include:
          - module: apps/game-server
            type: java
          - module: apps/web-client
            type: javascript
          - module: packages/spatial-grid
            type: java
          - module: services/pathfinding-service
            type: java
            
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
          
      - name: Set up environment
        uses: actions/setup-java@v3
        if: matrix.type == 'java'
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Analyze ${{ matrix.module }}
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
          SONAR_HOST_URL: ${{ secrets.SONAR_HOST_URL }}
        run: |
          cd ${{ matrix.module }}
          if [ "${{ matrix.type }}" == "java" ]; then
            ./gradlew test jacocoTestReport sonarqube \
              -Dsonar.projectKey=railsim_gaming_${{ matrix.module }}
          else
            npm install
            npm run test:coverage
            npx sonar-scanner \
              -Dsonar.projectKey=railsim_gaming_${{ matrix.module }}
          fi
```

### Three.js特有の設定最適化

ゲームエンジン部分には特別な考慮が必要です：

```javascript
// packages/game-engine/sonar-project.js
const sonarqubeScanner = require('sonarqube-scanner');

sonarqubeScanner({
    serverUrl: process.env.SONAR_HOST_URL,
    token: process.env.SONAR_TOKEN,
    options: {
        'sonar.projectKey': 'railsim_gaming_package_game-engine',
        'sonar.sources': './src',
        'sonar.exclusions': '**/assets/**,**/models/**,**/textures/**',
        
        // Three.js特有の複雑性ルール緩和
        'sonar.issue.ignore.multicriteria': 'e1,e2,e3',
        'sonar.issue.ignore.multicriteria.e1.ruleKey': 'javascript:S3776',
        'sonar.issue.ignore.multicriteria.e1.resourceKey': '**/math/**',
        'sonar.issue.ignore.multicriteria.e2.ruleKey': 'javascript:S138',
        'sonar.issue.ignore.multicriteria.e2.resourceKey': '**/shaders/**'
    }
});
```

## 実践的なトラブルシューティング

### デバッグコマンドセット

問題の診断と解決に役立つコマンド：

```bash
# 設定の検証（実行せずに確認）
./gradlew sonar --dry-run -Dsonar.verbose=true

# すべてのプロパティを表示
./gradlew sonar -Dsonar.showSettings=true

# メモリ使用状況の確認
./gradlew sonar -Dsonar.scanner.opts="-XX:+PrintGCDetails"
```

### メモリとパフォーマンスの最適化

大規模プロジェクト向けの設定：

```properties
# gradle.properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=512m
org.gradle.daemon=true
org.gradle.parallel=true
systemProp.sonar.scanner.loadOptimization=true
```

### 段階的な分析戦略

```gradle
// インクリメンタル分析の有効化
sonarqube {
    properties {
        property "sonar.analysis.mode", "incremental"
        property "sonar.skipPackageDesign", "true"  // 大規模プロジェクト向け
    }
}
```

## CI/CDパイプライン統合の完全例

### Jenkins Pipeline設定

```groovy
pipeline {
    agent any
    
    environment {
        SONAR_TOKEN = credentials('sonar-token')
        SONAR_HOST_URL = 'https://sonarqube.company.com'
    }
    
    stages {
        stage('Parallel Analysis') {
            parallel {
                stage('Backend') {
                    steps {
                        dir('apps/game-server') {
                            sh './gradlew test jacocoTestReport sonarqube'
                        }
                    }
                }
                stage('Frontend') {
                    steps {
                        dir('apps/web-client') {
                            sh 'npm install && npm run test:coverage'
                            sh 'npx sonar-scanner'
                        }
                    }
                }
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
```

### カバレッジ集約設定

複数モジュールのカバレッジを統合：

```gradle
task codeCoverageReport(type: JacocoReport) {
    executionData fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec")
    
    subprojects.each { subproject ->
        subproject.plugins.withType(JacocoPlugin).configureEach {
            sourceSets subproject.sourceSets.main
        }
    }
    
    reports {
        xml.required = true
        html.required = true
    }
}
```

## 推奨される実装アプローチ

**段階的な導入戦略**として、まずコアモジュールから開始し、徐々に全体に展開することを推奨します。最初にバックエンドのJavaモジュールで設定を確立し、次にフロントエンドのJavaScript/TypeScriptモジュールを追加、最後に共有パッケージとサービスを統合します。

**品質ゲートの設定**では、ゲーム開発特有の要件を考慮し、3Dグラフィックスや物理エンジンのコードには通常より緩い複雑性しきい値を設定します。一方で、セキュリティと信頼性の基準は厳格に維持します。

この包括的なアプローチにより、鉄道シミュレーションゲームのような複雑なモノレポプロジェクトでも、効果的なコード品質管理が実現できます。ファイル重複問題を回避しながら、各モジュールの特性に応じた最適な分析設定を適用することで、開発効率と品質の両立が可能になります。

## 実運用で判明した問題と対策記録

### Gradle Problems Reportによるcontext差異警告問題（2025年7月）

**問題の詳細**：
SonarQubeタスク実行時にGradle Problems Reportで以下の警告が継続的に発生することが判明：

```
Resolution of the configuration :packages:compileClasspath was attempted from a context different than the project context
Resolution of the configuration :packages:testCompileClasspath was attempted from a context different than the project context
Resolution of the configuration :packages:shared-models:testCompileClasspath was attempted from a context different than the project context
Resolution of the configuration :apps:compileClasspath was attempted from a context different than the project context
Resolution of the configuration :apps:testCompileClasspath was attempted from a context different than the project context
```

**技術的原因**：
- SonarQubeプラグインがマルチプロジェクト環境で各サブプロジェクトの依存関係情報を収集する際に、適切でないコンテキストから設定を解決しようとしている
- Gradle 8.13の非推奨動作であり、Gradle 9.0では完全にエラーになる予定
- 根本的にはSonarQubeプラグインのマルチプロジェクト設定解決メカニズムの制約

**検討した解決策と結果**：
1. **中央集権的設定の試行**：ルートプロジェクトでの統合管理を試みたが、ルートプロジェクトとサブプロジェクトのファイル読み込みが干渉し、二重インデックス問題が再発
2. **sonar.modules設定**：Java プロジェクトのみを明示的に指定したが、TypeScript プロジェクトとの連携で問題が発生

**採用した対策**：
**この警告を本プロジェクトでは問題として扱わない方針を決定**

**対策の根拠**：
1. **品質への影響なし**：この警告はSonarQube解析の品質や精度には影響しない
2. **過去の経験**：中央集権的な設定変更により二重インデックス問題が発生した経緯がある
3. **アーキテクチャ原則**：意図的にサブプロジェクトでファイル読み込みを個別設定するアーキテクチャを採用
4. **実用性優先**：警告は表示されるが、実際のSonarQube解析は正常に動作する

**記録日**：2025年7月7日
**対応者**：開発チーム + Claude Code
**今後の方針**：
- Gradle 9.0リリース時に再評価が必要
- SonarQubeプラグインの更新で根本解決の可能性を継続監視
- 品質ゲートへの影響がないことを定期確認

### 推奨される監視項目

実運用では以下の項目を定期的に監視することを推奨：

1. **SonarQube解析の成功率**：警告があっても解析は完了することを確認
2. **品質ゲートの状態**：context警告が品質評価に影響しないことを確認
3. **Gradleバージョン更新時の影響**：新バージョンでの動作確認
4. **SonarQubeプラグイン更新**：新バージョンでの根本解決可能性を確認
