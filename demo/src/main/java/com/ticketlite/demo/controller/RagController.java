package com.ticketlite.demo.controller;

import com.ticketlite.demo.assistant.rag.RagService;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ask")
public class RagController {

    private final RagService ragService;
    private final UsersRepository userRepository;

    @Autowired
    public RagController(RagService ragService, UsersRepository userRepository) {
        this.ragService = ragService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public AnswerResponse ask(@RequestBody AskRequest request) {
        UsersEntity user = userRepository.findById(Long.parseLong(request.userId()))
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String answer = ragService.answer(user, request.question(), request.intent());
        return new AnswerResponse(answer);
    }

    //Generar embeddings de cualquier tabla
    @PostMapping("/embeddings")
    public String generateEmbeddings(@RequestBody EmbeddingRequest request) {
        ragService.generateEmbeddingsForTable(request.tableName(), request.textColumn());
        return "Embeddings generados para tabla " + request.tableName() + " en columna " + request.textColumn();
    }

    @PostMapping("/train")
    public String train(@RequestBody TrainRequest request) {
        ragService.addFaq(request.question(), request.answer());
        ragService.generateEmbeddingsForTable("questions", "question");
        return "FAQ agregada y entrenada correctamente.";
    }

    // DTOs
    public record AskRequest(String userId, String question, String intent) {}
    public record AnswerResponse(String answer) {}
    public record TrainRequest(String question, String answer) {}
    public record EmbeddingRequest(String tableName, String textColumn) {}
}
