package net.rushhourgame.core.database.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 全エンティティの基底クラス
 * 仕様書：監査フィールド（createdAt, updatedAt, version）を統一的に管理
 */
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * ID（UUID自動生成）
     * 仕様書：@GeneratedValue(strategy = GenerationType.UUID)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private String id;

    /**
     * 作成日時（自動設定）
     * 仕様書：@CreationTimestamp
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時（自動設定）
     * 仕様書：@UpdateTimestamp
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * バージョン（楽観的ロック用）
     * 仕様書：@Version
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // Getter and Setter methods
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}