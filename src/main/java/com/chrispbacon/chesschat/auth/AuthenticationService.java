package com.chrispbacon.chesschat.auth;

import com.chrispbacon.chesschat.config.JwtService;
import com.chrispbacon.chesschat.model.token.Token;
import com.chrispbacon.chesschat.model.token.TokenType;
import com.chrispbacon.chesschat.model.user.Role;
import com.chrispbacon.chesschat.model.user.Student;
import com.chrispbacon.chesschat.model.user.UserDto;
import com.chrispbacon.chesschat.repository.TokenRepository;
import com.chrispbacon.chesschat.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

  public AuthenticationResponse register(RegisterRequest request) {
    log.info("REGISTER: Username: " + request.getUserName());
    var user =
        Student.builder()
            .id(UUID.randomUUID())
            .userName(request.getUserName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(Role.USER)
            .build();
    var savedUser = userRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    saveUserToken(savedUser, jwtToken);
    return generateAuthenticationResponse(savedUser, jwtToken, refreshToken);
  }

  public boolean checkIfEmailExists(RegisterRequest request) {
    return userRepository.findByEmail(request.getEmail()).isPresent();
  }

  public boolean checkIfUserNameExists(RegisterRequest request) {
    return userRepository.findByUserName(request.getUserName()).isPresent();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    log.info("Trying to find user in h2 database");
    log.info(request.getUserName());
    log.info(request.getPassword());

    authenticationManager
        .authenticate( // compares hashed db pw with hashed provided pw; BadCredentialsException
            // handled
            new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
    System.out.println("Line 75");
    var user = userRepository.findByUserName(request.getUserName()).orElseThrow();
    log.info(user.toString());
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    revokeAllUserTokens(user);
    saveUserToken(user, jwtToken);
    return generateAuthenticationResponse(user, jwtToken, refreshToken);
  }

  private AuthenticationResponse generateAuthenticationResponse(
      Student user, String jwtToken, String refreshToken) {
    UserDto userDto = new UserDto(user);
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
        .refreshToken(refreshToken)
        .user(userDto)
        .build();
  }

  private void saveUserToken(Student student, String jwtToken) {
    var token =
        Token.builder()
            .student(student)
            .token(jwtToken)
            .tokenType(TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
    tokenRepository.save(token);
  }

  private void revokeAllUserTokens(Student student) {
    var validUserTokens = tokenRepository.findAllValidTokenByUser(student.getId());
    if (validUserTokens.isEmpty()) return;
    validUserTokens.forEach(
        token -> {
          token.setExpired(true);
          token.setRevoked(true);
        });
    tokenRepository.saveAll(validUserTokens);
  }

  public void refreshToken(
      HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
      throws IOException {
    final String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userName;
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return;
    }
    refreshToken = authHeader.substring(7);
    userName = jwtService.extractUsername(refreshToken);
    if (userName != null) {
      var user = this.userRepository.findByUserName(userName).orElseThrow();
      if (jwtService.isTokenValid(refreshToken, user)) {
        var accessToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        var authResponse =
            AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        new ObjectMapper().writeValue(httpServletResponse.getOutputStream(), authResponse);
      }
    }
  }
}
