package com.deploymanager.service;

import com.deploymanager.dto.DockerImageDto;
import com.deploymanager.model.Servidor;
import com.deploymanager.repository.ServidorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {
    @Autowired
    private final ServidorRepository serverRepository;
    @Autowired
    private final SSHService sshService;

    @Autowired
    private final ObjectMapper mapper;  // Jackson

    public ImageService(ServidorRepository serverRepository, SSHService sshService, ObjectMapper mapper) {
        this.serverRepository = serverRepository;
        this.sshService = sshService;
        this.mapper = mapper;
    }

    /**images*/
    /**
     * Ejecuta 'docker rmi <imageName>' por SSH y construye el ResponseEntity.
     */

    public ResponseEntity<?> removeImage(
            Long servidorId,
            String imageId,
            String imageName
    ) {
        // 1) Recupera el servidor
        Servidor srv = serverRepository.findById(servidorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Servidor no encontrado: " + servidorId
                ));

        // 2) Decide qué referencia usar: prioriza imageId, luego imageName
        String ref;
        if (imageId != null && !imageId.isBlank()) {
            ref = imageId;
        } else if (imageName != null && !imageName.isBlank()) {
            ref = imageName;
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debes indicar imageId o imageName como parámetro"
            );
        }

        Session session = null;
        ChannelExec channel = null;
        try {
            // 3) Conectar por SSH
            session = new JSch()
                    .getSession(srv.getUsername(), srv.getHost(), srv.getPort());
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 4) Ejecutar comando docker rmi sobre la referencia escogida
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("docker rmi " + ref);

            // 5) Capturar stdout y stderr
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            channel.setOutputStream(stdout);
            channel.setErrStream(stderr);

            channel.connect();
            while (!channel.isClosed()) {
                Thread.sleep(50);
            }

            String out = stdout.toString(StandardCharsets.UTF_8).strip();
            String err = stderr.toString(StandardCharsets.UTF_8).strip();

            // 6) Armar la respuesta
            Map<String, String> body = new HashMap<>();
            body.put("imageRef", ref);
            body.put("stdout", out);
            if (!err.isEmpty()) {
                body.put("stderr", err);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(body);
            }

            return ResponseEntity.ok(body);

        } catch (JSchException | InterruptedException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al conectarse por SSH: " + ex.getMessage(),
                    ex
            );
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }

    public ResponseEntity<?> removeImage_(Long servidorId, String imageName) {
        Servidor srv = serverRepository.findById(servidorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Servidor no encontrado: " + servidorId
                ));

        Session session = null;
        ChannelExec channel = null;
        try {
            // 1) Conectar por SSH
            session = new JSch()
                    .getSession(srv.getUsername(), srv.getHost(), srv.getPort());
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 2) Comando docker rmi
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("docker rmi " + imageName);

            // 3) Capturar stdout y stderr
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            channel.setOutputStream(stdout);
            channel.setErrStream(stderr);

            channel.connect();
            while (!channel.isClosed()) {
                Thread.sleep(50);
            }

            String out = stdout.toString(StandardCharsets.UTF_8).strip();
            String err = stderr.toString(StandardCharsets.UTF_8).strip();

            // 4) Preparar body
            Map<String, String> body = new HashMap<>();
            body.put("image", imageName);
            body.put("stdout", out);
            if (!err.isEmpty()) {
                body.put("stderr", err);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(body);
            }

            return ResponseEntity.ok(body);

        } catch (JSchException | InterruptedException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al conectarse por SSH: " + e.getMessage(), e
            );
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }

    /**
     * Hace 'docker pull <imageName>' por SSH y arma el ResponseEntity.
     */
    public List<DockerImageDto> listImages(Long servidorId) {
        try {
            String out = sshService.execCommand(
                    servidorId,
                    "docker images --format '{{json .}}'"
            );
            List<DockerImageDto> images = new ArrayList<>();
            for (String line : out.split("\\r?\\n")) {
                if (line.trim().isEmpty()) continue;
                // mapea JSON a DTO
                DockerImageDto dto = mapper.readValue(line, DockerImageDto.class);
                images.add(dto);
            }
            return images;
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "No pudo listar imágenes: " + ex.getMessage(), ex
            );
        }
    }

    public ResponseEntity<?> pullImage(Long servidorId, String imageName) {
        Servidor srv = serverRepository.findById(servidorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Servidor no encontrado: " + servidorId
                ));

        Session session = null;
        ChannelExec channel = null;
        try {
            // 1) Conexión SSH
            session = new JSch()
                    .getSession(srv.getUsername(), srv.getHost(), srv.getPort());
            session.setPassword(srv.getPassword());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // 2) Comando docker pull
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand("docker pull " + imageName);

            // 3) Salida
            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            channel.setOutputStream(stdout);
            channel.setErrStream(stderr);

            channel.connect();

            // Esperamos a que termine
            while (!channel.isClosed()) {
                Thread.sleep(50);
            }

            String out = stdout.toString(StandardCharsets.UTF_8).strip();
            String err = stderr.toString(StandardCharsets.UTF_8).strip();

            // 4) Armamos el body
            Map<String, String> body = new HashMap<>();
            body.put("image", imageName);
            body.put("stdout", out);
            if (!err.isEmpty()) {
                body.put("stderr", err);
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(body);
            }

            return ResponseEntity
                    .ok(body);

        } catch (JSchException | InterruptedException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al conectarse por SSH: " + e.getMessage(), e
            );
        } finally {
            if (channel != null && channel.isConnected()) channel.disconnect();
            if (session != null && session.isConnected()) session.disconnect();
        }
    }
}
