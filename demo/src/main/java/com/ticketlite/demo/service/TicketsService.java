package com.ticketlite.demo.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ticketlite.demo.model.RegistrationsEntity;
import com.ticketlite.demo.model.TicketsEntity;
import com.ticketlite.demo.model.repository.RegistrationsRepository;
import com.ticketlite.demo.model.repository.TicketsRepository;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class TicketsService {

    //Atributos

    private TicketsRepository ticketsRepository;
    private RegistrationsRepository registrationsRepository;
    private EmailServiceImple emailServiceImple;

    //Importante para conectar el repository
    @Autowired
    public TicketsService(TicketsRepository ticketsRepository, RegistrationsRepository registrationsRepository, EmailServiceImple emailServiceImple) {
        this.ticketsRepository = ticketsRepository;
        this.registrationsRepository = registrationsRepository;
        this.emailServiceImple = emailServiceImple;
    }

    //Metodos
    //GET ALL
    public List<TicketsEntity> getAllTickets() {
        return ticketsRepository.findAll();
    }

    //GET BY ID
    //obtener ticket por ID
    public TicketsEntity getTicketById(Long ticketId) {
        return ticketsRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket no encontrado"));
    }
    //obtener ticket por ID de registro
    public TicketsEntity getTicketByRegistration(Long registrationId) {
        return ticketsRepository.findByRegistrationId(registrationId).orElseThrow(() -> new NotFoundException("Ticket no encontrado para el registro con ID: " + registrationId));
    }

    //POST
    //crear un ticket
    public TicketsEntity createTicket(Long registrationId) {
        try {
            RegistrationsEntity registration = registrationsRepository.findById(registrationId).orElseThrow(() -> new RuntimeException("Registro no encontrado"));

            TicketsEntity ticket = new TicketsEntity();

            ticket.setRegistration(registration);
            ticket.setEvent(registration.getEvents());
            String contenido = """
                TICKET ID: %s EVENTO: %s FECHA: %s USUARIO: %s TIPO: %s PRECIO: %s
        """.formatted(ticket.getId(),registration.getEvents().getName(),registration.getEvents().getStartDate(),registration.getUsers().getEmail(),registration.getTicketType(),registration.getPrice()
            );
            byte[] imagenQR = generarQrComoBytes(contenido);
            String base64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(imagenQR);
            ticket.setQrCode(base64);
            ticket.setCancelada(false);

            ticketsRepository.save(ticket);

            //Enviar el correo
            String email = registration.getUsers().getEmail();
            String mensaje = "Gracias por tu compra. Aquí tienes tu entrada adjunta con código QR.";
            emailServiceImple.enviarTicketConQR(email, "Tu entrada para " + registration.getEvents().getName(), mensaje, imagenQR);


            return ticket;

        } catch (NotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException("Error al crear el ticket: " + e.getMessage(), e);
        }
    }

    //DELETE
    public void deleteTicket (Long ticketId){
        if (ticketsRepository.existsById(ticketId)){
            ticketsRepository.deleteById(ticketId);
        }else {
            throw new RuntimeException("Ticket no encontrado por ID: " + ticketId);
        }
    }

    //Generar QR como imagen
    public byte[] generarQrComoBytes(String contenido) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix matrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, 300, 300);

            BufferedImage imagen = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    int color = matrix.get(x, y) ? 0x000000 : 0xFFFFFF;
                    imagen.setRGB(x, y, color);
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(imagen, "png", baos);
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar QR: " + e.getMessage());
        }
    }


}
