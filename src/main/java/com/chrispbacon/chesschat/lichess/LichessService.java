package com.chrispbacon.chesschat.lichess;

import com.chrispbacon.chesschat.chat.ChatController;
import com.chrispbacon.chesschat.chat.ChatMessage;
import com.chrispbacon.chesschat.chat.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class LichessService {

    private final String STATS_PREFIX = "$stats ";
    private final String USER_STATS_URL = "https://lichess.org/api/user/";

    private final String SENDER = "LiChessService";

    private static final Logger log = LoggerFactory.getLogger(LichessService.class);


    private final RestTemplate restTemplate;

    public LichessService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean shouldDisplayStats(final ChatMessage chatMessage){
        return chatMessage.getContent().startsWith(STATS_PREFIX);
    }

    public ChatMessage requestStats(ChatMessage chatMessage) {
        String lichessUsername = chatMessage.getContent().substring(STATS_PREFIX.length());
        ResponseEntity<String> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(USER_STATS_URL + lichessUsername, String.class);
        } catch (RestClientException e) {
            log.error("Error while requesting the LiChess stats", e);
            String errorMessage = "The username " + lichessUsername + " does not exist!";
            return new ChatMessage(errorMessage, SENDER, MessageType.CHAT);
        }
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseData = responseEntity.getBody();
            return new ChatMessage(responseData, SENDER, MessageType.CHAT);
        }
        return chatMessage;
    }
}
