package vwfsbr.comission.keycloakspi.user.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vwfsbr.comission.keycloakspi.provider.ExternalUserStorageProvider;
import vwfsbr.comission.keycloakspi.user.service.AuthenticateUserProviderService;
import vwfsbr.comission.keycloakspi.user.service.FindUserProviderService;
import vwfsbr.comission.keycloakspi.user.service.UserDto;
import vwfsbr.comission.keycloakspi.user.service.UserMock;

public class UserMockImpl implements UserMock {
	private Logger log = LoggerFactory.getLogger(ExternalUserStorageProvider.class);

	public UserDto obter(String url, String parametro, String username) {

		FindUserProviderService findUserProviderService = new FindUserProviderService(url, parametro);
		UserDto userDto = findUserProviderService.consultaUsuario(username);
		
		if (userDto != null && userDto.getRole() == "") {
			log.info("Entrei");
			
			FindUserProviderService findUserProviderServiceDealer = new FindUserProviderService(url, parametro);
			userDto = findUserProviderServiceDealer.consultaUsuarioDealer(username, userDto.getCodDealer());
			log.info("Role: " + userDto.getRole());
		}
		
		return (userDto != null) ? userDto : null;
	}

	public boolean autenticar(String url, String parametro, String username, String password) {
		AuthenticateUserProviderService authenticateUserProviderService = new AuthenticateUserProviderService(url,
				parametro);
		return authenticateUserProviderService.callAutenticaUsuario(username, password);
	}

}
                                                                                                                                                