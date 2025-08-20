package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.TicketsEntity;
import com.ticketlite.demo.service.TicketsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketsControllerTest {

    @Mock
    private TicketsService ticketsService;

    @InjectMocks
    private TicketsController ticketsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTickets() {
        // Crear tickets simulados
        TicketsEntity ticket1 = new TicketsEntity();
        ticket1.setId(1L);
        ticket1.setQrCode("QR123");

        TicketsEntity ticket2 = new TicketsEntity();
        ticket2.setId(2L);
        ticket2.setQrCode("QR456");

        List<TicketsEntity> mockTickets = Arrays.asList(ticket1, ticket2);

        // Definir comportamiento del mock
        when(ticketsService.getAllTickets()).thenReturn(mockTickets);

        // Llamar al método que estamos probando
        List<TicketsEntity> result = ticketsController.getAllTickets();

        // Validar resultados
        assertEquals(2, result.size());
        assertEquals("QR123", result.get(0).getQrCode());
        assertEquals("QR456", result.get(1).getQrCode());

        // Verificar interacción con el mock
        verify(ticketsService, times(1)).getAllTickets();
    }

    @Test
    public void testGetTicketByIdFound() throws Exception {
        // Crear ticket simulado
        TicketsEntity ticket = new TicketsEntity();
        ticket.setId(1L);
        ticket.setQrCode("QR123");

        // Definir comportamiento del mock
        when(ticketsService.getTicketById(1L)).thenReturn(ticket);

        // Llamar al método
        ResponseEntity<?> response = ticketsController.getTicketById(1L);

        // Validar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ticket, response.getBody());

        // Verificar interacción con el mock
        verify(ticketsService, times(1)).getTicketById(1L);
    }

    @Test
    public void testGetTicketByIdException() throws Exception {
        // Simular excepción del servicio
        when(ticketsService.getTicketById(1L)).thenThrow(new RuntimeException("Error"));

        // Llamar al método
        ResponseEntity<?> response = ticketsController.getTicketById(1L);

        // Validar que devuelve 500
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno al procesar la solicitud", response.getBody());

        verify(ticketsService, times(1)).getTicketById(1L);
    }
}
