package com.deploymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DockerSummaryDto {
    private int stacks;
    private int containers;
    private int images;
    private int volumes;
    private int networks;
}
