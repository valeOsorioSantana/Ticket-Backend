package com.ticketlite.demo.controller;

import com.ticketlite.demo.DTO.EventCompleteDTO;
import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.service.EventsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EventsControllerTest {

    @Mock
    private EventsService eventsService;

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
}
