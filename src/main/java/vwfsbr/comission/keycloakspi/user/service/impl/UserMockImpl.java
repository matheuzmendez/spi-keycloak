package vwfsbr.comission.keycloakspi.user.service.impl;

import vwfsbr.comission.keycloakspi.user.service.AuthenticateUserProviderService;
import vwfsbr.comission.keycloakspi.user.service.FindUserProviderService;
import vwfsbr.comission.keycloakspi.user.service.UserDto;
import vwfsbr.comission.keycloakspi.user.service.UserMock;
import vwfsbr.comission.keycloakspi.user.service.utils.ResponseAuthenticate;

public class UserMockImpl implements UserMock {

	public UserDto obter(String url, String parametro, String username) {

		FindUserProviderService findUserProviderService = new FindUserProviderService(url, parametro);
		UserDto userDto = findUserProviderService.consultaUsuario(username);

		return (userDto != null) ? userDto : null;
	}

	public ResponseAuthenticate autenticar(String url, String parametro, String username, String password) {
		AuthenticateUserProviderService authenticateUserProviderService = new AuthenticateUserProviderService(url,
				parametro);
		return authenticateUserProviderService.callAutenticaUsuario(username, password);
	}

}
