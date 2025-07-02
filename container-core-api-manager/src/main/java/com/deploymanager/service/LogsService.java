package com.deploymanager.service;

import com.deploymanager.mapper.ServidorMapper;
import com.deploymanager.model.Servidor;
import com.deploymanager.repository.ServidorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class LogsService {
    private final ServidorRepository serverRepository;

    public LogsService(ServidorRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    public void streamContainerLogs(Long servidorId,
                                    String containerName,
                                    int tailLines,
                                    SseEmitter emitter) {
        new Thread(() -> {
            Session session = null;
            ChannelExec channel = null;
            try {
                // 1) Recuperar por ID
                Servidor srv = serverRepository.findById(servidorId)
                        .orElseThrow(() -> new IllegalArgumentException("Servidor no encontrado: " + servidorId));

                // 2) Conexión SSH
                JSch jsch = new JSch();
                session = jsch.getSession(srv.getUsername(), srv.getHost(), srv.getPort());
                session.setPassword(srv.getPassword());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                // 3) Comando de logs
                String cmd = String.format("docker logs --follow --tail %d %s", tailLines, containerName);
                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand(cmd);
                InputStream in = channel.getInputStream();
                channel.connect();

                // 4) Leer y enviar por SSE
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        emitter.send(SseEmitter.event().data(line));
                    }
                }

            } catch (Exception e) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("error")
                            .data("Error en stream de logs: " + e.getMessage()));
                } catch (IOException ignored) {}
            } finally {
                // 4) Cierra recursos
                if (channel != null && channel.isConnected()) channel.disconnect();
                if (session != null && session.isConnected()) session.disconnect();
                emitter.complete();
            }
        }).start();
    }
}

