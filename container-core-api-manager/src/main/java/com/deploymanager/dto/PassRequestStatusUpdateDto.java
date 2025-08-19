package com.deploymanager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassRequestStatusUpdateDto {
    private Long id;
    private String newStatus; // QA_APPROVED, LEAD_APPROVED, DEPLOYED, REJECTED, CANCELLED
}