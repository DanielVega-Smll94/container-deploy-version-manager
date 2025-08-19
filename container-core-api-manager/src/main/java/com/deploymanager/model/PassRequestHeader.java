package com.deploymanager.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pass_request_headers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRequestHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String environment;     // DEV, QA, PROD
    private String status;          // PENDING, APPROVED, REJECTED, DEPLOYED
    private String branchName;
    private String commitHash;
    private String projectType;     // MICROSERVICE, MONOLITH, etc.
    private String requester;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "passRequestHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PassRequestDetail> details;
}
