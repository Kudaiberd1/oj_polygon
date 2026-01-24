package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class VerificationResult {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(updatable = false, nullable = false)
    private String id;

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