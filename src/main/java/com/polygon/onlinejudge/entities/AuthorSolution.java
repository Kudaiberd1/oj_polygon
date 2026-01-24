package com.polygon.onlinejudge.entities;

import com.polygon.onlinejudge.entities.enums.Language;
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
class AuthorSolution {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(name = "problem_version_id", nullable = false)
    private ProblemVersion version;

    private Language language;

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String sourceCode;
}
