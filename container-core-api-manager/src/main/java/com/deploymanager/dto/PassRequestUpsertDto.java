package com.deploymanager.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRequestUpsertDto {
    private Long id; // usado solo para update

    private String title;
    private String description;
    private String environment;
    private String status;
    private String branchName;
    private String commitHash;
    private String projectType;
    private String requester;
    private List<PassRequestDetailDto> details;
}
