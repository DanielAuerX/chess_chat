package com.chrispbacon.chesschat.model.user;

import lombok.Getter;

@Getter
public class UserDto {

	private final String email;
	private final String userName;
	private final String firstName;
	private final String lastName;
	private final Role role;

	public UserDto(Student student) {
		this.email = student.getEmail();
		this.userName = student.getUsername();
		this.firstName = student.getFirstName();
		this.lastName = student.getLastName();
		this.role = student.getRole();
	}
}
