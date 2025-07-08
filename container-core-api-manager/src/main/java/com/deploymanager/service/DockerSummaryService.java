package com.deploymanager.service;

import com.deploymanager.dto.DockerSummaryDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DockerSummaryService {
    private final SSHService sshService;

    public DockerSummaryService(SSHService sshService) {
        this.sshService = sshService;
    }
    public DockerSummaryDto getSummary(Long servidorId)  {
        try {
            DockerSummaryDto dto = new DockerSummaryDto();

            String outStacks = sshService.execCommand(servidorId, "docker stack ls -q | wc -l");
            dto.setStacks(parseCount(outStacks));
            dto.setContainers(parseCount(sshService.execCommand(servidorId, "docker ps -a -q | wc -l")));
            dto.setImages(parseCount(sshService.execCommand(servidorId, "docker images -q | sort | uniq | wc -l")));
            dto.setVolumes(parseCount(sshService.execCommand(servidorId, "docker volume ls -q | wc -l")));
            dto.setNetworks(parseCount(sshService.execCommand(servidorId, "docker network ls -q | wc -l")));
            return dto;
        }catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error al obtener summary de Docker: " + ex.getMessage(),
                    ex
            );
        }
    }

    /**
     * Quita espacios y parsea el número, devolviendo 0 si falla
     */
    private int parseCount(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
