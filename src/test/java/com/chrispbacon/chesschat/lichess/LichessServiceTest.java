package com.chrispbacon.chesschat.lichess;

import static org.junit.jupiter.api.Assertions.*;

import com.chrispbacon.chesschat.chat.ChatMessage;
import com.chrispbacon.chesschat.chat.MessageType;
import org.junit.jupiter.api.Test;

class LichessServiceTest {

  @Test
  void shouldDisplayStats() {
    ChatMessage chatMessage = new ChatMessage("$stats timo", "peter", MessageType.CHAT);
    LichessService lichessService = new LichessService(null);

    boolean result = lichessService.shouldDisplayStats(chatMessage);

    assertTrue(result);
  }

  @Test
  void name() {
    final String STATS_PREFIX = "$stats ";
    String lichessUsername = "$stats timo".substring(STATS_PREFIX.length());
    assertEquals("timo", lichessUsername);
  }
}
