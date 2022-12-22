package com.matheuzmendez.keycloakspi.user.service.impl;

import com.matheuzmendez.keycloakspi.user.service.AuthenticateUserProviderService;
import com.matheuzmendez.keycloakspi.user.service.FindUserProviderService;
import com.matheuzmendez.keycloakspi.user.service.UserDto;
import com.matheuzmendez.keycloakspi.user.service.UserMock;

public class UserMockImpl implements UserMock {

	public UserDto obter(String url, String username) {
		FindUserProviderService findUserProviderService = new FindUserProviderService(url);
		return findUserProviderService.callConsultaUsuario(username);
	}

	public boolean autenticar(String url, String username, String password) {
		AuthenticateUserProviderService authenticateUserProviderService = new AuthenticateUserProviderService(url);
		return authenticateUserProviderService.callAutenticaUsuario(username, password);
	}
	
	
}
