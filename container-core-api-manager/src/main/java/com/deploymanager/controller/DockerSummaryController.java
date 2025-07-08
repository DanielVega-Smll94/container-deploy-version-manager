package com.deploymanager.controller;

import com.deploymanager.dto.DockerSummaryDto;
import com.deploymanager.service.DockerSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/servers/{serverId}/summary")
public class DockerSummaryController {

    @Autowired
    private DockerSummaryService summaryService;

    @GetMapping
    public DockerSummaryDto getSummary(
            @PathVariable("serverId") Long servidorId  // <— aquí indicas el binding
    ) {
        return summaryService.getSummary(servidorId);
    }
}