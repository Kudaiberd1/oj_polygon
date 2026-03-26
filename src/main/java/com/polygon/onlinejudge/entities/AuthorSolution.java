package com.polygon.onlinejudge.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.polygon.onlinejudge.entities.enums.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorSolution {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JsonBackReference
    @JoinColumn(name = "problem_version_id", nullable = false)
    private ProblemVersion version;

    private Language language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String sourceCode;
}
