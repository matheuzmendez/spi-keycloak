package com.matheuzmendez.keycloakspi.user.service.impl;

import com.matheuzmendez.keycloakspi.user.service.AuthenticateUserProviderService;
import com.matheuzmendez.keycloakspi.user.service.FindUserProviderService;
import com.matheuzmendez.keycloakspi.user.service.UserDto;
import com.matheuzmendez.keycloakspi.user.service.UserMock;

public class UserMockImpl implements UserMock {

	public UserDto obter(String url, String parametro, String username) {

		FindUserProviderService findUserProviderService = new FindUserProviderService(url, parametro);
		UserDto userDto = findUserProviderService.callConsultaUsuario(username);

		return (userDto != null) ? userDto : null;
	}

	public boolean autenticar(String url, String parametro, String username, String password) {
		AuthenticateUserProviderService authenticateUserProviderService = new AuthenticateUserProviderService(url,
				parametro);
		return authenticateUserProviderService.callAutenticaUsuario(username, password);
	}

}
