package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generators")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Generator {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id")
    private ProblemVersion version;

    private String name;
    private String sourceCodeKey;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
