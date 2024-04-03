package com.chrispbacon.chesschat.lichess;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.chrispbacon.chesschat.chat.ChatMessage;
import com.chrispbacon.chesschat.chat.MessageType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@ExtendWith(MockitoExtension.class)
class LichessServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @Test
  void shouldDisplayStats() {
    ChatMessage chatMessage = new ChatMessage("$stats timo", "peter", MessageType.CHAT);
    LichessService lichessService = new LichessService(null);

    boolean result = lichessService.shouldDisplayStats(chatMessage);

    assertTrue(result);
  }

  @Test
  void shouldNotDisplayStats() {
    ChatMessage chatMessage = new ChatMessage("some other message", "", MessageType.CHAT);
    LichessService lichessService = new LichessService(null);

    boolean result = lichessService.shouldDisplayStats(chatMessage);

    assertFalse(result);
  }

  @Test
  void requestStats_Successful() {
    ChatMessage chatMessage = new ChatMessage("$stats testuser", "", MessageType.CHAT);
    ResponseEntity<String> responseEntity =
        new ResponseEntity<>(
            "{\"id\":\"testuser\",\"username\":\"testuser\",\"perfs\":{\"blitz\":{\"games\":0,\"rating\":1500,\"rd\":500,\"prog\":0,\"prov\":true},\"bullet\":{\"games\":0,\"rating\":1500,\"rd\":500,\"prog\":0,\"prov\":true},\"correspondence\":{\"games\":0,\"rating\":1500,\"rd\":500,\"prog\":0,\"prov\":true},\"classical\":{\"games\":0,\"rating\":1500,\"rd\":500,\"prog\":0,\"prov\":true},\"rapid\":{\"games\":0,\"rating\":1500,\"rd\":500,\"prog\":0,\"prov\":true}},\"createdAt\":1290872116000,\"playTime\":{\"total\":0,\"tv\":0},\"url\":\"https://lichess.org/@/testuser\",\"count\":{\"all\":0,\"rated\":0,\"ai\":0,\"draw\":0,\"drawH\":0,\"loss\":0,\"lossH\":0,\"win\":0,\"winH\":0,\"bookmark\":0,\"playing\":0,\"import\":0,\"me\":0},\"followable\":true,\"following\":false,\"blocking\":false,\"followsYou\":false}",
            HttpStatus.OK);
    when(restTemplate.getForEntity("https://lichess.org/api/user/testuser", String.class)).thenReturn(responseEntity);
    LichessService lichessService = new LichessService(restTemplate);


    ChatMessage result = lichessService.requestStats(chatMessage);

    assertNotNull(result);
    assertEquals(
        """
            LiChess Stats for testuser:
            + Blitz
            --- Games:  0
            --- Rating: 1500
            + Bullet
            --- Games:  0
            --- Rating: 1500
            + Rapid
            --- Games:  0
            --- Rating: 1500
            Profile URL: https://lichess.org/@/testuser
            """,
        result.getContent());
    assertEquals("LiChess Bot", result.getSender());
    assertEquals(MessageType.CHAT, result.getType());
  }


  @Test
  void requestStats_UsernameDoesNotExist() {
    String user = "testuser";
    ChatMessage chatMessage = new ChatMessage("$stats " + user, "", MessageType.CHAT);
    when(restTemplate.getForEntity("https://lichess.org/api/user/" + user, String.class)).thenThrow(RestClientException.class);
    LichessService lichessService = new LichessService(restTemplate);


    ChatMessage result = lichessService.requestStats(chatMessage);

    assertNotNull(result);
    assertEquals("The username " + user + " does not exist!", result.getContent());
    assertEquals("LiChess Bot", result.getSender());
    assertEquals(MessageType.CHAT, result.getType());
  }


  @Test
  void shouldChallenge() {
    ChatMessage chatMessage = new ChatMessage("$challenge username", "", MessageType.CHAT);
    LichessService lichessService = new LichessService(restTemplate);

    boolean result = lichessService.shouldChallenge(chatMessage);

    assertTrue(result);
  }

  @Test
  void shouldNotChallenge() {
    ChatMessage chatMessage = new ChatMessage("some other message", "", MessageType.CHAT);
    LichessService lichessService = new LichessService(restTemplate);

    boolean result = lichessService.shouldChallenge(chatMessage);

    assertFalse(result);
  }

  @Test
  void challenge_Successful() {
    String user = "testuser";
    ChatMessage chatMessage = new ChatMessage("$challenge " + user, "", MessageType.CHAT);
    ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"username\":\"" + user + "\"}", HttpStatus.OK);
    when(restTemplate.getForEntity("https://lichess.org/api/user/" + user, String.class)).thenReturn(responseEntity);
    LichessService lichessService = new LichessService(restTemplate);


    ChatMessage result = lichessService.challenge(chatMessage);

    assertNotNull(result);

    assertEquals(user, result.getContent());
    assertEquals(MessageType.LINK, result.getType());
    assertEquals("LiChess Bot", result.getSender());
  }

  @Test
  void challenge_NotSuccessful() {
    String user = "testuser";
    ChatMessage chatMessage = new ChatMessage("$challenge " + user, "", MessageType.CHAT);
    when(restTemplate.getForEntity("https://lichess.org/api/user/" + user, String.class)).thenThrow(RestClientException.class);
    LichessService lichessService = new LichessService(restTemplate);


    ChatMessage result = lichessService.challenge(chatMessage);

    assertNotNull(result);
    assertEquals("The username " + user + " does not exist!", result.getContent());
    assertEquals(MessageType.CHAT, result.getType());
    assertEquals("LiChess Bot", result.getSender());
  }
}
