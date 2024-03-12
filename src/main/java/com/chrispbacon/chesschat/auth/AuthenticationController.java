package com.chrispbacon.chesschat.auth;

import com.chrispbacon.chesschat.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthenticationController {

  private final AuthenticationService authenticationService;

  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
    if (authenticationService.checkIfEmailExists(request)) {
      return ResponseEntity.status(409).body("Email already exists!");
    }
    if (authenticationService.checkIfUserNameExists(request)) {
      return ResponseEntity.status(409).body("Username already exists!");
    }
    return ResponseEntity.ok(authenticationService.register(request));
  }

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(authenticationService.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    authenticationService.refreshToken(request, response);
  }

  @PostMapping("/validate-token")
  public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
    return ResponseEntity.ok(jwtService.validateToken(token));
  }
}
