package com.matheuzmendez.keycloakspi.user.service;

public class UserDto {

	private String username;
	private String email;
	private String firstname;
	private String lastname;
	private String password;

	public UserDto(String username, String email, String firstname, String lastname, String password) {
		this.username = username;
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getPassword() {
		return password;
	}
}
