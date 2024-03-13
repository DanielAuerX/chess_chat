package com.chrispbacon.chesschat.chat;

import com.chrispbacon.chesschat.lichess.LichessService;
import com.chrispbacon.chesschat.repository.MessageRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class ChatController {

  private final String COMMAND_PREFIX = "$";

  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final Logger log = LoggerFactory.getLogger(ChatController.class);
  private final LichessService lichessService;
  private final ActiveUserService activeUserService;
  private final MessageRepository messageRepository;

  public ChatController(
      LichessService lichessService,
      ActiveUserService activeUserService,
      MessageRepository messageRepository,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.lichessService = lichessService;
    this.activeUserService = activeUserService;
    this.messageRepository = messageRepository;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
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
    if (chatMessage.getContent().startsWith(COMMAND_PREFIX)){
      return new ChatMessage("unrecognized command: " + chatMessage.getContent() , "Obama Bot", MessageType.CHAT);
    }

    return chatMessage;
  }

  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(
      @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    // add username in web socket session
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    activeUserService.addUser(chatMessage.getSender());
    return chatMessage;
  }

  @MessageMapping("/chat.private.{username}")
  public void filterPrivateMessage(@Payload DirectMessageRequest message, @DestinationVariable("username") String username) {
    log.info("Sending priv message to: " + username);
    message.setType(MessageType.CHAT);
    simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
  }

}
