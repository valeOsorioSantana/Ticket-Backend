package com.ticketlite.demo.assistant.rag;

import com.ticketlite.demo.assistant.chat.Conversation;
import com.ticketlite.demo.assistant.chat.ConversationRepository;
import com.ticketlite.demo.assistant.vector.PgVectorUtils;
import com.ticketlite.demo.model.UsersEntity;
import com.ticketlite.demo.model.repository.UsersRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.postgresql.util.PGobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;
    private final JdbcTemplate jdbc;
    private final ConversationRepository conversations;
    private final UsersRepository usersRepository;

    @Autowired
    public RagService(UsersRepository usersRepository, EmbeddingModel embeddingModel, ChatClient chatClient, JdbcTemplate jdbc, ConversationRepository conversations) {
        this.embeddingModel = embeddingModel;
        this.chatClient = chatClient;
        this.jdbc = jdbc;
        this.conversations = conversations;
        this.usersRepository = usersRepository;
    }

    public void generateEmbeddingsForTable(String tableName, String textColumn) {
        List<Map<String, Object>> rows = jdbc.queryForList(
                "SELECT id, " + textColumn + " FROM " + tableName + " WHERE embedding IS NULL"
        );

        for (Map<String, Object> row : rows) {
            Integer id = (Integer) row.get("id");
            String text = (String) row.get(textColumn);

            if (text == null || text.isEmpty()) continue;

            EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(text));
            if (embeddingResponse.getResults().isEmpty()) continue;

            Object output = embeddingResponse.getResults().get(0).getOutput();
            List<Double> vector = new ArrayList<>();
            if (output instanceof float[] floats) {
                for (float f : floats) vector.add((double) f);
            } else if (output instanceof List<?> list) {
                vector = list.stream().map(o -> ((Number) o).doubleValue()).collect(Collectors.toList());
            } else {
                throw new IllegalStateException("Tipo de embedding no soportado: " + output.getClass());
            }

            String vectorLiteral = PgVectorUtils.toPgVectorLiteral(vector)
                    .replace("(", "[")
                    .replace(")", "]");

            PGobject pgVector = new PGobject();
            try {
                pgVector.setType("vector");
                pgVector.setValue(vectorLiteral);
            } catch (Exception e) {
                throw new RuntimeException("Error creando PGobject para vector", e);
            }

            jdbc.update("UPDATE " + tableName + " SET embedding = ? WHERE id = ?", pgVector, id);
        }

        System.out.println("Embeddings de " + tableName + " generados correctamente.");
    }

    public String answer(UsersEntity user, String question, String intent) {
        // 0. Buscar primero en FAQs
        String faq = findFaq(question);
        if (faq != null) {
            conversations.save(new Conversation(user, question, faq));
            return faq;
        }

        // 1. Tickets
        if ("tickets".equals(intent)) {
            String ticketAnswer = getTickets(user, question);
            if (ticketAnswer != null) {
                return ticketAnswer; // pregunta de mostrar tickets
            }
        }

        // 2. Registros
        if ("mostrar_registros".equals(intent)) {
            return getRegistros(user, question);
        }

        // 3. Lo demás → Embeddings + RAG
        String ragAnswer = answerWithRag(user, question, intent);

        // Guardar pregunta como "no respondida" si el RAG no ayuda
        jdbc.update("INSERT INTO questions (user_id, question, tipo, processed) VALUES (?, ?, 'no_respondida', false)",
                user.getId(), question);

        return ragAnswer;
    }

    //Busca si la pregunta ya está en las FAQs
    private String findFaq(String question) {
        String sqlFaq = "SELECT answer FROM questions WHERE tipo = 'faq' AND question ILIKE ? LIMIT 1";
        List<String> answers = jdbc.queryForList(sqlFaq, new Object[]{"%" + question + "%"}, String.class);
        return answers.isEmpty() ? null : answers.get(0);
    }

    //Consulta tickets del usuario
    private String getTickets(UsersEntity user, String question) {
        String q = question.toLowerCase();

        if (!(q.contains("mostrar") || q.contains("ver") || q.contains("listar")) || !q.contains("ticket")) {
            return null;
        }

        String sql = """
            SELECT t.id, t.qr_code, r.ticket_type, e.name AS event_name, e.created_at
            FROM tickets t
            JOIN registrations r ON r.id = t.registration_id
            JOIN events e ON e.id = r.events_id
            WHERE r.users_id = ?
            ORDER BY e.created_at DESC
        """;

        List<Map<String, Object>> tickets = jdbc.queryForList(sql, user.getId());

        if (tickets.isEmpty()) {
            return "No encontré tickets registrados para tu cuenta.";
        }

        StringBuilder sb = new StringBuilder("Aquí están tus tickets:\n\n");
        for (Map<String, Object> t : tickets) {
            sb.append("🎟️ Ticket #").append(t.get("id")).append("\n")
                    .append("Evento: ").append(t.get("event_name")).append("\n")
                    .append("Tipo: ").append(t.get("ticket_type")).append("\n")
                    .append("Código QR: ").append(t.get("qr_code")).append("\n")
                    .append("Fecha: ").append(t.get("created_at")).append("\n\n");
        }

        conversations.save(new Conversation(user, question, sb.toString()));
        return sb.toString();
    }

    //Consulta registros del usuario
    private String getRegistros(UsersEntity user, String question) {
        String sql = """
            SELECT r.id, r.ticket_type, e.created_at, e.name AS event_name
            FROM registrations r
            JOIN events e ON e.id = r.events_id
            WHERE r.users_id = ?
            ORDER BY e.created_at DESC
        """;

        List<Map<String, Object>> regs = jdbc.queryForList(sql, user.getId());

        if (regs.isEmpty()) {
            return "No encontré registros asociados a tu cuenta.";
        }

        StringBuilder sb = new StringBuilder("Aquí están tus registros:\n\n");
        for (Map<String, Object> r : regs) {
            sb.append("📝 Registro #").append(r.get("id")).append("\n")
                    .append("Evento: ").append(r.get("event_name")).append("\n")
                    .append("Tipo: ").append(r.get("ticket_type")).append("\n")
                    .append("Fecha: ").append(r.get("created_at")).append("\n\n");
        }

        conversations.save(new Conversation(user, question, sb.toString()));
        return sb.toString();
    }

    //RAG con embeddings y contexto
    private String answerWithRag(UsersEntity user, String question, String intent) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(List.of(question));

        if (embeddingResponse.getResults().isEmpty()) {
            throw new IllegalStateException("No se generó ningún embedding para la pregunta");
        }

        Object output = embeddingResponse.getResults().get(0).getOutput();
        List<Double> vector;
        if (output instanceof float[] floats) {
            vector = new ArrayList<>();
            for (float f : floats) vector.add((double) f);
        } else if (output instanceof List<?> list) {
            vector = list.stream().map(o -> ((Number) o).doubleValue()).collect(Collectors.toList());
        } else {
            throw new IllegalStateException("Tipo de embedding no soportado: " + output.getClass());
        }

        String v = PgVectorUtils.toPgVectorLiteral(vector);
        PGobject pgVector = new PGobject();
        try {
            pgVector.setType("vector");
            pgVector.setValue(v.replace("(", "[").replace(")", "]"));
        } catch (Exception e) {
            throw new RuntimeException("Error creando PGobject para vector", e);
        }

        String sql;
        Object[] params;
        switch (intent) {
            case "eventos":
                sql = "SELECT name AS texto, description, embedding FROM events ORDER BY embedding <=> ? LIMIT 5";
                params = new Object[]{pgVector};
                break;
            default:
                sql = "SELECT name AS texto, description, embedding FROM events ORDER BY embedding <=> ? LIMIT 5";
                params = new Object[]{pgVector};
                break;
        }

        List<String> docs = jdbc.query(sql, params, (rs, rowNum) -> {
            String texto = rs.getString("texto");
            String desc = "";
            try {
                desc = rs.getString("description");
            } catch (Exception ignored) {}
            return texto + (desc.isEmpty() ? "" : "\n" + desc);
        });

        String context = String.join("\n\n---\n\n", docs);

        List<Conversation> history = conversations.findRecent(user).stream()
                .limit(6)
                .collect(Collectors.toList());

        String historyBlock = history.stream()
                .map(h -> "Usuario: " + h.getUserMessage() + "\nAsistente: " + h.getBotMessage())
                .collect(Collectors.joining("\n\n"));

        String promptText = """
                Eres un asistente de soporte para una plataforma de eventos.
                SOLO usa el CONTEXTO recuperado y, si aplica, el HISTORIAL para responder.
                Si no encuentras respuesta en el contexto, di honestamente que no tienes información específica y ofrece opciones dentro del dominio (%s).
                
                === HISTORIAL (opcional) ===
                %s
                
                === CONTEXTO ===
                %s
                
                === PREGUNTA ===
                %s
                
                Responde en español, claro y con pasos concretos si es necesario.
                """.formatted(intent, historyBlock, context, question);

        String answer = chatClient.prompt().user(promptText).call().content();

        conversations.save(new Conversation(user, question, answer));
        return answer;
    }

     //Agregar nuevas FAQs al asistente
    public void addFaq(String question, String answer) {
        jdbc.update("INSERT INTO questions (question, answer, tipo, processed) VALUES (?, ?, 'faq', TRUE)",
                question, answer);
    }
}
