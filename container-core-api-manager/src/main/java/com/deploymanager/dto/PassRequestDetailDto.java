package com.deploymanager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassRequestDetailDto {
    private Long id;
    private String componentType;
    private String componentName;
    private String fileAffected;
    private String notes;
    private boolean requiresBackup;
    private boolean hasUnitTests;
}
