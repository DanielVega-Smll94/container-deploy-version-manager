package com.deploymanager.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRequestDto {
    private Long id;
    private String title;
    private String description;
    private String environment;
    private String status;
    private String branchName;
    private String commitHash;
    private String projectType;
    private String requester;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PassRequestDetailDto> details;
}
