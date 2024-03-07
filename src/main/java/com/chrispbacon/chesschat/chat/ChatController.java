package com.chrispbacon.chesschat.chat;

import com.chrispbacon.chesschat.lichess.LichessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final LichessService lichessService;
    private final ActiveUserService activeUserService;

    public ChatController(LichessService lichessService, ActiveUserService activeUserService) {
        this.lichessService = lichessService;
        this.activeUserService = activeUserService;
    }

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if (lichessService.shouldDisplayStats(chatMessage)){
            log.info("stats have been requested by {}", chatMessage.getSender());
            return lichessService.requestStats(chatMessage);
        }
        if (lichessService.shouldChallenge(chatMessage)){
            log.info("a challenge has been requested by {}", chatMessage.getSender());
            return lichessService.challenge(chatMessage);
        }
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        activeUserService.addUser(chatMessage.getSender());
        return chatMessage;
    }
}
