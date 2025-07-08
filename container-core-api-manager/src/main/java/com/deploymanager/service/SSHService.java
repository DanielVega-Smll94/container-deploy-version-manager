package com.deploymanager.service;

import com.deploymanager.dto.ContainerInfo;
import com.deploymanager.dto.DockerImageDto;
import com.deploymanager.dto.SSHExecRequest;
import com.deploymanager.dto.SSHRequestDto;
import com.deploymanager.mapper.ServidorMapper;
import com.deploymanager.model.Servidor;
import com.deploymanager.repository.ServidorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;


@Service
public class SSHService {

    private final ServidorRepository serverRepository;
    private final ServidorMapper serverMapper;
    private final ObjectMapper mapper;       // <- Jackson ObjectMapper inyectado

    public SSHService(ServidorRepository serverRepository, ServidorMapper serverMapper,
                      ObjectMapper mapper) {
        this.serverRepository = serverRepository;
        this.serverMapper = serverMapper;
        this.mapper = mapper;

    }

    public ResponseEntity<?> execute_old(SSHExecRequest req) {
        // 1) Recuperar servidor
        Optional<Servidor> opt = serverRepository.findById(req.getServidorId());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servidor no encontrado: " + req.getServidorId());
        }
        Servidor srv = opt.get();

        try {
            // 2) Conectar vía JSch
            JSch jsch = new JSch();
            Session session = jsch.getSession(
                    srv.getUsername(), srv.getHost(), srv.getPort()
            );
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(req.getCommand());
            InputStream in = channel.getInputStream();
            channel.connect();

            // 3) Leer la salida
            Scanner sc = new Scanner(in).useDelimiter("\\A");
            String output = sc.hasNext() ? sc.next() : "";

            channel.disconnect();
            session.disconnect();

            // 4) Devolver el resultado
            return ResponseEntity.ok(output);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar SSH: " + ex.getMessage());
        }
    }

    /**
     * Abre un canal SSH que ejecuta `docker logs -f <cid>` y
     * va enviando cada línea como evento SSE al cliente.
     */
    public SseEmitter streamContainerLogs(Long servidorId, String containerId, int tailLines) {
        SseEmitter emitter = new SseEmitter(0L);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Servidor srv = serverRepository.findById(servidorId)
                        .orElseThrow(() -> new IllegalArgumentException("Servidor no encontrado"));

                // Conexión SSH…
                JSch jsch = new JSch();
                Session session = jsch.getSession(srv.getUsername(), srv.getHost(), srv.getPort());
                session.setPassword(srv.getPassword());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                ChannelExec channel = (ChannelExec) session.openChannel("exec");
                // Aquí añadimos la cola de líneas:
                String cmd = String.format("docker logs --tail %d -f %s", tailLines, containerId);
                channel.setCommand(cmd);

                InputStream in = channel.getInputStream();
                channel.connect();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null && channel.isConnected()) {
                        emitter.send(line);
                    }
                }

                channel.disconnect();
                session.disconnect();
                emitter.complete();

            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }
    public SseEmitter streamContainerLogs_old(Long servidorId, String containerId) {
        // Sin timeout (permanece abierto mientras el cliente quiera)
        SseEmitter emitter = new SseEmitter(0L);

        // Ejecutamos en un hilo aparte para no bloquear el servlet thread
        Executors.newSingleThreadExecutor().submit(() -> {
            Optional<Servidor> opt = serverRepository.findById(servidorId);
            if (opt.isEmpty()) {
                emitter.completeWithError(
                        new IllegalArgumentException("Servidor no encontrado: " + servidorId));
                return;
            }
            Servidor srv = opt.get();

            Session session = null;
            ChannelExec channel = null;
            try {
                JSch jsch = new JSch();
                session = jsch.getSession(srv.getUsername(), srv.getHost(), srv.getPort());
                session.setPassword(srv.getPassword());
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect();

                channel = (ChannelExec) session.openChannel("exec");
                channel.setCommand("docker logs -f " + containerId);
                InputStream in = channel.getInputStream();
                channel.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null && channel.isConnected()) {
                    emitter.send(line);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            } finally {
                if (channel != null && channel.isConnected()) channel.disconnect();
                if (session != null && session.isConnected()) session.disconnect();
            }
        });

        return emitter;
    }

    public ResponseEntity<?> execute(SSHExecRequest req) {
        Optional<Servidor> opt = serverRepository.findById(req.getServidorId());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servidor no encontrado: " + req.getServidorId());
        }
        Servidor srv = opt.get();

        Session session = null;
        ChannelExec channel = null;
        try {
            session = new JSch().getSession(srv.getUsername(), srv.getHost(), srv.getPort());
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(req.getCommand());

            // Preparamos buffers para data y message
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ByteArrayOutputStream message = new ByteArrayOutputStream();
            channel.setOutputStream(data);
            channel.setErrStream(message);

            channel.connect();

            // Esperar a que termine
            while (!channel.isClosed()) {
                Thread.sleep(50);
            }

            String out = data.toString(StandardCharsets.UTF_8);
            String err = message.toString(StandardCharsets.UTF_8);

            // Si hay error, devolvemos 500 + contenido de message
            if (!err.isBlank()) {
                Map<String,String> body = Map.of(
                        "data", out.strip(),
                        "message", err.strip()
                );
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(body);
            }

            // Caso OK: devolvemos data en JSON
            Map<String,String> body = Map.of(
                    "data", out.strip()
            );
            return ResponseEntity.ok(body);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("exception", ex.getMessage()));
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }

    /*funcional*/
    public ResponseEntity<?> executeStructured(SSHExecRequest req) {
        Optional<Servidor> opt = serverRepository.findById(req.getServidorId());
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Servidor no encontrado: " + req.getServidorId());
        }
        Servidor srv = opt.get();

        Session session = null;
        ChannelExec channel = null;
        try {
            // 1) Abre la sesión SSH
            session = new JSch().getSession(srv.getUsername(), srv.getHost(), srv.getPort());
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 2) Prepara el comando
            String base = (req.getCommand() == null || req.getCommand().isBlank())
                    ? "docker ps -a"
                    : req.getCommand().trim();
            String fullCmd = String.format(
                    "/bin/sh -lc \"%s --format '{{json .}}'\"",
                    base
            );

            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(fullCmd);

            // 3) Ejecuta y lee todo data
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] rawBytes = in.readAllBytes();
            String rawOut  = new String(rawBytes, StandardCharsets.UTF_8);

            // 4) Parsea sólo líneas JSON válidas
            List<ContainerInfo> containers = new ArrayList<>();
            for (String line : rawOut.split("\\r?\\n")) {
                line = line.trim();
                if (!line.startsWith("{")) {
                    // descarta cualquier línea que no sea JSON
                    continue;
                }
                containers.add(mapper.readValue(line, ContainerInfo.class));
            }

            return ResponseEntity.ok(containers);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ejecutar SSH: " + ex.getMessage());
        } finally {
            // 5) Cierra canal y sesión
            if (channel != null && channel.isConnected()) {
                channel.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    public String execCommand(Long servidorId, String comando) throws Exception {
        // 1) Recupera datos de tu servidor
        Servidor srv = serverRepository.findById(servidorId)
                .orElseThrow(() -> new IllegalArgumentException("Servidor no encontrado: " + servidorId));

        // 2) Construye el DTO manualmente
        SSHRequestDto req = new SSHRequestDto();
        req.setHost(srv.getHost());
        req.setPort(srv.getPort());
        req.setUsername(srv.getUsername());
        req.setPassword(srv.getPassword());
        req.setCommand(comando);

        // 3) Llama a tu método existente
        return executeCommand_request(req);
    }

     /**
     * Orquesta la ejecución SSH a partir de un request con servidorId + comando.
     */
    /*public String executeCommand(SSHExecRequest execReq) throws Exception {

        Servidor srv = serverRepository.findById(execReq.getServidorId())
                .orElseThrow(() -> new IllegalArgumentException("Servidor no encontrado: " + execReq.getServidorId()));

        // 2) Arma el DTO para JSch
        SSHRequestDto dto = SSHRequestDto.builder()
                .host(srv.getHost())
                .port(srv.getPort())
                .username(srv.getUsername())
                .password(srv.getPassword())
                .command(execReq.getCommand())
                .build();

        // 3) Llama al método que hace el SSH real
        return executeCommand_request(dto);
    }*/

    /**
     * Método que se tenía para ejecutar por usuario/contraseña.
     */
    public String executeCommand_request(SSHRequestDto request) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(request.getUsername(), request.getHost(), request.getPort());
        session.setPassword(request.getPassword());  // 👈 autenticación por contraseña

        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(request.getCommand());
        InputStream in = channel.getInputStream();
        channel.connect();

        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";

        channel.disconnect();
        session.disconnect();

        return result;
    }
}
    //para ssh con  id_rsa
    /*public String executeCommand_SSH(SSHRequest request) throws Exception {
        JSch jsch = new JSch();
        jsch.addIdentity(request.getPrivateKeyPath());

        Session session = jsch.getSession(request.getUsername(), request.getHost(), request.getPort());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(request.getCommand());
        InputStream in = channel.getInputStream();
        channel.connect();

        Scanner scanner = new Scanner(in).useDelimiter("\\A");
        String result = scanner.hasNext() ? scanner.next() : "";

        channel.disconnect();
        session.disconnect();

        return result;
    }*/

