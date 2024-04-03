package com.chrispbacon.chesschat.auth;

import static org.mockito.Mockito.*;

import com.chrispbacon.chesschat.config.LogoutService;
import com.chrispbacon.chesschat.model.token.Token;
import com.chrispbacon.chesschat.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class LogoutServiceTest {

  @Mock private TokenRepository tokenRepository;

  @InjectMocks private LogoutService logoutService;

  @Test
  public void logout_withValidAuthHeader_invalidatesTokenAndClearsSecurityContext() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);
    Token storedToken = mock(Token.class);

    when(request.getHeader("Authorization")).thenReturn("Bearer some-jwt-token");
    when(tokenRepository.findByToken("some-jwt-token")).thenReturn(Optional.of(storedToken));

    logoutService.logout(request, response, authentication);

    verify(storedToken).setExpired(true);
    verify(storedToken).setRevoked(true);
    verify(tokenRepository).save(storedToken);
  }

  @Test
  public void logout_withMissingAuthHeader_doesNotInteractWithRepository() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    Authentication authentication = mock(Authentication.class);
    when(request.getHeader("Authorization")).thenReturn(null);

    logoutService.logout(request, response, authentication);

    verifyNoInteractions(tokenRepository);
  }
}
