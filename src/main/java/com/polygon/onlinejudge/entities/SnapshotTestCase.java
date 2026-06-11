package com.polygon.onlinejudge.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "snapshot_test_case")
public class SnapshotTestCase {
    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "snapshot_id", nullable = false)
    private UUID snapshotId;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "group_id")
    private UUID groupId;

    @Column(name = "input_s3_key")
    private String inputS3Key;

    @Column(name = "output_s3_key")
    private String outputS3Key;
}
