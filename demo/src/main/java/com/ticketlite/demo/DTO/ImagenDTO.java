package com.ticketlite.demo.DTO;

public class ImagenDTO {
    private Long id;
    private String nombreOriginal;
    private String url; // URL prefirmada

    public ImagenDTO() {}

    public ImagenDTO(Long id, String nombreOriginal, String url) {
        this.id = id;
        this.nombreOriginal = nombreOriginal;
        this.url = url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
