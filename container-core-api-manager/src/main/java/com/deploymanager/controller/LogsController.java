package com.deploymanager.controller;

import com.deploymanager.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/logs")
public class LogsController {
    @Autowired
    private LogsService sshService;

    //@GetMapping(value="/stream/{containerName}", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    @GetMapping(value="/stream/{servidorId}/{containerName}", produces= MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs(
            @PathVariable Long servidorId,
            @PathVariable String containerName,
            @RequestParam(defaultValue="200") int tailLines) {
        SseEmitter emitter = new SseEmitter(0L); // 0 = sin timeout
        sshService.streamContainerLogs(servidorId, containerName, tailLines, emitter);
        return emitter;
    }

}
