package com.matheuzmendez.keycloakspi.user.service;

public interface UserMock {
	UserDto obter(String username);

	boolean autenticar(String url, String username, String password);
}
