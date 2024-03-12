package com.chrispbacon.chesschat.chat;

import java.util.HashSet;
import java.util.Set;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveUserService {

  private final Set<String> activeUsers = new HashSet<>();
  private final SimpMessagingTemplate messagingTemplate;

  public ActiveUserService(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public Set<String> getActiveUsers() {
    return activeUsers;
  }

  public void addUser(final String username) {
    activeUsers.add(username);
    broadcastActiveUsers();
  }

  public void removeUser(final String username) {
    activeUsers.remove(username);
    broadcastActiveUsers();
  }

  private void broadcastActiveUsers() {
    messagingTemplate.convertAndSend("/topic/userlist", new HashSet<>(activeUsers));
  }
}
