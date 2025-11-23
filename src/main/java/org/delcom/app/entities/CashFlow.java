package org.delcom.app.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cash_flows")
public class CashFlow {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false) // TAMBAHKAN INI
    private UUID userId;                         // TAMBAHKAN INI

    private String type;
    private String source;
    private String label;
    private Integer amount;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    public CashFlow() {}

    public CashFlow(String type, String source, String label, Integer amount, String description) {
        this.type = type;
        this.source = source;
        this.label = label;
        this.amount = amount;
        this.description = description;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters dan Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    // TAMBAHKAN GETTER & SETTER UNTUK userId
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}