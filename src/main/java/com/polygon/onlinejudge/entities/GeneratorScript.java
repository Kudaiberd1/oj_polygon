package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "generator_scripts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratorScript {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id")
    private ProblemVersion version;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime updatedAt;
}
