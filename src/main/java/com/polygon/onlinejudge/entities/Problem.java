package com.polygon.onlinejudge.entities;

import com.polygon.onlinejudge.entities.enums.ContestType;
import com.polygon.onlinejudge.entities.enums.Language;
import com.polygon.onlinejudge.entities.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "problem")
@Builder
public class Problem {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(updatable = false, nullable = false)
    private String id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "input_description")
    private String inputDescription;

    @Column(name = "output_description")
    private String outputDescription;

    @Column(name = "time_limit_ms")
    private Long timeLimitMs;

    @Column(name = "memory_limit_mb")
    private Long memoryLimitMb;

    @Column(name = "allowed_languages")
    @Enumerated(EnumType.STRING)
    private Language allowedLanguages;

    @Column(name = "scoring_type")
    @Enumerated(EnumType.STRING)
    private ContestType scoringType;

    @Column(name = "owner_id")
    private UUID ownerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
