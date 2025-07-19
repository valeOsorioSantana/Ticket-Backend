package com.ticketlite.demo.service;


import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.Imagen;
import com.ticketlite.demo.model.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageStorageService imageStorageService;
    private final ImagenRepository imagenRepository;

    @Autowired
    public ImageService(ImageStorageService imageStorageService, ImagenRepository imagenRepository) {
        this.imageStorageService = imageStorageService;
        this.imagenRepository = imagenRepository;
    }

    public Imagen uploadImage(MultipartFile file, EventsEntity newEvent) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("Archivo de imagen vacío o nulo");
        }

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String key = "imagenes/" + fileName;

        imageStorageService.uploadImage(file, key);

        Imagen imagen = new Imagen();
        imagen.setNombreOriginal(file.getOriginalFilename());
        imagen.setKeyS3(key);
        imagen.setEvents(newEvent);
        return imagenRepository.save(imagen);
    }

    public Optional<Imagen> getImage(Long id) {
        return imagenRepository.findImagenByEventsId(id);
    }

    public String getPresignedUrl(Long id) throws Exception {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new Exception("Imagen no encontrada"));

        return imageStorageService.generatePresignedUrl(imagen.getKeyS3(), Duration.ofMinutes(15));
    }

    public Imagen updateImage(Long id, MultipartFile file) throws IOException {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new IOException("Imagen no encontrada"));

        imageStorageService.deleteImage(imagen.getKeyS3());

        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
        String newKey = "imagenes/" + fileName;

        imageStorageService.uploadImage(file, newKey);

        imagen.setKeyS3(newKey);
        imagen.setNombreOriginal(file.getOriginalFilename());
        return imagenRepository.save(imagen);
    }

    public void deleteImage(Long id) throws IOException {
        Imagen imagen = imagenRepository.findById(id)
                .orElseThrow(() -> new IOException("Imagen no encontrada"));

        imageStorageService.deleteImage(imagen.getKeyS3());

        imagenRepository.deleteById(imagen.getId());
    }
}