package com.chrispbacon.chesschat.messages;

import com.chrispbacon.chesschat.chat.ChatMessage;
import com.chrispbacon.chesschat.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MessageService {

  private final MessageRepository messageRepository;
  private static final Logger log = LoggerFactory.getLogger(MessageService.class);

  public MessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @GetMapping("/messages")
  public List<ChatMessage> getMessagesBySender(@RequestParam String name) {
    return messageRepository.findMessagesBySender(name);
  }
}
