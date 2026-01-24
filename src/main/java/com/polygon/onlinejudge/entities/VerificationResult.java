package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class VerificationResult {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    private ProblemVersion version;

    private boolean passed;
    private String log;
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        this.verifiedAt = LocalDateTime.now();
    }
}