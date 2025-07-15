package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.Imagen;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;
    private final EventsRepository eventsRepository;

    @Autowired
    public ImageController(ImageService imageService, EventsRepository eventsRepository) {
        this.imageService = imageService;
        this.eventsRepository = eventsRepository;
    }

    @PostMapping("/upload/{idEvent}")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @PathVariable Long idEvent) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío o no proporcionado"));
        }
        try {

            Optional<EventsEntity> eventOpt = eventsRepository.findById(idEvent);
            if (eventOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Evento no encontrada"));
            }
            Imagen imagen = imageService.uploadImage(file, eventOpt.get());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "Mesaje", "Imagen guardada correctamente"
            ));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        return imageService.getImage(id)
                .map(imagen -> {
                    String url = null;
                    try {
                        url = imageService.getPresignedUrl(imagen.getId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return ResponseEntity.ok(Map.of(
                            "id", imagen.getId(),
                            "nombre", imagen.getNombreOriginal(),
                            "url", url
                    ));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Imagen no encontrada")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Imagen imagen = imageService.updateImage(id, file);

            return ResponseEntity.ok(Map.of(
                    "Mesaje", "Imagen actualizada correctamente"
            ));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            imageService.deleteImage(id);
            return ResponseEntity.ok(Map.of("mensaje", "Imagen eliminada correctamente"));
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private ResponseEntity<?> handleException(Exception e) {
        if ("Imagen no encontrada".equals(e.getMessage())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
    }
}