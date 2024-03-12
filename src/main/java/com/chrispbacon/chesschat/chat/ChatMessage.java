package com.chrispbacon.chesschat.chat;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ChatMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID messageId;

  private String content;
  private String sender;
  private MessageType type;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime timestamp;

  public ChatMessage() {}

  public ChatMessage(String content, String sender, MessageType type) {
    this.content = content;
    this.sender = sender;
    this.type = type;
  }

  public static ChatMessageBuilder builder() {
    return new ChatMessageBuilder();
  }

  public String getContent() {
    return this.content;
  }

  public String getSender() {
    return this.sender;
  }

  public MessageType getType() {
    return this.type;
  }

  public UUID getMessageId() {
    return messageId;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public static class ChatMessageBuilder {
    private String content;
    private String sender;
    private MessageType type;

    ChatMessageBuilder() {}

    public ChatMessageBuilder content(String content) {
      this.content = content;
      return this;
    }

    public ChatMessageBuilder sender(String sender) {
      this.sender = sender;
      return this;
    }

    public ChatMessageBuilder type(MessageType type) {
      this.type = type;
      return this;
    }

    public ChatMessage build() {
      return new ChatMessage(this.content, this.sender, this.type);
    }

    public String toString() {
      return "ChatMessage.ChatMessageBuilder(content="
          + this.content
          + ", sender="
          + this.sender
          + ", type="
          + this.type
          + ")";
    }
  }
}
