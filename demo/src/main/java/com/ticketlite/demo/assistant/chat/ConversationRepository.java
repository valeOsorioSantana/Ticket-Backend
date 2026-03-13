package com.ticketlite.demo.assistant.chat;

import com.ticketlite.demo.model.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    List<Conversation> findRecent(UsersEntity userId);
}
