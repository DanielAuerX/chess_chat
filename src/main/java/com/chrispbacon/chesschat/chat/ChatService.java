package com.chrispbacon.chesschat.chat;

import com.chrispbacon.chesschat.lichess.LichessService;
import com.chrispbacon.chesschat.monitoring.MonitorService;
import com.chrispbacon.chesschat.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ChatService {

  private static final Logger log = LoggerFactory.getLogger(ChatService.class);
  private final LichessService lichessService;
  private final ActiveUserService activeUserService;
  private final MessageRepository messageRepository;
  private final MonitorService monitorService;

  public ChatService(
      LichessService lichessService,
      ActiveUserService activeUserService,
      MessageRepository messageRepository,
      MonitorService monitorService) {
    this.lichessService = lichessService;
    this.activeUserService = activeUserService;
    this.messageRepository = messageRepository;
    this.monitorService = monitorService;
  }

  public ChatMessage handleSendMessage(final ChatMessage chatMessage) {
    try {
      chatMessage.setTimestamp(LocalDateTime.now());
      messageRepository.save(chatMessage);
      if (lichessService.shouldDisplayStats(chatMessage)) {
        log.info("stats have been requested by {}", chatMessage.getSender());
        return lichessService.requestStats(chatMessage);
      }
      if (lichessService.shouldChallenge(chatMessage)) {
        log.info("a challenge has been requested by {}", chatMessage.getSender());
        return lichessService.challenge(chatMessage);
      }
      return chatMessage;
    } catch (Exception e) {
      if (!monitorService.sendAlert(e)) {
        log.error("Something went wrong while alerting the team.");
      }
    }
    return null;
  }

  public ChatMessage handleAddUser(
      ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    try {
      // add username in web socket session
      headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
      activeUserService.addUser(chatMessage.getSender());
      return chatMessage;
    } catch (Exception e) {
      if (!monitorService.sendAlert(e)) {
        log.error("Something went wrong while alerting the team.");
      }
    }
    return null;
  }
}
