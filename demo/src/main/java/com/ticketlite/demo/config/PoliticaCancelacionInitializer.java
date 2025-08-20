package com.ticketlite.demo.config;

import com.ticketlite.demo.model.EventsEntity;
import com.ticketlite.demo.model.PoliticaCancelacion;
import com.ticketlite.demo.model.repository.EventsRepository;
import com.ticketlite.demo.model.repository.PoliticaCancelacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PoliticaCancelacionInitializer implements CommandLineRunner {

    private final PoliticaCancelacionRepository politicaRepo;
    private final EventsRepository eventsRepository;

    public PoliticaCancelacionInitializer(EventsRepository eventsRepository,PoliticaCancelacionRepository politicaRepo) {
        this.politicaRepo = politicaRepo;
        this.eventsRepository = eventsRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Si ya existe una política, no hacer nada
        if (politicaRepo.count() == 0) {
            Long idEvento = 1L;
            Optional<EventsEntity> opt = eventsRepository.findById(idEvento);

            if (opt.isEmpty()) {
                System.out.println("No existe evento con ID="+idEvento+" (la BD está vacía). Se omite creación de política.");
                return; // <-- salimos y permitimos que la app siga arrancando
            }

            EventsEntity evento = opt.get();

            PoliticaCancelacion politica = new PoliticaCancelacion();
            politica.setDiasPreviosPermitidos(7); // Ejemplo: se puede cancelar hasta 7 días antes
            politica.setPorcentajeReembolso(0.8); // Ejemplo: 80% de reembolso
            politica.setEvent(evento);
            politicaRepo.save(politica);
            System.out.println("Política de cancelación predeterminada creada.");
        }
    }
}