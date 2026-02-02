package com.polygon.onlinejudge.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemVersion {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

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

    @OneToOne
    @JoinColumn(nullable = true)
    private ProblemStatement problemStatement;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
