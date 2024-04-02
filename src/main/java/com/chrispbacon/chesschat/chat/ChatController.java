package com.chrispbacon.chesschat.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/home")
public class ChatController {

  private final ChatService chatService;
  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final Logger log = LoggerFactory.getLogger(ChatController.class);


  public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
    this.chatService = chatService;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @MessageMapping("/chat.sendMessage")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    return chatService.handleSendMessage(chatMessage);
  }

  @MessageMapping("/chat.addUser")
  @SendTo("/topic/public")
  public ChatMessage addUser(
      @Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    return chatService.handleAddUser(chatMessage, headerAccessor);
  }

  @MessageMapping("/chat.private.{username}")
  public void filterPrivateMessage(@Payload DirectMessageRequest message, @DestinationVariable("username") String username) {
    log.info("Sending priv message to: " + username);
    message.setType(MessageType.CHAT);
    simpMessagingTemplate.convertAndSend("/user/" + username + "/exchange/amq.direct/chat.message", message);
  }

}
