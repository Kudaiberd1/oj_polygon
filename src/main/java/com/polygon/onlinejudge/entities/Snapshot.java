package com.polygon.onlinejudge.entities;

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
@Table(name = "snapshot")
public class Snapshot {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "problem_id", nullable = false)
    private UUID problemId;

    @Column(name = "source_version_id", nullable = false)
    private UUID sourceVersionId;

    @Column(name = "source_version_number", nullable = false)
    private Integer sourceVersionNumber;

    @Column(name = "time_limit_ms")
    private Long timeLimitMs;

    @Column(name = "memory_limit_mb")
    private Long memoryLimitMb;

    @Column(columnDefinition = "TEXT", name = "statement_description")
    private String statementDescription;

    @Column(columnDefinition = "TEXT", name = "statement_input")
    private String statementInput;

    @Column(columnDefinition = "TEXT", name = "statement_output")
    private String statementOutput;

    @Column(columnDefinition = "TEXT", name = "statement_notes")
    private String statementNotes;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_committed")
    private Boolean isCommitted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
