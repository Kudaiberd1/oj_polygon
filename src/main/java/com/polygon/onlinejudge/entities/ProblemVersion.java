package com.polygon.onlinejudge.entities;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemVersion {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    private int version;

    @Enumerated(value = EnumType.STRING)
    private Status status;

    private Long timeLimitMs;
    private Long memoryLimitMb;

    @Enumerated(EnumType.STRING)
    private ContestType scoringType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
