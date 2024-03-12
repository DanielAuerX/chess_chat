package com.chrispbacon.chesschat.lichess;

import com.chrispbacon.chesschat.chat.ChatMessage;
import com.chrispbacon.chesschat.chat.MessageType;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class LichessService {

  private final String STATS_PREFIX = "$stats ";
  private final String CHALLENGE_PREFIX = "$challenge ";
  private final String USER_STATS_URL = "https://lichess.org/api/user/";
  private final String SENDER = "LiChess Bot";

  private static final Logger log = LoggerFactory.getLogger(LichessService.class);

  private final RestTemplate restTemplate;

  public LichessService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public boolean shouldDisplayStats(final ChatMessage chatMessage) {
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
      final String responseData = extractStats(responseEntity);
      return new ChatMessage(responseData, SENDER, MessageType.CHAT);
    }
    return chatMessage;
  }

  private String extractStats(ResponseEntity<String> responseEntity) {
    String responseData = responseEntity.getBody();
    if (responseData != null && !responseData.isEmpty()) {
      try {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(responseData).getAsJsonObject();
        return formatStats(jsonObject);
      } catch (Exception e) {
        log.error("Error while parsing LiChess stats response", e);
      }
    }
    return "Unable to parse LiChess stats response.";
  }

  private String formatStats(final JsonObject jsonObject) {
    final String username = jsonObject.get("username").getAsString();
    final JsonObject perfs = jsonObject.getAsJsonObject("perfs");
    final String blitzGames = perfs.getAsJsonObject("blitz").get("games").getAsString();
    final String blitzRating = perfs.getAsJsonObject("blitz").get("rating").getAsString();
    final String bulletGames = perfs.getAsJsonObject("bullet").get("games").getAsString();
    final String bulletRating = perfs.getAsJsonObject("bullet").get("rating").getAsString();
    final String rapidGames = perfs.getAsJsonObject("rapid").get("games").getAsString();
    final String rapidRating = perfs.getAsJsonObject("rapid").get("rating").getAsString();
    final String profileUrl = jsonObject.get("url").getAsString();
    return """
            LiChess Stats for %s:
            + Blitz
            --- Games:  %s
            --- Rating: %s
            + Bullet
            --- Games:  %s
            --- Rating: %s
            + Rapid
            --- Games:  %s
            --- Rating: %s
            Profile URL: %s
            """
        .formatted(
            username,
            blitzGames,
            blitzRating,
            bulletGames,
            bulletRating,
            rapidGames,
            rapidRating,
            profileUrl);
  }

  public boolean shouldChallenge(final ChatMessage chatMessage) {
    return chatMessage.getContent().startsWith(CHALLENGE_PREFIX);
  }

  public ChatMessage challenge(ChatMessage chatMessage) {
    String lichessUsername = chatMessage.getContent().substring(CHALLENGE_PREFIX.length());
    ResponseEntity<String> responseEntity;
    try {
      responseEntity = restTemplate.getForEntity(USER_STATS_URL + lichessUsername, String.class);
    } catch (RestClientException e) {
      log.error("Error while requesting the LiChess stats", e);
      String errorMessage = "The username " + lichessUsername + " does not exist!";
      return new ChatMessage(errorMessage, SENDER, MessageType.CHAT);
    }
    if (responseEntity.getStatusCode().is2xxSuccessful()) {
      return new ChatMessage(lichessUsername, SENDER, MessageType.LINK);
    }
    return chatMessage;
  }
}
