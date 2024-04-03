package com.chrispbacon.chesschat.chat;

public class DirectMessageRequest {

  private String sender;
  private String recipient;
  private String content;

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  private MessageType type;

  public DirectMessageRequest() {}

  public DirectMessageRequest(String sender, String recipient, String content) {
    this.sender = sender;
    this.recipient = recipient;
    this.content = content;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient(String recipient) {
    this.recipient = recipient;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "DirectMessageRequest{"
        + "recipient='"
        + recipient
        + '\''
        + ", content='"
        + content
        + '\''
        + '}';
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }
}
