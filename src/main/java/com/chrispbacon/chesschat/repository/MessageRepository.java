package com.chrispbacon.chesschat.repository;

import com.chrispbacon.chesschat.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<ChatMessage, UUID> {

  List<ChatMessage> findMessagesBySender(String sender);
}
