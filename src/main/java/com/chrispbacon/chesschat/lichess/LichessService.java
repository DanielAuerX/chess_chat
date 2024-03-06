package com.chrispbacon.chesschat.lichess;

import com.chrispbacon.chesschat.chat.ChatMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LichessService {

    private final String STATS_PREFIX = "$stats ";
    private final String USER_STATS_URL = "https://lichess.org/api/user/";

    //private final RestTemplate restTemplate;

    //public LichessService(RestTemplate restTemplate) {
    //    this.restTemplate = restTemplate;
    //}

    public boolean shouldDisplayStats(final ChatMessage chatMessage){
        return chatMessage.getContent().startsWith(STATS_PREFIX);
    }

    public ChatMessage requestStats(ChatMessage chatMessage){
        String lichessUsername = chatMessage.getContent().substring(STATS_PREFIX.length());
        //ResponseEntity<String> responseEntity = restTemplate.getForEntity(USER_STATS_URL, String.class);
        return null;
    }
}
