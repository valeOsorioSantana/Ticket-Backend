package com.ticketlite.demo.config;

import com.ticketlite.demo.model.PoliticaCancelacion;
import com.ticketlite.demo.model.repository.PoliticaCancelacionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PoliticaCancelacionInitializer implements CommandLineRunner {

    private final PoliticaCancelacionRepository politicaRepo;

    public PoliticaCancelacionInitializer(PoliticaCancelacionRepository politicaRepo) {
        this.politicaRepo = politicaRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        // Si ya existe una política, no hacer nada
        if (politicaRepo.count() == 0) {
            PoliticaCancelacion politica = new PoliticaCancelacion();
            politica.setDiasPreviosPermitidos(7); // Ejemplo: se puede cancelar hasta 7 días antes
            politica.setPorcentajeReembolso(0.8); // Ejemplo: 80% de reembolso
            politicaRepo.save(politica);
            System.out.println("Política de cancelación predeterminada creada.");
        }
    }
}