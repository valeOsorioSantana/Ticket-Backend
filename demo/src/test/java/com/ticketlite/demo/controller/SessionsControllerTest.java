package com.ticketlite.demo.controller;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.SessionsEntity;
import com.ticketlite.demo.service.SessionsService;
import com.ticketlite.demo.structure.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionsControllerTest {

    @Mock
    private SessionsService sessionsService;

    @InjectMocks
    private SessionsController sessionsController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetByIdFound() {
        // Crear sesión simulada
        SessionsEntity session = new SessionsEntity();
        session.setId(1L);
        session.setTitle("Sesion 1");

        // Definir comportamiento del mock
        when(sessionsService.getById(1L)).thenReturn(session);

        // Llamar al método
        SessionsEntity result = sessionsController.getById(1L);

        // Validar resultados
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sesion 1", result.getTitle());

        // Verificar interacción con el mock
        verify(sessionsService, times(1)).getById(1L);
    }

    @Test
    public void testGetByIdNotFound() {
        // Simular que el servicio devuelve null o lanza excepción si no encuentra
        when(sessionsService.getById(1L)).thenReturn(null);

        SessionsEntity result = sessionsController.getById(1L);

        // Validar que el resultado es null
        assertNull(result);

        verify(sessionsService, times(1)).getById(1L);
    }

    @Test
    public void testGetByIdException() {
        // Simular excepción del servicio
        when(sessionsService.getById(1L)).thenThrow(new RuntimeException("Error"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            sessionsController.getById(1L);
        });

        assertEquals("Error", exception.getMessage());

        verify(sessionsService, times(1)).getById(1L);
    }

    @Test
    public void testSaveSessionSuccess() throws Exception {
        String eventName = "Evento 1";

        // Crear objeto de prueba
        EventsEntity event = new EventsEntity();
        event.setId(1L);
        event.setName(eventName);

        SessionsEntity sessionData = new SessionsEntity();
        sessionData.setTitle("Sesión 1");

        SessionsEntity createdSession = new SessionsEntity();
        createdSession.setId(1L);
        createdSession.setTitle("Sesión 1");
        createdSession.setEvent(event);

        // Configurar comportamiento de mocks
        when(sessionsService.getEventByName(eventName)).thenReturn(event);
        when(sessionsService.createSession(eventName, sessionData)).thenReturn(createdSession);

        // Llamar al método
        ResponseEntity<?> response = sessionsController.saveSession(eventName, sessionData);

        // Validar resultados
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof SessionsEntity);

        SessionsEntity result = (SessionsEntity) response.getBody();
        assertEquals("Sesión 1", result.getTitle());
        assertEquals(event, result.getEvent());

        // Verificar interacción con mocks
        verify(sessionsService, times(1)).getEventByName(eventName);
        verify(sessionsService, times(1)).createSession(eventName, sessionData);
    }

    @Test
    public void testSaveSessionEventNotFound() throws Exception {
        String eventName = "EventoInexistente";
        SessionsEntity sessionData = new SessionsEntity();

        // Simular excepción
        when(sessionsService.getEventByName(eventName)).thenThrow(new NotFoundException("Evento no encontrado"));

        ResponseEntity<?> response = sessionsController.saveSession(eventName, sessionData);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Evento no encontrado", response.getBody());

        verify(sessionsService, times(1)).getEventByName(eventName);
        verify(sessionsService, times(0)).createSession(anyString(), any());
    }

    @Test
    public void testSaveSessionInternalError() throws Exception {
        String eventName = "EventoError";
        SessionsEntity sessionData = new SessionsEntity();
        EventsEntity event = new EventsEntity();

        when(sessionsService.getEventByName(eventName)).thenReturn(event);
        when(sessionsService.createSession(eventName, sessionData)).thenThrow(new RuntimeException("Error inesperado"));

        ResponseEntity<?> response = sessionsController.saveSession(eventName, sessionData);

        assertNotNull(response);
        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Error interno al crear la sesión", response.getBody());

        verify(sessionsService, times(1)).getEventByName(eventName);
        verify(sessionsService, times(1)).createSession(eventName, sessionData);
    }
}
