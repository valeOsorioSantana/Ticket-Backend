package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.ReembolsoRespuestaDTO;
import com.ticketlite.demo.DTO.SolicitudCancelacionDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.PoliticaCancelacion;
import com.ticketlite.demo.model.Reembolso;
import com.ticketlite.demo.model.TicketsEntity;
import com.ticketlite.demo.model.repository.PoliticaCancelacionRepository;
import com.ticketlite.demo.model.repository.ReembolsoRepository;
import com.ticketlite.demo.model.repository.TicketsRepository;
import com.ticketlite.demo.service.EmailServiceImple;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RestController
@RequestMapping("/cancelacion")
@Tag(name = "Gestión de Cancelacion y Reembolso", description = "Operaciones para solicitar la cancelación de entradas y gestionar reembolsos")
public class CancelacionController {

    private TicketsRepository ticketRepo;
    private PoliticaCancelacionRepository politicaRepo;
    private ReembolsoRepository reembolsoRepo;
    private EmailServiceImple emailService;

    @Autowired
    public CancelacionController(EmailServiceImple emailService,TicketsRepository ticketRepo, PoliticaCancelacionRepository politicaRepo, ReembolsoRepository reembolsoRepo) {
        this.ticketRepo = ticketRepo;
        this.politicaRepo = politicaRepo;
        this.reembolsoRepo = reembolsoRepo;
        this.emailService = emailService;
    }

    @Operation(
            summary = "Solicitar reembolso de una entrada",
            description = "Permite cancelar una entrada asociada a un evento y registrar la solicitud de reembolso, "
                    + "siempre y cuando se cumplan las políticas de cancelación definidas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reembolso solicitado con éxito",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReembolsoRespuestaDTO.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o no cumple con la política de cancelación"),
            @ApiResponse(responseCode = "404", description = "Entrada no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/{ticketId}/cancelar")
    public ResponseEntity<ReembolsoRespuestaDTO> cancelarEntrada(@Parameter(description = "Datos para procesar la cancelación", required = true)@RequestBody SolicitudCancelacionDTO dto) {

        Optional<TicketsEntity> optTicket = ticketRepo.findById(dto.getTicketsId());

        if (optTicket.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ReembolsoRespuestaDTO(null, 0, "ERROR", "Entrada no encontrada"));
        }

        TicketsEntity ticket = optTicket.get();

        if (ticket.isCancelada()) {
            return ResponseEntity.badRequest()
                    .body(new ReembolsoRespuestaDTO(null, 0, "YA_CANCELADA", "La entrada ya fue cancelada."));
        }

        EventsEntity evento = ticket.getEvent();
        if (evento == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ReembolsoRespuestaDTO(null, 0, "ERROR", "La entrada no tiene evento asociado."));
        }

        PoliticaCancelacion politica = politicaRepo.findAll().stream().findFirst().orElse(null);

        if (politica == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ReembolsoRespuestaDTO(null, 0, "SIN_POLITICA", "El evento no tiene política de cancelación definida."));
        }

        LocalDateTime ahora = LocalDateTime.now();
        long dias = ChronoUnit.DAYS.between(ahora.toLocalDate(), evento.getStartDate().toLocalDate());

        if (dias < politica.getDiasPreviosPermitidos()) {
            return ResponseEntity.badRequest()
                    .body(new ReembolsoRespuestaDTO(null, 0, "RECHAZADA", "No se puede cancelar tan cerca del evento."));
        }

        BigDecimal precio = ticket.getRegistration().getPrice();
        BigDecimal monto = calcularReembolso(precio, politica.getPorcentajeReembolso());

        ticket.setCancelada(true);
        ticketRepo.save(ticket);

        //Enviar correo de cancelación
        String correo = ticket.getRegistration().getUsers().getEmail();
        String nombreUsuario = ticket.getRegistration().getUsers().getName();
        String nombreEvento = evento.getName();

        emailService.sendCancelationEmail(correo, nombreUsuario, nombreEvento);

        Reembolso reembolso = new Reembolso();
        reembolso.setTicket(ticket);
        reembolso.setFechaSolicitud(ahora);
        reembolso.setMontoReembolsado(monto);
        reembolso.setEstado("SOLICITADO");

        reembolsoRepo.save(reembolso);

        ReembolsoRespuestaDTO respuesta = new ReembolsoRespuestaDTO(
                reembolso.getId(), monto.doubleValue(), "SOLICITADO", "Reembolso solicitado con éxito");

        return ResponseEntity.ok(respuesta);
    }

    private BigDecimal calcularReembolso(BigDecimal precio, double porcentaje) {
        return precio.multiply(BigDecimal.valueOf(porcentaje));
    }
}
