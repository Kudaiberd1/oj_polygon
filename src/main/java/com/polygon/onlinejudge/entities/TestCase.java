package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {
    @Id
    @Column(updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "test_group_id", nullable = false)
    private TestGroup group;

    @ManyToOne
    @JoinColumn(name = "problem_version_id", nullable = true)
    private ProblemVersion problemVersion;

    private Long orderId;

    private String inputPath;
    private String outputPath;

    private Boolean isExample;
    private String customOutput;
}