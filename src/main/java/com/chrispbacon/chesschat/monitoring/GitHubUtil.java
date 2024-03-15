package com.chrispbacon.chesschat.monitoring;

import com.chrispbacon.chesschat.config.JwtService;
import com.chrispbacon.chesschat.model.user.Student;
import com.chrispbacon.chesschat.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitHubUtil {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(GitHubUtil.class);

  @Value("${chat.ticket.url}")
  private String GITHUB_API_URL;

  @Value("${chat.ticket.owner}")
  private String OWNER;

  @Value("${chat.ticket.repo}")
  private String REPO;

  @Value("${chat.ticket.token}")
  private String TOKEN;

  public GitHubUtil(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  public HttpStatusCode createGitHubIssue(TicketDto ticketDto) {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(TOKEN);
    headers.setContentType(MediaType.APPLICATION_JSON);

    RestTemplate restTemplate = new RestTemplate();

    String url = GITHUB_API_URL.replace("{owner}", OWNER).replace("{repo}", REPO);

    String jsonBody =
        String.format(
            "{\"title\": \"%s\", \"body\": \"%s\"}", compileTitle(ticketDto), ticketDto.body());
    log.info(jsonBody);

    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody, headers);

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

    return response.getStatusCode();
  }

  private String compileTitle(TicketDto ticketDto) {
    return "Support ticket: " + ticketDto.title();
  }

  private Student getUser(HttpServletRequest request) {
    String username = getUsername(request);
    return userRepository.findByUserName(username).orElseThrow();
  }

  private String getUsername(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken = authHeader.substring(7);
    return jwtService.extractUsername(refreshToken);
  }
}
