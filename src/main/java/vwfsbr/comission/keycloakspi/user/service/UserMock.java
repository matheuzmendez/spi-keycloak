package vwfsbr.comission.keycloakspi.user.service;

import vwfsbr.comission.keycloakspi.user.service.utils.ResponseAuthenticate;

public interface UserMock {
	UserDto obter(String url, String parametro, String username);

	ResponseAuthenticate autenticar(String url, String parametro, String username, String password);
}
