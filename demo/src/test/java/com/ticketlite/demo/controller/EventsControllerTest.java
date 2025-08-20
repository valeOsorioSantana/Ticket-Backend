package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.EventCompleteDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.service.EventAnalyticsService;
import com.ticketlite.demo.service.EventsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventsControllerTest {

    @Mock
    private EventsService eventsService;

    @Mock
    private EventAnalyticsService eventAnalyticsService;

    @InjectMocks
    private EventsController eventsController;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void  testGetAllEvents(){
        // Crear datos simulados
        EventCompleteDTO event1 = new EventCompleteDTO();
        event1.setId(1L);
        event1.setName("Evento 1");

        EventCompleteDTO event2 = new EventCompleteDTO();
        event2.setId(2L);
        event2.setName("Evento 2");

        List<EventCompleteDTO> mockEvents = Arrays.asList(event1, event2);

        // Definir comportamiento del mock
        when(eventsService.getAllEvents()).thenReturn(mockEvents);

        // Llamar al método que estamos probando
        List<EventCompleteDTO> result = eventsController.getAllEvents();

        // Validar resultados
        assertEquals(2, result.size());
        assertEquals("Evento 1", result.get(0).getName());

        // Verificar interacción con el mock
        verify(eventsService, times(1)).getAllEvents();
        System.out.println("Resultado: " + result);
    }

    @Test
    public void testGetEventByIdFound() {
        // Crear evento simulado
        EventCompleteDTO mockEvent = new EventCompleteDTO();
        mockEvent.setId(1L);
        mockEvent.setName("Evento de prueba");

        // Definir comportamiento del mock
        when(eventsService.getById(1L)).thenReturn(Optional.of(mockEvent));

        // Llamar al método
        ResponseEntity<?> response = eventsController.getEventById(1L);

        // Validar resultados
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof EventCompleteDTO);
        assertEquals("Evento de prueba", ((EventCompleteDTO) response.getBody()).getName());

        // Verificar interacción con los mocks
        verify(eventsService, times(1)).getById(1L);
        verify(eventAnalyticsService, times(1)).incrementView(1L);
    }

    @Test
    public void testGetEventByIdNotFound() {
        // Evento no encontrado
        when(eventsService.getById(2L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = eventsController.getEventById(2L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(eventsService, times(1)).getById(2L);
        verify(eventAnalyticsService, times(0)).incrementView(anyLong());
    }

    @Test
    public void testGetEventByIdException() {
        // Simular excepción del servicio
        when(eventsService.getById(3L)).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<?> response = eventsController.getEventById(3L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error interno al procesar la solicitud", response.getBody());

        verify(eventsService, times(1)).getById(3L);
        verify(eventAnalyticsService, times(0)).incrementView(anyLong());
    }
}
