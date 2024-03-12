package com.chrispbacon.chesschat.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {

	private String email;
	private String userName;
	private String password;

}
