package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class TestCase {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "test_group_id", nullable = false)
    private TestGroup group;

    private String inputPath;
    private String outputPath;
}