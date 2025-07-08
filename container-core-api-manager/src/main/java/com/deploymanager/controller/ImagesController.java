package com.deploymanager.controller;

import com.deploymanager.dto.DockerImageDto;
import com.deploymanager.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers/{serverId}/images")
public class ImagesController {
    @Autowired
    private ImageService imageService;

    @GetMapping
    public List<DockerImageDto> listImages(@PathVariable Long serverId) {
        return imageService.listImages(serverId);
    }
    //@PathVariable String imageId
    //@DeleteMapping("/{imageId}")
    @DeleteMapping
    public ResponseEntity<Void> removeImage(
            @PathVariable Long serverId,
            @RequestParam(required = false) String imageId,
            @RequestParam(required = false) String imageName
    ) {
        //imageService.removeImage(serverId, imageId);
        imageService.removeImage(serverId, imageId, imageName);
        return ResponseEntity.noContent().build();
    }

    /*

    @PostMapping("/images/pull")
    public ResponseEntity<?> pullImage(
            @RequestParam Long servidorId,
            @RequestParam String imageName
    ) {
        return sshService.pullImage(servidorId, imageName);
    }

     Elimina una imagen remota: docker rmi <imageName>

    @DeleteMapping("/images/{imageName}")
    public ResponseEntity<?> removeImage(
            @PathVariable String imageName,
            @RequestParam Long servidorId
    ) {
        // Simplemente delega y devuelve lo que arme el servicio:
        return sshService.removeImage(servidorId, imageName);
    }
    * */
}
