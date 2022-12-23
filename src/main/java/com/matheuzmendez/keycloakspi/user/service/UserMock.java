package com.matheuzmendez.keycloakspi.user.service;

public interface UserMock {
	UserDto obter(String url, String parametro, String username);

	boolean autenticar(String url, String parametro, String username, String password);
}
