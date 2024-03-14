package com.chrispbacon.chesschat.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class MonitorService {

  private static final Logger log = LoggerFactory.getLogger(MonitorService.class);

  private final GitHubUtil gitHubUtil;

  public MonitorService(GitHubUtil gitHubUtil) {
    this.gitHubUtil = gitHubUtil;
  }

  public boolean sendAlert(Exception exception) {
    HttpStatusCode responseCode =
        gitHubUtil.createGitHubIssue(
            new TicketDto("An exception occurred! Please fix!", exception.getMessage()));
    if (responseCode != HttpStatus.CREATED) {
      log.error("Failed to create issue. Status code: {}", responseCode);
      return false;
    }
    return true;
  }
}
