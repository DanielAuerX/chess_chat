package com.chrispbacon.chesschat.repository;

import com.chrispbacon.chesschat.chat.ChatMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<ChatMessage, UUID> {

  List<ChatMessage> findMessagesBySender(String sender);
}
