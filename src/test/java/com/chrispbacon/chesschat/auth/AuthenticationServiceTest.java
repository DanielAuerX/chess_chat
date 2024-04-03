package com.chrispbacon.chesschat.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chrispbacon.chesschat.config.JwtService;
import com.chrispbacon.chesschat.model.token.Token;
import com.chrispbacon.chesschat.model.user.Role;
import com.chrispbacon.chesschat.model.user.Student;
import com.chrispbacon.chesschat.repository.TokenRepository;
import com.chrispbacon.chesschat.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private TokenRepository tokenRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private AuthenticationManager authenticationManager;
  @InjectMocks private AuthenticationService authenticationService;

  @Test
  void register_ShouldReturnCorrectAuthenticationResponse() {
    RegisterRequest request = new RegisterRequest("test@email.com", "testName", "xyz");
    when(passwordEncoder.encode(request.getPassword())).thenReturn("xyz");
    when(userRepository.save(any(Student.class)))
        .thenReturn(
            new Student(
                UUID.randomUUID(),
                request.getEmail(),
                request.getUserName(),
                request.getPassword(),
                "",
                "",
                Role.USER));
    String token = "abcdefgh";
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
    String refreshToken = "1234567890";
    when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);
    when(tokenRepository.save(any(Token.class))).thenReturn(null);

    AuthenticationResponse result = authenticationService.register(request);

    assertEquals(token, result.getAccessToken());
    assertEquals(refreshToken, result.getRefreshToken());
    assertNotNull(result.getUser());
    assertEquals(request.getUserName(), result.getUser().getUserName());
    verify(userRepository).save(any(Student.class));
  }

  @Test
  void authenticate_ShouldReturnCorrectAuthenticationResponse() {
    Student student =
        new Student(
            UUID.randomUUID(), "test@email.com", "maxCool", "123", "hans", "müller", Role.USER);
    AuthenticationRequest authenticationRequest =
        new AuthenticationRequest(student.getUsername(), student.getPassword());
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(null);
    when(userRepository.findByUserName(authenticationRequest.getUserName()))
        .thenReturn(Optional.of(student));
    String token = "abcdefgh";
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn(token);
    String refreshToken = "1234567890";
    when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn(refreshToken);

    AuthenticationResponse result = authenticationService.authenticate(authenticationRequest);

    assertEquals(token, result.getAccessToken());
    assertEquals(refreshToken, result.getRefreshToken());
    assertNotNull(result.getUser());
    assertEquals(authenticationRequest.getUserName(), result.getUser().getUserName());
    verify(tokenRepository).save(any(Token.class));
  }
}
