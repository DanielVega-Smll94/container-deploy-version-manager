package com.deploymanager.controller;

import com.deploymanager.dto.SSHExecRequest;
//import com.deploymanager.dto.SSHRequestDto;
//import com.deploymanager.dto.ServidorDto;
import com.deploymanager.service.SSHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/ssh")
public class SSHController {
    @Autowired
    private SSHService sshService;

    /**
     * Ejemplo: POST /api/ssh/execute
     * Body:
     * {
     *   "servidorId": 5,
     *   "command":   "docker ps -a"
     * }
     */
    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody SSHExecRequest req) {
        return sshService.execute(req);
    }

    /**
     * JSON estructurado: lista de ContainerInfo
     * POST /api/ssh/execute/json
     */
    @PostMapping("/execute/json")
    public ResponseEntity<?> executeJson(@RequestBody SSHExecRequest req) {
        return sshService.executeStructured(req);
    }

    // — start contenedor —
    @PostMapping("/containers/{cid}/start")
    public ResponseEntity<?> startContainer(@PathVariable String cid,
                                            @RequestParam Long servidorId) {
        SSHExecRequest req = new SSHExecRequest(servidorId, "docker start " + cid);
        return sshService.execute(req);
    }

    // — Detener un contenedor —
    @PostMapping("/containers/{cid}/stop")
    public ResponseEntity<?> stopContainer(@PathVariable String cid,
                                           @RequestParam Long servidorId
    ) {
        SSHExecRequest req = new SSHExecRequest(servidorId, "docker stop " + cid);
        return sshService.execute(req);
    }

    // — Reiniciar un contenedor —
    @PostMapping("/containers/{cid}/restart")
    public ResponseEntity<?> restartContainer(@PathVariable String cid,
                                              @RequestParam Long servidorId)
    {
        SSHExecRequest req = new SSHExecRequest(servidorId, "docker restart " + cid);
        return sshService.execute(req);
    }

    // — Eliminar un contenedor (optionally forzar) —
    @DeleteMapping("/containers/{cid}")
    public ResponseEntity<?> removeContainer(@PathVariable String cid,
                                             @RequestParam Long servidorId,
                                             @RequestParam(defaultValue="false") boolean force)
    {
        String cmd = "docker rm " + (force ? "-f " : "") + cid;
        SSHExecRequest req = new SSHExecRequest(servidorId, cmd);
        return sshService.execute(req);
    }

    // — Ver logs de un contenedor (tail 100 por defecto) —
    @GetMapping("/containers/{cid}/logs")
    public ResponseEntity<?> logs(@PathVariable String cid,
                                  @RequestParam Long servidorId,
                                  @RequestParam(defaultValue="100") int tail) {
        String cmd = String.format("docker logs --tail %d %s", tail, cid);
        SSHExecRequest req = new SSHExecRequest(servidorId, cmd);
        return sshService.execute(req);
    }

    @GetMapping(
            path = "/containers/{cid}/logs/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamLogs(
            @PathVariable("cid") String containerId,
            @RequestParam("servidorId") Long servidorId,
            @RequestParam(name="tail",    defaultValue="200") int  tailLines
    ) {
        // delegamos totalmente al servicio:
        return sshService.streamContainerLogs(servidorId, containerId, tailLines);
    }

}
